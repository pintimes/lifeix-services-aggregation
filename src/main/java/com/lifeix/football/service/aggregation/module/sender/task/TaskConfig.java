package com.lifeix.football.service.aggregation.module.sender.task;

import java.util.ArrayList;
import java.util.List;

import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Configuration
public class TaskConfig {

    @Autowired
    private EmailSendTask emailSendTask;

    @Autowired
    private SmsSendTask smsSendTask;

    @Bean
    public MethodInvokingJobDetailFactoryBean emailJobDetail() {
	MethodInvokingJobDetailFactoryBean bean = new MethodInvokingJobDetailFactoryBean();
	bean.setTargetObject(emailSendTask);
	bean.setTargetMethod("start");
	bean.setConcurrent(false);
	return bean;
    }

    @Bean
    public MethodInvokingJobDetailFactoryBean smsJobDetail() {
	MethodInvokingJobDetailFactoryBean bean = new MethodInvokingJobDetailFactoryBean();
	bean.setTargetObject(smsSendTask);
	bean.setTargetMethod("start");
	bean.setConcurrent(false);
	return bean;
    }

    @Bean
    public CronTriggerFactoryBean emailJobTrigger() {
	CronTriggerFactoryBean tigger = new CronTriggerFactoryBean();
	tigger.setJobDetail(emailJobDetail().getObject());
	tigger.setCronExpression("0/5 * * * * ? ");// 每5秒执行一次
	return tigger;
    }

    @Bean
    public CronTriggerFactoryBean smsJobTrigger() {
	CronTriggerFactoryBean tigger = new CronTriggerFactoryBean();
	tigger.setJobDetail(smsJobDetail().getObject());
	tigger.setCronExpression("0/5 * * * * ? ");// 每5秒执行一次
	return tigger;
    }

    @Bean
    public SchedulerFactoryBean schedulerFactory(CronTriggerFactoryBean[] jobTrigger) {
	SchedulerFactoryBean bean = new SchedulerFactoryBean();
	if (jobTrigger != null && jobTrigger.length > 0) {
	    List<Trigger> triggers = new ArrayList<Trigger>(jobTrigger.length);
	    for (CronTriggerFactoryBean trigger : jobTrigger) {
		triggers.add(trigger.getObject());
	    }
	    bean.setTriggers(triggers.toArray(new Trigger[triggers.size()]));
	}
	return bean;
    }

}
