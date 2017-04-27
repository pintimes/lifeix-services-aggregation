package com.lifeix.football.service.aggregation.module.sender.module.email.dao;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class EmailTaskLockDao {

    private static final String EMAIL_TASK_LOCK_PREFIX = "email:task:lock:";

    @Autowired
    private StringRedisTemplate srt;

    public Long addTaskLock(Long emailId, long expire) {
	String key = EMAIL_TASK_LOCK_PREFIX + emailId;
	BoundValueOperations<String, String> ops = srt.boundValueOps(key);
	boolean flag = ops.setIfAbsent("1");
	if (flag) {
	    ops.expire(expire, TimeUnit.SECONDS);
	    return emailId;
	}
	return null;
    }

    public void releaseTaskLock(Long emailId) {
	String key = EMAIL_TASK_LOCK_PREFIX + emailId;
	srt.delete(key);
    }
}
