package com.weihua.ui.userinterface;

import org.apache.log4j.Logger;

import com.weihua.assistant.constant.AssistantType;
import com.weihua.assistant.constant.OriginType;
import com.weihua.assistant.entity.request.BaseRequest;
import com.weihua.assistant.service.MainAssistant;
import com.weihua.util.GsonUtil;

public class AssistantInterface implements UserInterface {

	private static Logger LOGGER = Logger.getLogger(AssistantInterface.class);

	@Override
	public String getResponse(String request) {
		MainAssistant mainAssistant = new MainAssistant();
		return mainAssistant.execute(request);
	}

	public static void main(String[] args) {
		AssistantInterface assistantInterface = new AssistantInterface();
		BaseRequest.RequestData requestData = new BaseRequest.RequestData();
		requestData.originType = OriginType.WEB.getCode();
		requestData.assistantType = AssistantType.MAIN_ASSISTANT.getCode();
		requestData.requestContent = "test";
		String request = GsonUtil.toJson(requestData);
		String response = assistantInterface.getResponse(request);
		LOGGER.info(response);
	}
}
