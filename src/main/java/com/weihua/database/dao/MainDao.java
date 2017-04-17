package com.weihua.database.dao;

import java.util.List;
import java.util.Map;

public interface MainDao {
	List<Map<String, Object>> findAssistantByRelatedWordList();

	List<Map<String, Object>> findAssistantList();

	Map<String, Object> findAssistantById(Integer id);

	List<Map<String, Object>> findAssistantHistory(Integer topCount);

	int recordAssistantHistory(Object... params);
}
