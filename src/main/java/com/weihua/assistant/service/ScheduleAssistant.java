package com.weihua.assistant.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.weihua.assistant.constant.AssistantConstant;
import com.weihua.assistant.constant.OriginType;
import com.weihua.assistant.entity.request.BaseRequest;
import com.weihua.assistant.entity.request.Request;
import com.weihua.assistant.entity.response.Response;
import com.weihua.assistant.service.annotation.BackServiceLocation;
import com.weihua.assistant.service.annotation.ServiceLocation;
import com.weihua.assistant.service.base.BaseAssistant;
import com.weihua.database.dao.FamilyDao;
import com.weihua.database.dao.HolidayArrangementDao;
import com.weihua.database.dao.LifeMotoDao;
import com.weihua.database.dao.impl.FamilyDaoImpl;
import com.weihua.database.dao.impl.HolidayArrangementDaoImpl;
import com.weihua.database.dao.impl.LifeMotoDaoImpl;
import com.weihua.util.CollectionUtil;
import com.weihua.util.ConfigUtil;
import com.weihua.util.DateUtil;
import com.weihua.util.DateUtil.TimePeriod;
import com.weihua.util.DidaListUtil;
import com.weihua.util.DidaListUtil.Task;
import com.weihua.util.DidaListUtil.TaskStatus;
import com.weihua.util.DidaListUtil.TaskType;
import com.weihua.util.EmailUtil;
import com.weihua.util.EmailUtil.SendEmailInfo;
import com.weihua.util.ExceptionUtil;
import com.weihua.util.GsonUtil;

/**
 * @author chengwei2
 * @category Schedule Service
 */
public class ScheduleAssistant extends BaseAssistant {

	private static Logger LOGGER = Logger.getLogger(ScheduleAssistant.class);
	private static LifeMotoDao lifeMotoDao = new LifeMotoDaoImpl();
	private static HolidayArrangementDao holidayArrangementDao = new HolidayArrangementDaoImpl();
	private static FamilyDao familyDao = new FamilyDaoImpl();

	@Override
	public Response getResponse(Request request) {
		Response response = null;
		try {
			BaseRequest baseRequest = (BaseRequest) request;
			if (baseRequest.isLocationPath() == null || baseRequest.isLocationPath() == false) {
				analysisRequest(baseRequest);
				response = getScheduleList((BaseRequest) request);
			} else {
				response = invokeLocationMethod((BaseRequest) request);
			}
		} catch (Exception e) {
			ExceptionUtil.propagate(LOGGER, e);
		}
		return response;
	}

	private void analysisRequest(BaseRequest baseRequest) {
		String content = baseRequest.getContent();
		Map<String, String> extraInfo = new HashMap<String, String>();
		TaskType taskType = TaskType.fromValue(content);
		if (taskType != null) {
			extraInfo.put("taskType", taskType.getCode());
			baseRequest.setExtraInfo(GsonUtil.toJson(extraInfo));
		}
	}

	@ServiceLocation(value = "getScheduleList")
	public Response getScheduleList(BaseRequest request) throws Exception {
		if (request.getOriginType() != OriginType.WEB)
			return null;
		String extraInfo = request.getExtraInfo();
		Map<String, String> extraInfoMap = GsonUtil.getMapFromJson(extraInfo);

		TaskType taskType = TaskType.fromCode(String.valueOf(extraInfoMap.get("taskType")));
		List<Task> taskList = DidaListUtil.getTaskListFromDida365(taskType, TaskStatus.UNFINISH);

		Map<String, Object> model = new HashMap<String, Object>();
		model.put("taskCode", taskType.getCode());
		model.put("taskValue", taskType.getValue());
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		if (CollectionUtil.isNotEmpty(taskList)) {
			for (Task item : taskList) {
				Map<String, Object> entity = new HashMap<String, Object>();
				entity.put("id", item.id);
				entity.put("title", item.title);
				entity.put("status", item.status);
				list.add(entity);
			}
		}

		model.put("taskList", list);

		return response(model, "schedule/tasklist");
	}

