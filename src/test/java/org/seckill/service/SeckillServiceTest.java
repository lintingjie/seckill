package org.seckill.service;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillExeception;
import org.seckill.exception.SeckillException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({
	"classpath:spring/spring-dao.xml",
	"classpath:spring/spring-service.xml"
})
public class SeckillServiceTest {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private SeckillService seckillService;
	
	@Test
	public void testGetById(){
		Seckill seckill = seckillService.getById(1000L);
		logger.info("seckill={}", seckill);
	}
	@Test
	public void testGetSeckillList(){
		List<Seckill> list = seckillService.getSeckillList();
		logger.info("list={}", list);
	}
	
	//集成测试代码完整逻辑，注意代码的可重复性
	@Test
	public void testSeckillLogic(){
		long id = 1001;
		long userPhone = 15764210010L;
		Exposer exposer = seckillService.exportSeckillUrl(id);
		if(exposer.isExposed()){
			logger.info("exposer={}", exposer);
			String md5 = exposer.getMd5();
			try {
				SeckillExecution execution = seckillService.executeSeckill(id, userPhone, md5);
				logger.info("execution={}", execution);
			} catch (RepeatKillExeception e) {
				logger.error(e.getMessage());
			} catch (SeckillException e) {
				logger.error(e.getMessage());
			}
		}else{
			logger.warn("exposer={}",exposer);
		}
		
		
	}
	@Test
	public void testExecuteSeckill(){
		long id = 1000;
		long userPhone = 15764210010L;
		String md5 = "83d3477e08a3a3affcd7e07e78a1d832";
		try {
			SeckillExecution execution = seckillService.executeSeckill(id, userPhone, md5);
			logger.info("execution={}", execution);
		} catch (RepeatKillExeception e) {
			logger.error(e.getMessage());
		} catch (SeckillException e) {
			logger.error(e.getMessage());
		}
	}
	
	@Test
	public void testExecuteSeckillByProcedure(){
		long seckillId = 1001;
		long userPhone = 13556233400L;
		Exposer exposer = seckillService.exportSeckillUrl(seckillId);
		if(exposer.isExposed()){
			String md5 = exposer.getMd5();
			SeckillExecution execution = 
					seckillService.executeSeckillByProcedure(seckillId, userPhone, md5);
			logger.info(execution.getStateInfo());
		}
		
		
	}

}
