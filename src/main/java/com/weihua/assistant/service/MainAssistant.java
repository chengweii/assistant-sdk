package com.weihua.assistant.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.weihua.assistant.constant.AssistantType;
import com.weihua.assistant.entity.request.BaseRequest;
import com.weihua.assistant.entity.request.Request;
import com.weihua.assistant.entity.response.Response;
import com.weihua.database.dao.MainDao;
import com.weihua.database.dao.impl.MainDaoImpl;
import com.weihua.util.ExceptionUtil;

public class MainAssistant extends BaseAssistant {
	private static Logger LOGGER = Logger.getLogger(MainAssistant.class);
	
	private static MainDao mainDao=new MainDaoImpl();

	public String execute(String request) {
		BaseRequest baseRequest = new BaseRequest(request);
		Assistant assistant = assignAssistant(baseRequest);
		Response response = assistant.getResponse(baseRequest);
		return response.getContent();
	}

	@Override
	public Response getResponse(Request request) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("requestContent", request.getContent());
		model.put("responseContent", mainDao.findAll());
		return response(model);
	}

	private Assistant assignAssistant(BaseRequest baseRequest) {
		Assistant assistant = this;
		try {
			if (baseRequest.getAssistantType() != null
					&& baseRequest.getAssistantType() != AssistantType.MAIN_ASSISTANT) {
				Class<?> assistantType = Class.forName(baseRequest.getAssistantType().getValue());
				assistant = (Assistant) assistantType.newInstance();
			}
		} catch (Exception e) {
			ExceptionUtil.propagate(LOGGER, e);
		}
		return assistant;
	}
}
