package com.weihua.assistant.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.weihua.assistant.context.Context;
import com.weihua.assistant.context.Context.HistoryRecord;
import com.weihua.assistant.entity.alarm.AlarmInfo;
import com.weihua.assistant.entity.request.BaseRequest;
import com.weihua.assistant.entity.request.Request;
import com.weihua.assistant.entity.response.Response;
import com.weihua.assistant.service.annotation.ServiceLocation;
import com.weihua.database.dao.FamilyDao;
import com.weihua.database.dao.impl.FamilyDaoImpl;
import com.weihua.util.DateUtil;
import com.weihua.util.ExceptionUtil;
import com.weihua.util.GsonUtil;

public class FamilyAssistant extends BaseAssistant {

	private static Logger LOGGER = Logger.getLogger(FamilyAssistant.class);

	private static FamilyDao familyDao = new FamilyDaoImpl();

	@Override
	public Response getResponse(Request request) {
		Response response = null;
		try {
			BaseRequest baseRequest = (BaseRequest) request;
			if (baseRequest.isLocationPath() == null || baseRequest.isLocationPath() == false) {
				response = getRecordListByWord((BaseRequest) request);
			} else {
				response = invokeLocationMethod((BaseRequest) request);
			}
		} catch (Exception e) {
			ExceptionUtil.propagate(LOGGER, e);
		}
		return response;
	}

	@ServiceLocation(value = "getRecordListByWord")
	public Response getRecordListByWord(BaseRequest request) {
		String extraInfo = request.getExtraInfo();
		Map<String, String> extraInfoMap = GsonUtil.getMapFromJson(extraInfo);
		String word = "";
		if (extraInfoMap != null && extraInfoMap.get("word") != null) {
			word = extraInfoMap.get("word");
		}
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		if (!"".equals(word))
			result = familyDao.findRecordListByWord(word);

		Map<String, Object> model = new HashMap<String, Object>();

		model.put("recordList", result);
		return response(model, "family/index");
	}

	@ServiceLocation(value = "getRecordById")
	public Response getRecordById(BaseRequest request) {
		String extraInfo = request.getExtraInfo();
		Map<String, String> extraInfoMap = GsonUtil.getMapFromJson(extraInfo);
		Integer recordId = extraInfoMap.get("recordId") != null ? Integer.valueOf(extraInfoMap.get("recordId")) : 0;
		Map<String, Object> result = familyDao.findRecordById(recordId);

		Map<String, Object> model = new HashMap<String, Object>();

		model.put("record", result);
		return response(model, "family/item");
	}

	@ServiceLocation(value = "recordLife")
	public Response recordLife(BaseRequest request) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("record_id", "");
		result.put("type_name", "");
		result.put("record_time", "");
		result.put("record_title", "");
		result.put("record_content", "");
		result.put("optimization", "");

		Map<String, Object> model = new HashMap<String, Object>();

		model.put("record", result);
		return response(model, "family/item");
	}

	@ServiceLocation(value = "saveRecord")
	public Response saveRecord(BaseRequest request) {
		String extraInfo = request.getExtraInfo();
		Map<String, String> extraInfoMap = GsonUtil.getMapFromJson(extraInfo);
		String typeName = extraInfoMap.get("typeName") != null ? String.valueOf(extraInfoMap.get("typeName")) : "";
		String recordTime = extraInfoMap.get("recordTime") != null ? String.valueOf(extraInfoMap.get("recordTime"))
				: "";
		String recordTitle = extraInfoMap.get("recordTitle") != null ? String.valueOf(extraInfoMap.get("recordTitle"))
				: "";
		String recordContent = extraInfoMap.get("recordContent") != null
				? String.valueOf(extraInfoMap.get("recordContent")) : "";
		String optimization = extraInfoMap.get("optimization") != null
				? String.valueOf(extraInfoMap.get("optimization")) : "";
		int recordId = extraInfoMap.get("recordId") != null ? Integer.valueOf(extraInfoMap.get("recordId")) : 0;

		int result = 0;
		if (recordId == 0) {
			result = familyDao.modifyRecord(typeName, recordTitle, recordContent, recordTime, optimization);
		} else {
			result = familyDao.modifyRecord(typeName, recordTitle, recordContent, recordTime, optimization, recordId);
		}

		Map<String, Object> model = new HashMap<String, Object>();

		model.put("status", result > 0 ? 1 : 0);
		model.put("msg", result > 0 ? "Ok,save succeed." : "Sorry,save failed.");
		return responseJson(model);
	}
	
	@ServiceLocation(value = "getTriflesByTime")
	public Response getTriflesByTime(BaseRequest request) {
		HistoryRecord lastHistoryRecord = Context.findLastBackAssistantHistory(request.getAssistantType(),
				request.getOriginRequest());
		Map<String, String> timeConfig = GsonUtil.getMapFromJson(request.getExtraInfo());
		
		String[] timeList=timeConfig.get("rate").split(",");
		
		if(timeList!=null&&timeList.length>0){
			String currentTime=DateUtil.getDateFormat(new Date(), "");
			for(String time:timeList){
			}
		}
		
		return null;
	}
}
