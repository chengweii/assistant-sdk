package com.weihua.assistant.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.weihua.assistant.constant.AssistantConstant;
import com.weihua.assistant.constant.FoodType;
import com.weihua.assistant.entity.alarm.AlarmInfo;
import com.weihua.assistant.entity.request.BaseRequest;
import com.weihua.assistant.entity.request.Request;
import com.weihua.assistant.entity.response.Response;
import com.weihua.assistant.service.annotation.BackServiceLocation;
import com.weihua.assistant.service.base.BaseAssistant;
import com.weihua.database.dao.FoodListDao;
import com.weihua.database.dao.impl.FoodListDaoImpl;
import com.weihua.util.DateUtil;
import com.weihua.util.DateUtil.TimePeriod;
import com.weihua.util.ExceptionUtil;

/**
 * @author chengwei2
 * @category Cookbook;
 */
public class DietAssistant extends BaseAssistant {
	private static Logger LOGGER = Logger.getLogger(DietAssistant.class);

	private static FoodListDao foodListDao = new FoodListDaoImpl();

	@Override
	public Response getResponse(Request request) {
		Response response = null;
		try {
			BaseRequest baseRequest = (BaseRequest) request;
			if (baseRequest.isLocationPath() == null || baseRequest.isLocationPath() == false) {
				response = getFoodByTime((BaseRequest) request);
			} else {
				response = invokeLocationMethod((BaseRequest) request);
			}
		} catch (Exception e) {
			ExceptionUtil.propagate(LOGGER, e);
		}
		return response;
	}

	@BackServiceLocation(value = "getFoodByTime")
	public Response getFoodByTime(BaseRequest request) {
		if (isTriggerServiceReminding(request)) {
			FoodType foodType = FoodType.BREAKFAST;
			Date date = new Date();
			TimePeriod timePeriod = DateUtil.getTimePeriodByDate(date);
			if (timePeriod == TimePeriod.NOON) {
				foodType = FoodType.LUNCH;
			} else if (timePeriod == TimePeriod.AFTERNOON) {
				foodType = FoodType.DINNER;
			}

			if (foodType == null)
				return null;

			Map<String, Object> food = foodListDao.findRandomRecord(foodType);
			if (food != null) {
				AlarmInfo alarmInfo = new AlarmInfo();
				alarmInfo.setTitle(foodType.getValue() + AssistantConstant.DIETASSISTANT_STRING_1);
				alarmInfo.setContent(String.valueOf(food.get("food_name")));
				alarmInfo.setImage(String.valueOf(food.get("icon_link")));
				return responseJson(alarmInfo);
			}
		}

		return null;
	}

}