	@BackServiceLocation(value = "getScheduleByTime")
	public Response getScheduleByTime(BaseRequest request) throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();
		if (isTriggerServiceReminding(request)) {
			bindScheduleModel(model);

			SendEmailInfo info = new SendEmailInfo();
			info.setReceiveUser(
					ConfigUtil.getProperties().get(AssistantConstant.FAMILY_ASSISTANT_EMAIL_SCHEDULE_REMINDEMAILUSER));
			info.setHeadName(AssistantConstant.FAMILY_ASSISTANT_STRING_12);
			Response response = response(model, "schedule/schedule");
			info.setSendHtml(response.getContent());
			EmailUtil.sendEmail(info);

			return responseJson(null);
		}

		return null;
	}

	private void bindScheduleModel(Map<String, Object> model) throws Exception {
		Map<String, Object> lifeMoto = lifeMotoDao.findRandomRecord();
		model.put("lifeMoto", lifeMoto.get("moto"));
		model.put("lifeMotoImage", lifeMoto.get("collocation_picture"));

		List<Map<String, Object>> scheduleList = new ArrayList<Map<String, Object>>();
		List<Task> taskList = DidaListUtil.getTaskListFromDida365(TaskType.CURRENT_SCHEDULE, TaskStatus.UNFINISH);
		if (!CollectionUtil.isEmpty(taskList)) {
			for (Task task : taskList) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("schedule_time", task.startDate);
				map.put("schedule_title", task.title);
				map.put("schedule_content", task.content);
				scheduleList.add(map);
			}
		}

		boolean isHoliday = holidayArrangementDao.findIsHoliday(DateUtil.getDateFormat(new Date()));
		List<Map<String, Object>> triflesList = familyDao.findRecordListByTime("00:00", "23:59",
				isHoliday ? AssistantConstant.FAMILY_ASSISTANT_STRING_6 : AssistantConstant.FAMILY_ASSISTANT_STRING_7);
		if (!CollectionUtil.isEmpty(triflesList)) {
			for (Map<String, Object> item : triflesList) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("schedule_time", item.get("record_time"));
				map.put("schedule_title", item.get("record_title"));
				map.put("schedule_content", item.get("record_content"));
				scheduleList.add(map);
			}
		}

		Date tempDate;
		TimePeriod timePeriod;
		for (Map<String, Object> item : scheduleList) {
			if (item.get("schedule_time") == null) {
				item.put("schedule_time", "00:00");
			}
			tempDate = DateUtil.getDateTimeHHMMFormat(String.valueOf(item.get("schedule_time")));
			timePeriod = DateUtil.getTimePeriodByDate(tempDate);
			item.put("time_period_color", TimePeriodColor.fromCode(timePeriod.getCode()).getValue());
		}

		model.put("scheduleList", scheduleList);
	}

	public static enum TimePeriodColor {
		MORNING("MORNING", "rgba(46,204,113,0.6)"), BEFORENOON("BEFORENOON", "rgba(26,188,156,0.6)"), NOON("NOON",
				"rgba(231,76,60,0.6)"), AFTERNOON("AFTERNOON", "rgba(230,126,34,0.6)"), NIGHT("NIGHT",
						"rgba(243,156,18,0.6)"), DEEPNIGHT("DEEPNIGHT", "rgba(192,57,43,0.6)");

		private TimePeriodColor(String code, String value) {
			this.code = code;
			this.value = value;
		}

		private String code;
		private String value;

		public String getCode() {
			return code;
		}

		public String getValue() {
			return value;
		}

		public static TimePeriodColor fromCode(String code) {
			for (TimePeriodColor entity : TimePeriodColor.values()) {
				if (entity.getCode().equals(code)) {
					return entity;
				}
			}
			return TimePeriodColor.MORNING;
		}
	}

}
