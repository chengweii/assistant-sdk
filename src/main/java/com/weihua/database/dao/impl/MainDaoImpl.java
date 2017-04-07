package com.weihua.database.dao.impl;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.weihua.database.dao.MainDao;

public class MainDaoImpl extends BaseDao implements MainDao {

	private static Logger LOGGER = Logger.getLogger(MainDaoImpl.class);

	@Override
	public List<Map<String, Object>> findAll() {
		String sql = "select id,name,password from user";
		List<Map<String, Object>> list = dBHelper.queryMapList(LOGGER, sql);
		return list;
	}

}
