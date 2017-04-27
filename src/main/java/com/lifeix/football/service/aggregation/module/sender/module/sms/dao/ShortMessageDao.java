package com.lifeix.football.service.aggregation.module.sender.module.sms.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.lifeix.football.service.aggregation.module.sender.module.sms.po.ShortMessagePO;

@Repository
public class ShortMessageDao {

    @Autowired
    private SqlSession sqlSession;

    public List<ShortMessagePO> getUnsendedShortMessages(Long startId, int limit) {
	Map<String, Object> params = new HashMap<String, Object>();
	if (startId != null)
	    params.put("startId", startId);
	params.put("limit", limit);
	return sqlSession.selectList("ShortMessageMapper.getUnsendedShortMessages", params);
    }

    public boolean insertBatch(List<ShortMessagePO> poList) {
	return sqlSession.insert("ShortMessageMapper.insertBatch", poList) > 0;
    }

    public boolean updateSendFlag(Long id, int sendFlag) {
	Map<String, Object> params = new HashMap<String, Object>();
	params.put("id", id);
	params.put("sendFlag", sendFlag);
	return sqlSession.update("ShortMessageMapper.updateSendFlag", params) > 0;
    }
}
