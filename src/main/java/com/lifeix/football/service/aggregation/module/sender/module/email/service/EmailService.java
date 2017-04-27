package com.lifeix.football.service.aggregation.module.sender.module.email.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lifeix.football.common.exception.IllegalparamException;
import com.lifeix.football.service.aggregation.module.sender.common.Constants.SendFlag;
import com.lifeix.football.service.aggregation.module.sender.model.Email;
import com.lifeix.football.service.aggregation.module.sender.module.email.dao.EmailDao;
import com.lifeix.football.service.aggregation.module.sender.module.email.po.EmailPO;

@Service
public class EmailService {

    @Autowired
    private EmailDao emailDao;

    public void sendEmails(List<Email> emails) {
	if (emails == null || emails.size() == 0)
	    throw new IllegalparamException("emails must have data");
	if (emails.size() >= 200)
	    throw new IllegalparamException("emails' size must be less than 200");
	List<EmailPO> emailPOList = new ArrayList<EmailPO>(emails.size());
	for (Email email : emails) {
	    EmailPO emailPO = new EmailPO();
	    emailPO.setFromAddress(email.getFromAddress());
	    emailPO.setToAddress(email.getToAddress());
	    emailPO.setSubject(email.getSubject());
	    emailPO.setContent(email.getContent());
	    emailPO.setType(email.getType());
	    emailPO.setCreateTime(new Date());
	    emailPO.setSendFlag(SendFlag.UNSENDED);
	    emailPOList.add(emailPO);
	}
	emailDao.insertBatch(emailPOList);
    }
}
