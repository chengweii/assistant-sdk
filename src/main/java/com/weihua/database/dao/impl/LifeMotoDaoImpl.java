package com.weihua.database.dao.impl;

import java.util.Map;

import org.apache.log4j.Logger;

import com.weihua.database.dao.LifeMotoDao;

public class LifeMotoDaoImpl extends BaseDao implements LifeMotoDao {

	private static Logger LOGGER = Logger.getLogger(LifeMotoDaoImpl.class);

	@Override
	public Map<String, Object> findRandomRecord() {
		String sql = "select * from life_moto order by random() limit 1";
		Map<String, Object> result = dBHelper.queryMap(LOGGER, sql);
		return result;
	}

}
