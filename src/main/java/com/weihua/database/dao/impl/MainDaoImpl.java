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
		String sql = "select distinct assistant_id,request_content from (select * from assistant_history t where t.assistant_id != '111' and t.service_type='1' order by create_time desc) order by create_time desc limit ?";
		List<Map<String, Object>> result = dBHelper.queryMapList(LOGGER, sql, topCount);
		return result;
	}

	@Override
	public int recordAssistantHistory(Object... params) {
		String sql = "insert into assistant_history(assistant_id,request_content,service_type,create_time) values(?,?,?,?)";
		return dBHelper.queryUpdate(LOGGER, sql, params);
	}

	@Override
	public List<Map<String, Object>> findAssistantByRelatedWordList() {
		String sql = "select * from assistant_related_word";
		List<Map<String, Object>> result = dBHelper.queryMapList(LOGGER, sql);
		return result;
	}

	@Override
	public List<Map<String, Object>> findAssistantServiceList() {
		String sql = "select * from assistant_service_list where status = '1'";
		List<Map<String, Object>> result = dBHelper.queryMapList(LOGGER, sql);
		return result;
	}

	@Override
	public List<Map<String, Object>> findAssistantHistoryByAssistantId(String assistantId) {
		String sql = "select * from assistant_history where assistant_id=?";
		List<Map<String, Object>> result = dBHelper.queryMapList(LOGGER, sql, assistantId);
		return result;
	}

	@Override
	public Map<String, Object> findLastBackAssistantHistory(String assistantId, String requestContent) {
		String sql = "select * from assistant_history where service_type='2' and assistant_id=? and request_content=? order by create_time desc limit 1";
		Map<String, Object> result = dBHelper.queryMap(LOGGER, sql, assistantId, requestContent);
		return result;
	}

}
