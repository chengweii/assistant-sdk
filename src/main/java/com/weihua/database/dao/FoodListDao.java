package com.weihua.database.dao;

import java.util.Map;

public interface FoodListDao {
	Map<String, Object> findRandomRecord(Integer foodType,Integer forSeason,Integer forWeek);
}
