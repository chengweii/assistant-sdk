package com.weihua.util;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class DBUtil {
	private static Logger LOGGER = Logger.getLogger(DBUtil.class);

	private static DBHelper dBHelper;

	public static void initDBHelper(DBHelper helper) {
		dBHelper = helper;
	}

	public static DBHelper getDBHelper() {
		if (dBHelper == null) {
			LOGGER.error("DBHelper is not initialized.");
			throw new RuntimeException("Please firstly call DBUtil.initDBHelper to init DBHelper.");
		}
		return dBHelper;
	}

	public interface DBHelper {
		String DB_NAME = "assistant.db";

		Map<String, Object> queryMap(Logger logger, String sql, Object... params);

		List<Map<String, Object>> queryMapList(Logger logger, String sql, Object... params);

		int queryUpdate(Logger logger, String sql, Object... params);

		int[] queryBatch(Logger logger, String sql, Object[][] params);
	}
}
