package com.weihua.database.dao.impl;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.weihua.database.dao.FinancingDao;

public class FinancingDaoImpl extends BaseDao implements FinancingDao {

	private static Logger LOGGER = Logger.getLogger(FinancingDaoImpl.class);

	@Override
	public List<Map<String, Object>> findAlarmSharesList() {
		String sql = "select * from alarm_shares";
		List<Map<String, Object>> result = dBHelper.queryMapList(LOGGER, sql);
		return result;
	}

	@Override
	public int modifyAlarmShares(Object... params) {
		String sql = "update alarm_shares set alarm_config=?,status=? where id=?";
		return dBHelper.queryUpdate(LOGGER, sql, params);
	}

}
