package com.weihua.assistant.service.base;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.gson.reflect.TypeToken;
import com.weihua.assistant.constant.OriginType;
import com.weihua.assistant.context.Context;
import com.weihua.assistant.context.Context.HistoryRecord;
import com.weihua.assistant.entity.request.BaseRequest;
import com.weihua.assistant.service.Assistant;
import com.weihua.util.DateUtil;
import com.weihua.util.GsonUtil;

public abstract class ServiceAssistant implements Assistant {

	private static Logger LOGGER = Logger.getLogger(ServiceAssistant.class);

	protected static boolean isTriggerServiceReminding(BaseRequest request) {
		ServiceRemindTimeConfig serviceRemindTimeConfig = getServiceRemindTimeConfig(request);
		if (serviceRemindTimeConfig == null) {
			return false;
		}
		if (ServiceTriggerPeriod.DAY == serviceRemindTimeConfig.getServiceTriggerPeriod()) {
			Date currentDate = new Date();
			String currentTime = DateUtil.getDateFormat(currentDate, "HH:mm");
			if (serviceRemindTimeConfig.remindTimes.contains(currentTime)) {
				currentTime = DateUtil.getDateFormat(currentDate, "yyyy-MM-dd ") + currentTime;
				HistoryRecord lastHistoryRecord = Context.findLastBackAssistantHistory(request.getAssistantType(),
						request.getOriginRequest());

				String remindTime = "";
				if (lastHistoryRecord != null) {
					remindTime = DateUtil.getDateFormat(lastHistoryRecord.getCreateTime(), "yyyy-MM-dd HH:mm");
				}
				LOGGER.info("ServiceAssistant currentTime:" + currentTime + "remindTime:" + remindTime);

				return lastHistoryRecord == null || !currentTime.equals(remindTime);
			}
		} else if (ServiceTriggerPeriod.SECOND == serviceRemindTimeConfig.getServiceTriggerPeriod()) {
			HistoryRecord lastHistoryRecord = Context.findLastBackAssistantHistory(request.getAssistantType(),
					request.getOriginRequest());
			return lastHistoryRecord == null || DateUtil.getDateDiff(DateUtil.getNowDateTime(),
					lastHistoryRecord.getCreateTime()) > Long.valueOf(serviceRemindTimeConfig.remindTimes.get(0));
		}

		return false;

	}

	private enum ServiceTriggerPeriod {
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

	private static ServiceRemindTimeConfig getServiceRemindTimeConfig(BaseRequest request) {
		if (request.getOriginType() != OriginType.WEB)
			return null;
		ServiceRemindTimeConfig serviceRemindTimeConfig = GsonUtil.getEntityFromJson(request.getExtraInfo(),
				new TypeToken<ServiceRemindTimeConfig>() {
				});
		return serviceRemindTimeConfig;
	}

	private static class ServiceRemindTimeConfig {
		public ServiceTriggerPeriod getServiceTriggerPeriod() {
			return ServiceTriggerPeriod.fromCode(triggerPeriod);
		}

		public String triggerPeriod;
		public List<String> remindTimes;

		@Override
		public String toString() {
			return "{serviceTriggerPeriod:" + getServiceTriggerPeriod().name() + ",remindTimes:" + remindTimes + "}";
		}
	}
}
