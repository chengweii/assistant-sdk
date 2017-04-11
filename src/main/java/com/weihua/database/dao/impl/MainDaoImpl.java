package com.weihua.database.dao.impl;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.weihua.database.dao.MainDao;

public class MainDaoImpl extends BaseDao implements MainDao {

	private static Logger LOGGER = Logger.getLogger(MainDaoImpl.class);

	@Override
	public List<Map<String, Object>> findAssistantList() {
		String sql = "select * from assistant_list";
		List<Map<String, Object>> result = dBHelper.queryMapList(LOGGER, sql);
		return result;
	}

	@Override
	public Map<String, Object> findAssistantById(Integer id) {
		String sql = "select * from assistant_list where id=?";
		Map<String, Object> result = dBHelper.queryMap(LOGGER, sql, id);
		return result;
	}

	@Override
	public List<Map<String, Object>> findAssistantHistory(Integer topCount) {
		String sql = "select * from assistant_history order by create_time desc limit ?";
		List<Map<String, Object>> result = dBHelper.queryMapList(LOGGER, sql, topCount);
		return result;
	}

	@Override
	public int recordAssistantHistory(Object... params) {
		String sql = "insert into assistant_history(assistant_id,request_content,response_content,create_time) values(?,?,?,?)";
		return dBHelper.queryUpdate(LOGGER, sql, params);
	}

}
