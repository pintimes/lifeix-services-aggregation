package com.lifeix.football.service.aggregation.module.sender.module.email.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.lifeix.football.service.aggregation.module.sender.module.email.po.EmailPO;

@Repository
public class EmailDao {

    @Autowired
    private SqlSession sqlSession;

    public List<EmailPO> getUnsendedEmais(Long startId, int limit) {
	Map<String, Object> params = new HashMap<String, Object>();
	if (startId != null)
	    params.put("startId", startId);
	params.put("limit", limit);
	return sqlSession.selectList("EmailMapper.getUnsendedEmais", params);
    }

    public boolean insertBatch(List<EmailPO> poList) {
	return sqlSession.insert("EmailMapper.insertBatch", poList) > 0;
    }

    public boolean updateSendFlag(Long id, int sendFlag) {
	Map<String, Object> params = new HashMap<String, Object>();
	params.put("id", id);
	params.put("sendFlag", sendFlag);
	return sqlSession.update("EmailMapper.updateSendFlag", params) > 0;
    }
}
