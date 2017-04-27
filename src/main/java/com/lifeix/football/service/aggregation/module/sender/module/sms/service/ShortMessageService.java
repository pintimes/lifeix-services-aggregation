package com.lifeix.football.service.aggregation.module.sender.module.sms.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lifeix.football.common.exception.IllegalparamException;
import com.lifeix.football.service.aggregation.module.sender.common.Constants.SendFlag;
import com.lifeix.football.service.aggregation.module.sender.model.ShortMessage;
import com.lifeix.football.service.aggregation.module.sender.module.sms.dao.ShortMessageDao;
import com.lifeix.football.service.aggregation.module.sender.module.sms.po.ShortMessagePO;

@Service
public class ShortMessageService {

    @Autowired
    private ShortMessageDao shortMessageDao;

    public void sendShortMessages(List<ShortMessage> shortMessages) {
	if (shortMessages == null || shortMessages.size() == 0)
	    throw new IllegalparamException("shortMessages must have data");
	if (shortMessages.size() > 500)
	    throw new IllegalparamException("short messages' size must be less than 500");
	List<ShortMessagePO> shortMessagePOList = new ArrayList<ShortMessagePO>(shortMessages.size());
	for (ShortMessage shortMessage : shortMessages) {
	    ShortMessagePO shortMessagePO = new ShortMessagePO();
	    shortMessagePO.setSignName(shortMessage.getSignName());
	    shortMessagePO.setTemplateCode(shortMessage.getTemplateCode());
	    shortMessagePO.setParamString(shortMessage.getParamString());
	    shortMessagePO.setRecNum(shortMessage.getRecNum());
	    shortMessagePO.setSendFlag(SendFlag.UNSENDED);
	    shortMessagePOList.add(shortMessagePO);
	}
	shortMessageDao.insertBatch(shortMessagePOList);
    }
}
