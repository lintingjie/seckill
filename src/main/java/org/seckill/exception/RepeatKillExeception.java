package org.seckill.exception;

/**
 * 重复秒杀异常（运行期异常）
 * @author lintingjie
 *
 */
public class RepeatKillExeception extends SeckillException{

	public RepeatKillExeception(String message) {
		super(message);
	}
	
	public RepeatKillExeception(String message, Throwable cause) {
		super(message,cause);
	}
	
}
