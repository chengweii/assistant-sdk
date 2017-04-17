package com.weihua.assistant.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.weihua.assistant.constant.AssistantType;
import com.weihua.assistant.context.Context;
import com.weihua.assistant.entity.request.BaseRequest;
import com.weihua.assistant.entity.request.Request;
import com.weihua.assistant.entity.response.BaseResponse;
import com.weihua.assistant.entity.response.Response;
import com.weihua.database.dao.MainDao;
import com.weihua.database.dao.impl.MainDaoImpl;
import com.weihua.util.ExceptionUtil;

public class MainAssistant extends BaseAssistant {
	private static Logger LOGGER = Logger.getLogger(MainAssistant.class);

	private static MainDao mainDao = new MainDaoImpl();

	public String execute(String request) {
		BaseRequest baseRequest = new BaseRequest(request);
		Assistant assistant = assignAssistant(baseRequest);
		Response response = assistant.getResponse(baseRequest);
		if (response == null) {
			response = this.getResponse(baseRequest);
		}

		Context.recordHistory(baseRequest.getAssistantType(), baseRequest, (BaseResponse) response);

		return response.getContent();
	}

	@Override
	public Response getResponse(Request request) {
		Map<String, Object> model = new HashMap<String, Object>();
		if (request.getContent() == null || request.getContent().equals("") || request.getContent().equals("管家")) {
			model.put("welcomeMsg", "Hello,Master,What can I do for you?");
		} else {
			model.put("welcomeMsg", "Sorry,Master,About \"" + request.getContent()
					+ "\",I don't know too much yet,but I can provide you with the following help:");
		}
		model.put("assistantList", mainDao.findAssistantList());
		return response(model, "main");
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
