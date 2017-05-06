package com.weihua.database.dao;

import java.util.List;
import java.util.Map;

public interface FinancingDao {
	List<Map<String, Object>> findAlarmSharesList();

	int modifyAlarmShares(Object... params);
}
