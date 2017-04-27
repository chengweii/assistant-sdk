package com.weihua.database.dao.impl;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.weihua.database.dao.GoalDao;

public class GoalDaoImpl extends BaseDao implements GoalDao {

	private static Logger LOGGER = Logger.getLogger(GoalDaoImpl.class);

	@Override
	public List<Map<String, Object>> findGoalList() {
		String sql = "select t.goal_name,sum(t.stage_cost_hours) as goal_cost_hours,round(sum(t.stage_cost_hours*t.progress)/sum(t.stage_cost_hours)) as goal_progress from my_goal t GROUP BY t.goal_name";
		List<Map<String, Object>> result = dBHelper.queryMapList(LOGGER, sql);
		return result;
	}

	@Override
	public List<Map<String, Object>> findStageListByGoalName(String goalName) {
		String sql = "select * from my_goal where goal_name=?";
		List<Map<String, Object>> result = dBHelper.queryMapList(LOGGER, sql,goalName);
		return result;
	}

	@Override
	public Map<String, Object> findStageById(Integer id) {
		String sql = "select * from my_goal where id=?";
		Map<String, Object> result = dBHelper.queryMap(LOGGER, sql,id);
		return result;
	}

	@Override
	public int modifyStage(Object... params) {
		String sql = "update my_goal set stage_cost_hours=?,progress=? where id=?";
		return dBHelper.queryUpdate(LOGGER, sql, params);
	}

}
