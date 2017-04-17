package com.weihua.assistant.service;

import java.util.HashMap;
import java.util.Map;

import com.weihua.assistant.entity.request.Request;
import com.weihua.assistant.entity.response.Response;

public class FamilyAssistant extends BaseAssistant {

	@Override
	public Response getResponse(Request request) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("responseContent", "FamilyAssistant is developing.");
		return response(model);
	}

}
