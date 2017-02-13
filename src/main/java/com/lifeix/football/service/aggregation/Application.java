package com.lifeix.football.service.aggregation;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableAsync;

import com.lifeix.football.common.ApplicationUtil;

/**
 * 
 * @author zengguangwei
 */
@SpringBootApplication
@EnableAsync
public class Application {

	public static void main(String[] args) {
		ApplicationUtil.run(Application.class, args);
	}

}
