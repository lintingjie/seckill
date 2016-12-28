package org.seckill.service;

import java.util.List;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillExeception;
import org.seckill.exception.SeckillException;

/**
 * 业务接口：站在"使用者"角度设计接口 三个方面：粒度，参数，返回类型 （return类型/异常）
 * 
 * @author lintingjie
 *
 */
public interface SeckillService {

	// 查询所有秒杀记录
	List<Seckill> getSeckillList();

	// 查询单个秒杀记录
	Seckill getById(long seckillId);

	// 秒杀开启时输出秒杀接口地址，否则输出系统时间和秒杀时间
	Exposer exportSeckillUrl(long seckillId);

	// 执行秒杀操作
	SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
			throws SeckillException, RepeatKillExeception, SeckillException;

	// 执行秒杀操作by存储过程
	SeckillExecution executeSeckillByProcedure(long seckillId, long userPhone, String md5);
}
