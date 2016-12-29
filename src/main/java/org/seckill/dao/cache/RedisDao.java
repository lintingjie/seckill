package org.seckill.dao.cache;

import org.seckill.entity.Seckill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisDao {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private final JedisPool jedisPool;
	
	public RedisDao(String ip, int port){
		jedisPool = new JedisPool(ip,port);
	}
	
	private RuntimeSchema<Seckill> schema = RuntimeSchema.createFrom(Seckill.class);
	
	public Seckill getSeckill(long seckillId){
		try {
			Jedis jedis = jedisPool.getResource();
			try {
				String key = "seckill:"+seckillId;
				//并没有实现序列化
				//get：byte[]->反序列化->Object(Seckill)
				//采用自定义序列化
				//protostuff:pojo
				byte[] bytes = jedis.get(key.getBytes());//对象存在redis中为二进制的
				if(bytes!=null){
					Seckill seckill = schema.newMessage();//空对象
					ProtostuffIOUtil.mergeFrom(bytes, seckill, schema);//seckill被反序列化
					return seckill;
				}
				
			} finally {
				jedis.close();
			}				
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return null;
	}
	
	public String putSeckill(Seckill seckill){
		//set Object(Seckill)->序列化->byte[]
		try {
			Jedis jedis = jedisPool.getResource();
			try {
				String key = "seckill:"+seckill.getSeckillId();
				byte[] bytes = ProtostuffIOUtil.toByteArray(seckill, schema, 
						LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
				//超时缓存
				int timeout = 60 * 60;//一小时。超时时间
				String result = jedis.setex(key.getBytes(), timeout, bytes);//成功返回"ok"
				return result;
			} finally {
				jedis.close();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
		return null;
	}

}
