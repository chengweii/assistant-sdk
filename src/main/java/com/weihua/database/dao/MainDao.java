package com.weihua.database.dao;

import java.util.List;
import java.util.Map;

public interface MainDao {
	List<Map<String, Object>> findAssistantByRelatedWordList();

	List<Map<String, Object>> findAssistantList();

	Map<String, Object> findAssistantById(Integer id);

	List<Map<String, Object>> findAssistantHistory(Integer topCount);

	List<Map<String, Object>> findAssistantHistoryByAssistantId(String assistantId);

	Map<String, Object> findLastBackAssistantHistory(String assistantId, String requestContent);

	int recordAssistantHistory(Object... params);

	List<Map<String, Object>> findAssistantServiceList();
}
