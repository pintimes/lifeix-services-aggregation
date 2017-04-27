package com.lifeix.football.service.aggregation.module.sender.module.sms.dao;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ShortMessageTaskLockDao {

    private static final String SHORT_MESSAGE_TASK_LOCK_PREFIX = "shortMessage:task:lock:";

    @Autowired
    private StringRedisTemplate srt;

    public Long addTaskLock(Long shortMessageId, long expire) {
	String key = SHORT_MESSAGE_TASK_LOCK_PREFIX + shortMessageId;
	BoundValueOperations<String, String> ops = srt.boundValueOps(key);
	boolean flag = ops.setIfAbsent("1");
	if (flag) {
	    ops.expire(expire, TimeUnit.SECONDS);
	    return shortMessageId;
	}
	return null;
    }

    public void releaseTaskLock(Long shortMessageId) {
	String key = SHORT_MESSAGE_TASK_LOCK_PREFIX + shortMessageId;
	srt.delete(key);
    }

}
