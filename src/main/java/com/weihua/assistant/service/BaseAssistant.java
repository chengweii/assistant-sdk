package com.weihua.assistant.service;

import java.util.Map;

import com.weihua.assistant.entity.response.BaseResponse;
import com.weihua.assistant.entity.response.Response;

import com.weihua.util.TemplateUtil;

public abstract class BaseAssistant implements Assistant {

	private static final String TEMPLATE_SUFFIX = ".htm";

	private static final String DEFAULT_TEMPLATE = "default";

	protected Response response(Map<String, Object> model, String templatePath) {
		String content = TemplateUtil.renderByTemplateReader(templatePath + TEMPLATE_SUFFIX, model);
		Response response = new BaseResponse(content);
		return response;
	}

	protected Response response(Map<String, Object> model) {
		String content = TemplateUtil.renderByTemplateReader(DEFAULT_TEMPLATE + TEMPLATE_SUFFIX, model);
		Response response = new BaseResponse(content);
		return response;
	}
}
