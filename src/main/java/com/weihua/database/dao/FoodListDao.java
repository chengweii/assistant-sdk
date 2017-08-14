package com.weihua.database.dao;

import java.util.Map;

import com.weihua.assistant.constant.FoodType;

public interface FoodListDao {
	Map<String, Object> findRandomRecord(FoodType foodType);
}
