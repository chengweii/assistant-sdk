package com.weihua.assistant.context;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.weihua.assistant.constant.AssistantType;
import com.weihua.assistant.entity.request.BaseRequest;
import com.weihua.assistant.entity.response.BaseResponse;
import com.weihua.database.dao.MainDao;
import com.weihua.database.dao.impl.MainDaoImpl;
import com.weihua.util.DateUtil;

public class Context {

	private static Map<String, List<HistoryRecord>> history = new LinkedHashMap<String, List<HistoryRecord>>();

	private static MainDao mainDao = new MainDaoImpl();

	public static boolean recordHistory(AssistantType assistantType, BaseRequest request, BaseResponse response) {
		if (assistantType == null)
			return false;

		HistoryRecord historyRecord = new HistoryRecord();
		historyRecord.setRequest(request);
		historyRecord.setResponse(response);
		historyRecord.setAssistantType(assistantType);
		historyRecord.setCreateTime(new Date());
		List<HistoryRecord> list = null;
		if (history.containsKey(assistantType.getCode())) {
			list = history.get(assistantType.getCode());
		} else {
			list = new ArrayList<HistoryRecord>();
			history.put(assistantType.getCode(), list);
		}

		persistenceHistory(historyRecord);

		list.add(historyRecord);

		return true;
	}

	public static List<HistoryRecord> findHistoryByAssistantId(AssistantType assistantType) {
		if (assistantType == null)
			return null;

		return history.get(assistantType.getCode());
	}

	private static void persistenceHistory(HistoryRecord historyRecord) {
		mainDao.recordAssistantHistory(historyRecord.getAssistantType().getCode(),
				historyRecord.getRequest().getOriginRequest(), historyRecord.getResponse().getMetaData(),
				DateUtil.getDateTimeFormat(historyRecord.getCreateTime()));
	}

	public static class HistoryRecord {
		private BaseRequest request;
		private BaseResponse response;
		private AssistantType assistantType;
		private Date createTime;

		public BaseRequest getRequest() {
			return request;
		}

		public void setRequest(BaseRequest request) {
			this.request = request;
		}

		public BaseResponse getResponse() {
			return response;
		}

		public void setResponse(BaseResponse response) {
			this.response = response;
		}

		public AssistantType getAssistantType() {
			return assistantType;
		}

		public void setAssistantType(AssistantType assistantType) {
			this.assistantType = assistantType;
		}

		public Date getCreateTime() {
			return createTime;
		}

		public void setCreateTime(Date createTime) {
			this.createTime = createTime;
		}

	}
}
