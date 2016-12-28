package org.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.SuccessKilled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})//告诉JUnit spring配置文件在哪
public class SuccessKilledDaoTest {
	
	@Autowired
	SuccessKilledDao successKilledDao;
	
	@Test
	public void testInsertSuccessKilled(){
		int insertCount = successKilledDao.insertSuccessKilled(1001, 15764210366L);
		System.out.println(insertCount);
	}
	
	@Test
	public void testQueryByIdWithSeckill(){
		SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(1000, 15764210366L);
		System.out.println(successKilled);
		System.out.println(successKilled.getSeckill());
	}


}
