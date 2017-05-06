package com.weihua.database.dao;

import java.util.List;
import java.util.Map;

public interface InterviewDao {
	Map<String, Object> findSubjectById(Integer id);

	List<Map<String, Object>> findSubjectList(int beginRowNum, int rowCount);
}
