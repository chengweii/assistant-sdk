package com.weihua.assistant.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.weihua.assistant.constant.AssistantType;
import com.weihua.assistant.constant.ServiceType;
import com.weihua.assistant.context.Context;
import com.weihua.assistant.entity.request.BaseRequest;
import com.weihua.assistant.entity.request.Request;
import com.weihua.assistant.entity.response.BaseResponse;
import com.weihua.assistant.entity.response.Response;
import com.weihua.assistant.service.annotation.ServiceLocation;
import com.weihua.database.dao.MainDao;
import com.weihua.database.dao.impl.MainDaoImpl;
import com.weihua.util.ExceptionUtil;

public class MainAssistant extends BaseAssistant {
	private static Logger LOGGER = Logger.getLogger(MainAssistant.class);

	private static MainDao mainDao = new MainDaoImpl();

	static {
		setAssistantMap(mainDao.findAssistantList());
	}

	public String execute(String request) {
		BaseRequest baseRequest = new BaseRequest(request);
		Assistant assistant = assignAssistant(baseRequest);
		Response response = assistant.getResponse(baseRequest);
		if (response == null) {
			response = this.getResponse(baseRequest);
		}

		if (baseRequest.isLocationPath() == null) {
			Context.recordHistory(baseRequest.getAssistantType(), ServiceType.FRONT_SERVICE, baseRequest,
					(BaseResponse) response);
		}

		return response.getContent();
	}

	@Override
	public Response getResponse(Request request) {
		Response response = null;
		try {
			BaseRequest baseRequest = (BaseRequest) request;
			if (baseRequest.isLocationPath() == null || baseRequest.isLocationPath() == false) {
				response = toHome((BaseRequest) request);
			} else {
				response = invokeLocationMethod((BaseRequest) request);
			}
		} catch (Exception e) {
			ExceptionUtil.propagate(LOGGER, e);
		}
		return response;
	}

	@ServiceLocation(value = "toHome")
	public Response toHome(BaseRequest request) {
		Map<String, Object> model = new HashMap<String, Object>();
		if (request.getContent() == null || request.getContent().equals("") || request.getContent().equals("管家")) {
			model.put("welcomeMsg", "Hello,Master,What can I do for you?");
		} else {
			model.put("welcomeMsg", "Sorry,Master,About \"" + request.getContent()
					+ "\",I don't know too much yet,but I can provide you with the following help:");
		}
		model.put("assistantList", getAssistantMap());
		return response(model, "main");
	}

	@ServiceLocation(value = "callAlarmService")
	public Response callAlarmService(BaseRequest request) {
		List<Map<String, Object>> result = mainDao.findAssistantServiceList();

		Map<String, Object> model = new HashMap<String, Object>();
		List<String> msgList = new ArrayList<String>();

		Assistant assistant;
		Response response;
		BaseRequest baseRequest;
		try {
			for (Map<String, Object> item : result) {
				Class<?> assistantType;
				assistantType = Class.forName(AssistantType.fromCode(item.get("assistant_id").toString()).getValue());
				assistant = (Assistant) assistantType.newInstance();
				baseRequest = new BaseRequest(item.get("request_content").toString());
				baseRequest.setOriginType(request.getOriginType());
				baseRequest.setExtraInfo(item.get("time_config").toString());
				response = assistant.getResponse(baseRequest);
				if (response != null) {
					if (response.getContent() != null) {
						msgList.add(response.getContent());
					}
					Context.recordHistory(baseRequest.getAssistantType(), ServiceType.BACK_SERVICE, baseRequest,
							(BaseResponse) response);
				}
			}
		} catch (Exception e) {
			ExceptionUtil.propagate(LOGGER, e);
		}

		model.put("msgList", msgList);
		return responseJson(model);
	}

	private Assistant assignAssistant(BaseRequest baseRequest) {
		Assistant assistant = this;
		try {
			if (baseRequest.getAssistantType() != AssistantType.MAIN_ASSISTANT) {
				Class<?> assistantType = Class.forName(baseRequest.getAssistantType().getValue());
				assistant = (Assistant) assistantType.newInstance();
			} else {
				List<Map<String, Object>> assistantByRelatedWordList = mainDao.findAssistantByRelatedWordList();
				if (assistantByRelatedWordList != null && assistantByRelatedWordList.size() > 0) {
					for (Map<String, Object> entity : assistantByRelatedWordList) {
						if (entity.get("associated_word").equals(baseRequest.getContent())) {
							Class<?> assistantType = Class
									.forName(AssistantType.fromCode(entity.get("assistant_id").toString()).getValue());
							assistant = (Assistant) assistantType.newInstance();
							break;
						}
					}
				}
			}
		} catch (Exception e) {
			ExceptionUtil.propagate(LOGGER, e);
		}
		return assistant;
	}
}
