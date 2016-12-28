package org.seckill.dao.cache;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dao.SeckillDao;
import org.seckill.entity.Seckill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class RedisDaoTest {
	private long seckillId = 1001;
	
	@Autowired
	private RedisDao redisDao;
	
	@Autowired
	private SeckillDao seckillDao;
	
	@Test
	public void testRedisDao(){
		Seckill seckill = redisDao.getSeckill(seckillId);
		if(seckill==null){
			seckill = seckillDao.queryById(seckillId);
			if(seckill!=null){
				String result = redisDao.putSeckill(seckill);
				System.out.println(result);
				seckill = redisDao.getSeckill(seckillId);
				System.out.println(seckill);
			}
			
		}
		
	}

}
