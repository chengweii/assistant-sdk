package com.weihua.assistant.context;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.weihua.assistant.constant.AssistantType;
import com.weihua.assistant.entity.request.Request;
import com.weihua.assistant.entity.response.Response;

public class Context {
	private static Logger LOGGER = Logger.getLogger(Context.class);

	private static Map<String, List<HistoryRecord>> history = new LinkedHashMap<String, List<HistoryRecord>>();

	public static boolean recordHistory(AssistantType assistantType, Request request, Response response) {
		if (assistantType == null)
			return false;

		HistoryRecord historyRecord = new HistoryRecord();
		historyRecord.setRequest(request);
		historyRecord.setResponse(response);
		List<HistoryRecord> list = null;
		if (history.containsKey(assistantType.getCode())) {
			list = history.get(assistantType.getCode());
		} else {
			list = new ArrayList<HistoryRecord>();
			history.put(assistantType.getCode(), list);
		}
		list.add(historyRecord);
		
		return true;
	}

	public static List<HistoryRecord> findHistoryByAssistantId(AssistantType assistantType) {
		if (assistantType == null)
			return null;
		
		return history.get(assistantType.getCode());
	}

	public static boolean persistenceHistory() {
		LOGGER.info("Persistence history success.");
		return true;
	}

	public static class HistoryRecord {
		private Request request;
		private Response response;

		public Request getRequest() {
			return request;
		}

		public void setRequest(Request request) {
			this.request = request;
		}

		public Response getResponse() {
			return response;
		}

		public void setResponse(Response response) {
			this.response = response;
		}
	}
}
