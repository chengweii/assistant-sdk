package com.weihua.database.dao.impl;

import java.util.Map;

import org.apache.log4j.Logger;

import com.weihua.database.dao.FoodListDao;

public class FoodListDaoImpl extends BaseDao implements FoodListDao {

	private static Logger LOGGER = Logger.getLogger(FoodListDaoImpl.class);

	@Override
	public Map<String, Object> findRandomRecord(Integer foodType, Integer forSeason, Integer forWeek) {
		String sql = "select * from food_list where food_type = ? and for_season = ? and for_week = ? order by random() limit 1";
		Map<String, Object> result = dBHelper.queryMap(LOGGER, sql, foodType, forSeason, forWeek);
		return result;
	}

}
