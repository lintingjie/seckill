package org.seckill.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.Seckill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})//告诉JUnit spring配置文件在哪
public class SeckillDaoTest {
	
	//注入Dao实现类依赖
	@Autowired
	private SeckillDao seckillDao;
	
	@Test
	public void testQueryById(){
		long id = 1000;
		Seckill seckill = seckillDao.queryById(id);
		System.out.println(seckill);
	}
	
	@Test
	public void testQueryAll(){
		//java没有保存形参的记录：queryAll(int offset,int limit) --> queryAll(arg0, arg1)
		//所以需要：queryAll(@Param("offset")int offset, @Param("limit")int limit);
		List<Seckill> list = seckillDao.queryAll(0,100);
		for (Seckill seckill:list) {
			System.out.println(seckill);
		}
	}
	
	@Test
	public void testReduceNumber(){
		int updateCount = seckillDao.reduceNumber(1000, new Date());
		System.out.println(updateCount);
	}
	
	

}
