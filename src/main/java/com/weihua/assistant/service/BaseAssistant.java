package com.weihua.assistant.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.weihua.assistant.constant.AssistantType;
import com.weihua.assistant.constant.OriginType;
import com.weihua.assistant.context.Context;
import com.weihua.assistant.context.Context.HistoryRecord;
import com.weihua.assistant.entity.request.BaseRequest;
import com.weihua.assistant.entity.response.BaseResponse;
import com.weihua.assistant.entity.response.Response;
import com.weihua.assistant.service.annotation.ServiceLocation;
import com.weihua.util.DateUtil;
import com.weihua.util.ExceptionUtil;
import com.weihua.util.GsonUtil;
import com.weihua.util.TemplateUtil;

public abstract class BaseAssistant implements Assistant {

	private static Logger LOGGER = Logger.getLogger(BaseAssistant.class);

	private static final String TEMPLATE_SUFFIX = ".htm";

	private static final String DEFAULT_TEMPLATE = "default";

	private static final String ASSISTANT_NAME = "assistantName";

	private static final String ASSISTANT_TYPE = "assistantType";

	private static final String ASSISTANT_TITLE = "assistantTitle";

	private static List<Map<String, Object>> assistantNameList = null;

	private static Map<String, Assistant> assistantCache = new HashMap<String, Assistant>();

	protected Response response(Map<String, Object> model, String templatePath) {
		bindCommonInfo(model);

		String content = TemplateUtil.renderByTemplateReader(templatePath + TEMPLATE_SUFFIX, model);
		BaseResponse response = new BaseResponse(content);
		if (model != null) {
			String modelJson = GsonUtil.toJson(model);
			response.setMetaData(modelJson);
		}

		return response;
	}

	protected Response response(Map<String, Object> model) {
		bindCommonInfo(model);

		String content = TemplateUtil.renderByTemplateReader(DEFAULT_TEMPLATE + TEMPLATE_SUFFIX, model);
		BaseResponse response = new BaseResponse(content);
		if (model != null) {
			String modelJson = GsonUtil.toJson(model);
			response.setMetaData(modelJson);
		}

		return response;
	}

	protected Response responseJson(Map<String, Object> model) {
		String modelJson = "{}";

		bindCommonInfo(model);

		if (model != null)
			modelJson = GsonUtil.toJson(model);

		BaseResponse response = new BaseResponse(modelJson);
		response.setMetaData(modelJson);

		return response;
	}

	protected Response responseJson(Object model) {
		String modelJson = "{}";

		if (model != null)
			modelJson = GsonUtil.toJson(model);

		BaseResponse response = new BaseResponse(modelJson);
		response.setMetaData(modelJson);

		return response;
	}

	private void bindCommonInfo(Map<String, Object> model) {
		if (model != null) {
			String code = AssistantType.fromValue(this.getClass().getName()).getCode();
			model.put(ASSISTANT_NAME, this.getClass().getSimpleName());
			model.put(ASSISTANT_TITLE, getAssistantNameFromAssistantMap(code));
			model.put(ASSISTANT_TYPE, code);
		}
	}

	private String getAssistantNameFromAssistantMap(String code) {
		if (assistantNameList != null) {
			for (Map<String, Object> item : assistantNameList) {
				if (code.equals(String.valueOf(item.get("id")))) {
					return String.valueOf(item.get("assistant_name"));
				}
			}
		}
		return "";
	}

	protected static Assistant getAssistantByAssistantType(String assistantTypeName) {
		try {
			if (assistantCache.containsKey(assistantTypeName)) {
				return assistantCache.get(assistantTypeName);
			} else {
				Class<?> assistantType = Class.forName(assistantTypeName);
				Assistant assistant = (Assistant) assistantType.newInstance();
				synchronized (assistantCache) {
					if (!assistantCache.containsKey(assistantTypeName)) {
						assistantCache.put(assistantTypeName, assistant);
					}
				}
				return assistant;
			}
		} catch (Exception e) {
			ExceptionUtil.propagate(LOGGER, e);
		}
		return null;
	}

	protected static void setAssistantMap(List<Map<String, Object>> assistantList) {
		BaseAssistant.assistantNameList = assistantList;
	}

	protected static List<Map<String, Object>> getAssistantMap() {
		return BaseAssistant.assistantNameList;
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

	protected static boolean isTriggerServiceReminding(ServiceTriggerPeriod serviceTriggerPeriod, String remindTime,
			BaseRequest request) {

		if (ServiceTriggerPeriod.DAY == serviceTriggerPeriod) {
			String currentTime = DateUtil.getDateFormat(new Date(), "HH:mm");
			if (currentTime.equals(remindTime)) {
				HistoryRecord lastHistoryRecord = Context.findLastBackAssistantHistory(request.getAssistantType(),
						request.getOriginRequest());
				return lastHistoryRecord == null
						|| !remindTime.equals(DateUtil.getDateFormat(lastHistoryRecord.getCreateTime(), "HH:mm"));
			}
		} else if (ServiceTriggerPeriod.SECOND == serviceTriggerPeriod) {
			HistoryRecord lastHistoryRecord = Context.findLastBackAssistantHistory(request.getAssistantType(),
					request.getOriginRequest());
			return lastHistoryRecord == null || DateUtil.getDateDiff(DateUtil.getNowDateTime(),
					lastHistoryRecord.getCreateTime()) > Long.valueOf(remindTime);
		}

		return false;

	}

	protected enum ServiceTriggerPeriod {
		SECOND, DAY, WEEK, MONTH, SEASON, YEAR;
		public static ServiceTriggerPeriod fromCode(String code) {
			for (ServiceTriggerPeriod entity : ServiceTriggerPeriod.values()) {
				if (String.valueOf(entity.ordinal()).equals(code)) {
					return entity;
				}
			}
			return null;
		}
	}

	protected static ServiceRemindTimeConfig getServiceRemindTimeConfig(BaseRequest request) {
		if (request.getOriginType() != OriginType.WEB)
			return null;
		ServiceRemindTimeConfig serviceRemindTimeConfig = new ServiceRemindTimeConfig();
		Map<String, String> timeConfig = GsonUtil.getMapFromJson(request.getExtraInfo());
		serviceRemindTimeConfig.serviceTriggerPeriod = ServiceTriggerPeriod.fromCode(timeConfig.get("triggerPeriod"));
		serviceRemindTimeConfig.remindTimeConfig = timeConfig;
		return serviceRemindTimeConfig;
	}

	protected static class ServiceRemindTimeConfig {
		public ServiceTriggerPeriod serviceTriggerPeriod;
		public Map<String, String> remindTimeConfig;
	}
}
