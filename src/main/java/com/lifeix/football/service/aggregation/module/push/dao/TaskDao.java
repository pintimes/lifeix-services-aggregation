package com.lifeix.football.service.aggregation.module.push.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.lifeix.football.service.aggregation.module.push.po.MsgTaskPO;

@Repository
public class TaskDao {

	@Autowired
	private MongoTemplate template;
	
	public void clear(){
		template.dropCollection(MsgTaskPO.class);
	}

	public void insert(MsgTaskPO po) {
		template.insert(po);
	}
	
	public void insertAll(List<MsgTaskPO> pos) {
		template.insertAll(pos);
	}

	public void updateStatusOnFail(String id, String status, String reason) {
		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(id));
		Update update = new Update();
		update.set("status", status);
		update.set("reason", reason);
		template.updateFirst(query, update, MsgTaskPO.class);
	}

	public void updateStatusOnSucc(String id, String status, String response) {
		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(id));
		Update update = new Update();
		update.set("status", status);
		update.set("response", response);
		template.updateFirst(query, update, MsgTaskPO.class);
	}

}
