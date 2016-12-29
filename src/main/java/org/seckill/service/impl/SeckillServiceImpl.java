package org.seckill.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.seckill.dao.SeckillDao;
import org.seckill.dao.SuccessKilledDao;
import org.seckill.dao.cache.RedisDao;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessKilled;
import org.seckill.enums.SeckillStateEnum;
import org.seckill.exception.RepeatKillExeception;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

@Service
@Transactional
/*
 * 使用注解控制事务方法的优点：
 */
public class SeckillServiceImpl implements SeckillService{
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private SeckillDao seckillDao;
	@Autowired
	private SuccessKilledDao successKilledDao;
	@Autowired
	private RedisDao redisDao;
	
	//盐值用于混肴md5字符串
	private String salt = "foIHhouUU)~@##U)Nog';AGK;+o)oihjiKG.";

	public List<Seckill> getSeckillList() {
		return seckillDao.queryAll(0, 4);
	}

	public Seckill getById(long seckillId) {
		Seckill seckill = redisDao.getSeckill(seckillId);
		if(seckill==null){
			seckill = seckillDao.queryById(seckillId);
			if(seckill!=null){
				redisDao.putSeckill(seckill);
			}
		}
		return seckillDao.queryById(seckillId);
	}

	public Exposer exportSeckillUrl(long seckillId) {
		//优化点：缓存优化
		//超时的基础上维护一致性
		//1.访问redis
		Seckill seckill = redisDao.getSeckill(seckillId);
		if(seckill==null){
			//2.访问数据库
			seckill = seckillDao.queryById(seckillId);
			if(seckill!=null){
				//3.放入redis
				redisDao.putSeckill(seckill);
			}else{
				return new Exposer(false,seckillId);
			}
		}
//		Seckill seckill = seckillDao.queryById(seckillId);
//		if(seckill==null){
//			return new Exposer(false,seckillId);
//		}
		Date startTime = seckill.getStartTime();
		Date endTime = seckill.getEndTime();
		Date nowTime = new Date();
		if(nowTime.getTime()<startTime.getTime()||nowTime.getTime()>endTime.getTime()){
			return new Exposer(false,seckillId,
					nowTime.getTime(),startTime.getTime(),endTime.getTime());
		}
		
		//转化特定字符串的过程，不可逆
		String md5 = getMD5(seckillId);
		return new Exposer(true,md5,seckillId);
	}
	
	private String getMD5(long seckillId){
		String base = seckillId+"/"+salt;
		String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
		return md5;
	}

	public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
			throws SeckillException, RepeatKillExeception, SeckillException {
		if(md5==null||!md5.equals(getMD5(seckillId))){
			throw new SeckillException("seckill data rewrite");
		}
		//执行秒杀逻辑：减库存+记录购买行为
		try {
			//记录购买行为
			int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
			if(insertCount <= 0 ){
				//重复秒杀
				throw new RepeatKillExeception("seckill repeated");
			}else{
				//减库存,热点商品竞争（高并发点）
				int updateCount = seckillDao.reduceNumber(seckillId, new Date());
				if(updateCount<=0){
					//没有更新到记录,秒杀结束，rollback
					throw new SeckillCloseException("seckill is closed");
				}else{
					//秒杀成功,commit
					SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
					return new SeckillExecution(seckillId,SeckillStateEnum.SUCCESS,successKilled);
				}
			}
			
		} catch(SeckillCloseException e1){
			throw e1;
		} catch(RepeatKillExeception e2){
			throw e2;
		}catch (Exception e) {
			logger.error(e.getMessage(),e);
			//所有编译器异常，转化为运行期异常
			throw new SeckillException("seckill inner error:"+e.getMessage());
		}
	}

	public SeckillExecution executeSeckillByProcedure(long seckillId, long userPhone, String md5){
		if(md5==null||!md5.equals(getMD5(seckillId))){
			return new SeckillExecution(seckillId,SeckillStateEnum.DATA_REWRITE);
		}
		Date killTime = new Date();
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("seckillId", seckillId);
		map.put("phone",userPhone);
		map.put("killTime", killTime);
		map.put("result", null);
		//执行存储过程
		try {
			seckillDao.killByProcedure(map);
			int result = MapUtils.getInteger(map, "result", -2);//result默认为-2
			if(result==1){
				//秒杀成功
				SuccessKilled sk = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
				return new SeckillExecution(seckillId,SeckillStateEnum.SUCCESS,sk);
			}else{
				return new SeckillExecution(seckillId,SeckillStateEnum.stateOf(result));
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return new SeckillExecution(seckillId,SeckillStateEnum.INNER_ERROR);
		}
	}

}
