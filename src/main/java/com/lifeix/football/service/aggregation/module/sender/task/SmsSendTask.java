package com.lifeix.football.service.aggregation.module.sender.task;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.aliyuncs.sms.model.v20160927.SingleSendSmsRequest;
import com.aliyuncs.sms.model.v20160927.SingleSendSmsResponse;
import com.lifeix.football.service.aggregation.module.sender.common.Constants.SendFlag;
import com.lifeix.football.service.aggregation.module.sender.module.sms.dao.ShortMessageDao;
import com.lifeix.football.service.aggregation.module.sender.module.sms.dao.ShortMessageTaskLockDao;
import com.lifeix.football.service.aggregation.module.sender.module.sms.po.ShortMessagePO;

@Component
public class SmsSendTask {

    private static final Logger LOG = LoggerFactory.getLogger(SmsSendTask.class);

    private static final int SHORT_MESSAGE_LIMIT = 2000;

    private static final int SHORT_MESSAGE_MAX_THREAD_POOL_SIZE = 200;

    private static final int SHORT_MESSAGE_TASK_EXPIRE = 60;

    private static final IClientProfile profile = DefaultProfile.getProfile("cn-beijing", "LTAI65PZCIAjEtsZ",
            "FirWyVHgQ1tDxubXiEf1W3PNsZpcog");

    static {
	try {
	    DefaultProfile.addEndpoint("cn-beijing", "cn-beijing", "Sms", "sms.aliyuncs.com");
	} catch (ClientException e) {
	    LOG.error(e.getMessage(), e);
	}
    }

    @Autowired
    private ShortMessageDao shortMessageDao;

    @Autowired
    private ShortMessageTaskLockDao shortMessageTaskLockDao;

    public void start() {
	List<ShortMessagePO> list = null;
	Long startId = null;
	do {
	    list = shortMessageDao.getUnsendedShortMessages(startId, SHORT_MESSAGE_LIMIT);
	    if (list != null && list.size() > 0) {
		startId = list.get(list.size() - 1).getId();
		ExecutorService service = Executors.newFixedThreadPool(SHORT_MESSAGE_MAX_THREAD_POOL_SIZE);
		for (int j = 0; j < list.size(); j++) {
		    ShortMessagePO shortMessagePO = list.get(j);
		    Long shortMessageId = shortMessageTaskLockDao.addTaskLock(shortMessagePO.getId(),
		            SHORT_MESSAGE_TASK_EXPIRE);
		    if (shortMessageId == null)
			continue;
		    ShortMessageThread thread = new ShortMessageThread(shortMessagePO);
		    service.execute(thread);
		}
		service.shutdown();
		try {
		    service.awaitTermination(SHORT_MESSAGE_TASK_EXPIRE, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
		    LOG.error(e.getMessage(), e);
		}
	    }
	} while (list != null && list.size() > 0);
    }

    class ShortMessageThread implements Runnable {

	private ShortMessagePO shortMessagePO;

	public ShortMessageThread(ShortMessagePO shortMessagePO) {
	    this.shortMessagePO = shortMessagePO;
	}

	@Override
	public void run() {
	    try {
		boolean flag = shortMessageDao.updateSendFlag(shortMessagePO.getId(), SendFlag.SENDING);
		if (flag) {
		    SingleSendSmsResponse response = aliSend(shortMessagePO);
		    LOG.info(String.format("shortMessageId=%s, aliRequestId=%s", shortMessagePO.getId(),
		            response.getRequestId()));
		    shortMessageDao.updateSendFlag(shortMessagePO.getId(), SendFlag.SENDED);
		    shortMessageTaskLockDao.releaseTaskLock(shortMessagePO.getId());
		}
	    } catch (Throwable t) {
		LOG.error(String.format("shortMessageId=%s, sendError:%s", shortMessagePO.getId(), t.getMessage()), t);
	    }
	}
    }

    private SingleSendSmsResponse aliSend(ShortMessagePO shortMessagePO) throws ServerException, ClientException {
	IAcsClient client = new DefaultAcsClient(profile);
	SingleSendSmsRequest request = new SingleSendSmsRequest();
	request.setSignName(shortMessagePO.getSignName());
	request.setTemplateCode(shortMessagePO.getTemplateCode());
	request.setParamString(shortMessagePO.getParamString());
	request.setRecNum(shortMessagePO.getRecNum());
	return client.getAcsResponse(request);
    }
}
