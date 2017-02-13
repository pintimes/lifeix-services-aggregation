package com.lifeix.football.service.aggregation.module.push.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.lifeix.football.service.aggregation.module.push.po.MsgPO;

@Repository
public class MsgDao {

	@Autowired
	private MongoTemplate template;

	public void clear() {
		template.dropCollection(MsgPO.class);
	}

	public void insert(MsgPO po) {
		template.insert(po);
	}

	public void updateStatus(String id, String status) {
		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(id));
		Update update = new Update();
		update.set("status", status);
		template.updateFirst(query, update, MsgPO.class);
	}

	public MsgPO findByTitle(String title) {
		Criteria titleCriteria = Criteria.where("title").is(title);
		Query query = new Query().addCriteria(titleCriteria);
		return template.findOne(query, MsgPO.class);
	}

	public MsgPO findById(String id) {
		return template.findById(id, MsgPO.class);
	}

	public boolean exsit(String id) {
		return template.exists(new Query().addCriteria(Criteria.where("id").is(id)), MsgPO.class);
	}

	public List<MsgPO> findAll() {
		return template.findAll(MsgPO.class);
	}

}
