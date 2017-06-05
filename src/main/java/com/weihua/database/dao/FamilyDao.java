package com.weihua.database.dao;

import java.util.List;
import java.util.Map;

public interface FamilyDao {
	List<Map<String, Object>> findRecordListByWord(String word);

	Map<String, Object> findRecordById(Integer id);

	int modifyRecord(Object... params);

	List<Map<String, Object>> findRecordListByTime(String timeBegin, String timeEnd, String typeWord);

	int[] syncAllRecord(Object[][] params);
}
