package com.weihua.database.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.weihua.database.dao.MainDao;

public class MainDaoImpl extends BaseDao implements MainDao {

	private static Logger LOGGER = Logger.getLogger(MainDaoImpl.class);

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> findAssistantList() {
		String sql = "select * from assistant_list";
		List<Map<String, Object>> result = dBHelper.queryMapList(LOGGER, sql);

		sql = "select * from assistant_related_word";
		List<Map<String, Object>> result1 = dBHelper.queryMapList(LOGGER, sql);
		for (Map<String, Object> item : result) {
			item.put("related_word_list", new ArrayList<Map<String, Object>>());
			for (Map<String, Object> item1 : result1) {
				if (String.valueOf(item.get("id")).equals(String.valueOf(item1.get("assistant_id")))) {
					((List<Map<String, Object>>) item.get("related_word_list")).add(item1);
				}
			}
		}

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

	@Override
	public List<Map<String, Object>> findAssistantByRelatedWordList() {
		String sql = "select * from assistant_related_word";
		List<Map<String, Object>> result = dBHelper.queryMapList(LOGGER, sql);
		return result;
	}

}
