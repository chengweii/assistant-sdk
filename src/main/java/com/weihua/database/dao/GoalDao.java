package com.weihua.database.dao;

import java.util.List;
import java.util.Map;

public interface GoalDao {
	List<Map<String, Object>> findGoalList();

	List<Map<String, Object>> findStageListByGoalName(String goalName);

	Map<String, Object> findStageById(Integer id);

	int modifyStage(Object... params);
}
