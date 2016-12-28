package org.seckill.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.seckill.entity.Seckill;

public interface SeckillDao {
	
	//减库存
	int reduceNumber(@Param("seckillId")long seckillId,@Param("killTime")Date killTime);
	
	//根据id查询商品信息
	Seckill queryById(long seckillId);
	
	//根据偏移量查询秒杀商品列表
	List<Seckill> queryAll(@Param("offset")int offset, @Param("limit")int limit);
	
	//使用存储过程执行秒杀
	void killByProcedure(Map<String,Object> paramMap);

	
}
