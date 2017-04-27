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
import com.aliyuncs.dm.model.v20151123.SingleSendMailRequest;
import com.aliyuncs.dm.model.v20151123.SingleSendMailResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.lifeix.football.service.aggregation.module.sender.common.Constants.EmailType;
import com.lifeix.football.service.aggregation.module.sender.common.Constants.SendFlag;
import com.lifeix.football.service.aggregation.module.sender.module.email.dao.EmailDao;
import com.lifeix.football.service.aggregation.module.sender.module.email.dao.EmailTaskLockDao;
import com.lifeix.football.service.aggregation.module.sender.module.email.po.EmailPO;

@Component
public class EmailSendTask {

    private Logger LOG = LoggerFactory.getLogger(EmailSendTask.class);

    private static final int EMAIL_LIMIT = 2000;

    private static final int EMAIL_MAX_THREAD_POOL_SIZE = 200;

    private static final int EMAIL_TASK_EXPIRE = 60;

    private final IClientProfile profile = DefaultProfile.getProfile("cn-beijing", "LTAI65PZCIAjEtsZ",
            "FirWyVHgQ1tDxubXiEf1W3PNsZpcog");

    @Autowired
    private EmailDao emailDao;

    @Autowired
    private EmailTaskLockDao emailTaskLockDao;

    public void start() {
	List<EmailPO> list = null;
	Long startId = null;
	do {
	    list = emailDao.getUnsendedEmais(startId, EMAIL_LIMIT);
	    if (list != null && list.size() > 0) {
		startId = list.get(list.size() - 1).getId();
		ExecutorService service = Executors.newFixedThreadPool(EMAIL_MAX_THREAD_POOL_SIZE);
		for (int j = 0; j < list.size(); j++) {
		    EmailPO emailPO = list.get(j);
		    Long emailId = emailTaskLockDao.addTaskLock(emailPO.getId(), EMAIL_TASK_EXPIRE);
		    if (emailId == null)
			continue;
		    EmailThread thread = new EmailThread(emailPO);
		    service.execute(thread);
		}
		service.shutdown();
		try {
		    service.awaitTermination(EMAIL_TASK_EXPIRE, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
		    LOG.error(e.getMessage(), e);
		}
	    }
	} while (list != null && list.size() > 0);
    }

    class EmailThread implements Runnable {

	private EmailPO emailPO;

	public EmailThread(EmailPO emailPO) {
	    this.emailPO = emailPO;
	}

	@Override
	public void run() {
	    try {
		boolean flag = emailDao.updateSendFlag(emailPO.getId(), SendFlag.SENDING);
		if (flag) {
		    SingleSendMailResponse response = aliSend(emailPO);
		    LOG.info(String.format("emailId=%s, aliRequestId=%s", emailPO.getId(), response.getRequestId()));
		    emailDao.updateSendFlag(emailPO.getId(), SendFlag.SENDED);
		    emailTaskLockDao.releaseTaskLock(emailPO.getId());
		}
	    } catch (Throwable t) {
		LOG.error(String.format("emailId=%s, sendError:%s", emailPO.getId(), t.getMessage()), t);
	    }
	}
    }

    private SingleSendMailResponse aliSend(EmailPO emailPO) throws ServerException, ClientException {
	IAcsClient client = new DefaultAcsClient(profile);
	SingleSendMailRequest request = new SingleSendMailRequest();
	request.setAccountName(emailPO.getFromAddress());
	request.setAddressType(1);
	request.setReplyToAddress(false);
	request.setToAddress(emailPO.getToAddress());
	request.setSubject(emailPO.getSubject());
	if (emailPO.getType() == EmailType.TEXT)
	    request.setTextBody(emailPO.getContent());
	else
	    request.setHtmlBody(emailPO.getContent());
	return client.getAcsResponse(request);
    }

}
