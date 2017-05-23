package com.weihua.assistant.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.weihua.assistant.entity.request.BaseRequest;
import com.weihua.assistant.entity.request.Request;
import com.weihua.assistant.entity.response.Response;
import com.weihua.assistant.service.annotation.ServiceLocation;
import com.weihua.util.ExceptionUtil;
import com.weihua.util.GsonUtil;

public class KnowledgeBaseAssistant extends BaseAssistant {

	private static Logger LOGGER = Logger.getLogger(KnowledgeBaseAssistant.class);
	
	@Override
	public Response getResponse(Request request) {
		Response response = null;
		try {
			BaseRequest baseRequest = (BaseRequest) request;
			if (baseRequest.isLocationPath() == null || baseRequest.isLocationPath() == false) {
				response = getKnowledgeDetail((BaseRequest) request);
			} else {
				response = invokeLocationMethod((BaseRequest) request);
			}
		} catch (Exception e) {
			ExceptionUtil.propagate(LOGGER, e);
		}
		return response;
	}
	
	@ServiceLocation(value = "getKnowledgeDetail")
	public Response getKnowledgeDetail(BaseRequest request) {
		String extraInfo = request.getExtraInfo();
		Map<String, String> extraInfoMap = GsonUtil.getMapFromJson(extraInfo);

		Map<String, Object> model = new HashMap<String, Object>();
		return response(model, "knowledgebase/item");
	}
}
