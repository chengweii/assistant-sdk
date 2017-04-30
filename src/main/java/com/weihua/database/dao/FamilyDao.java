package com.weihua.database.dao;

import java.util.List;
import java.util.Map;

public interface FamilyDao {
	List<Map<String, Object>> findRecordListByWord(String word);

	Map<String, Object> findRecordById(Integer id);

	int modifyRecord(Object... params);
}