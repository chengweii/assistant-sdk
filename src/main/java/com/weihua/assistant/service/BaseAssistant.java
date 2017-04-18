package com.weihua.assistant.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import com.weihua.assistant.constant.AssistantType;
import com.weihua.assistant.entity.request.BaseRequest;
import com.weihua.assistant.entity.response.BaseResponse;
import com.weihua.assistant.entity.response.Response;
import com.weihua.assistant.service.annotation.ServiceLocation;
import com.weihua.util.GsonUtil;
import com.weihua.util.TemplateUtil;

public abstract class BaseAssistant implements Assistant {

	private static final String TEMPLATE_SUFFIX = ".htm";

	private static final String DEFAULT_TEMPLATE = "default";

	private static final String ASSISTANT_NAME = "assistantName";

	private static final String ASSISTANT_TYPE = "assistantType";

	protected Response response(Map<String, Object> model, String templatePath) {
		if (model != null) {
			model.put(ASSISTANT_NAME, this.getClass().getSimpleName());
			model.put(ASSISTANT_TYPE, AssistantType.fromValue(this.getClass().getName()).getCode());
		}
		String content = TemplateUtil.renderByTemplateReader(templatePath + TEMPLATE_SUFFIX, model);
		BaseResponse response = new BaseResponse(content);
		if (model != null) {
			String modelJson = GsonUtil.toJson(model);
			response.setMetaData(modelJson);
		}
		return response;
	}

	protected Response response(Map<String, Object> model) {
		if (model != null) {
			model.put(ASSISTANT_NAME, this.getClass().getSimpleName());
			model.put(ASSISTANT_TYPE, AssistantType.fromValue(this.getClass().getName()).getCode());
		}
		String content = TemplateUtil.renderByTemplateReader(DEFAULT_TEMPLATE + TEMPLATE_SUFFIX, model);
		BaseResponse response = new BaseResponse(content);
		if (model != null) {
			String modelJson = GsonUtil.toJson(model);
			response.setMetaData(modelJson);
		}
		return response;
	}

	protected Response invokeLocationMethod(BaseRequest request)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		for (Method method : this.getClass().getDeclaredMethods()) {
			ServiceLocation serviceLocation = method.getAnnotation(ServiceLocation.class);
			if (serviceLocation != null && serviceLocation.value().equals(request.getContent())) {
				return (Response) method.invoke(this, request);
			}
		}
		return null;
	}
}
