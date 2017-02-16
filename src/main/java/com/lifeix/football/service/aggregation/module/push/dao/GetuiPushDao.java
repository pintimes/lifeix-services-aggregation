package com.lifeix.football.service.aggregation.module.push.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.lifeix.football.service.aggregation.module.push.po.GetuiPushPO;
import com.lifeix.football.service.aggregation.module.push.po.MsgPO;

@Repository
public class GetuiPushDao {
	
	@Autowired
	private MongoTemplate template;

	public GetuiPushPO findById(String id) {
		return template.findById(id, GetuiPushPO.class);
	}

	public void insert(GetuiPushPO po) {
		template.insert(po);
	}

	public void updateStatus(String id, String status) {
		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(id));
		Update update = new Update();
		update.set("status", status);
		template.updateFirst(query, update, GetuiPushPO.class);
	}

	public void updateStatus(String id, String status, String reason) {
		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(id));
		Update update = new Update();
		update.set("status", status);
		update.set("failReason", reason);
		template.updateFirst(query, update, GetuiPushPO.class);
	}

	public void clear() {
		template.dropCollection(GetuiPushPO.class);
	}

}
