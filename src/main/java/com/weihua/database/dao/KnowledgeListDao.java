package com.weihua.database.dao;

import java.util.List;
import java.util.Map;

public interface KnowledgeListDao {
	List<Map<String, Object>> findRecordList();
}
