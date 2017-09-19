package com.weihua.assistant.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.weihua.assistant.constant.AssistantConstant;
import com.weihua.assistant.constant.FoodType;
import com.weihua.assistant.entity.request.BaseRequest;
import com.weihua.assistant.entity.request.Request;
import com.weihua.assistant.entity.response.Response;
import com.weihua.assistant.service.annotation.BackServiceLocation;
import com.weihua.assistant.service.annotation.ServiceLocation;
import com.weihua.assistant.service.base.BaseAssistant;
import com.weihua.database.dao.FamilyDao;
import com.weihua.database.dao.FoodListDao;
import com.weihua.database.dao.HolidayArrangementDao;
import com.weihua.database.dao.LifeMotoDao;
import com.weihua.database.dao.impl.FamilyDaoImpl;
import com.weihua.database.dao.impl.FoodListDaoImpl;
import com.weihua.database.dao.impl.HolidayArrangementDaoImpl;
import com.weihua.database.dao.impl.LifeMotoDaoImpl;
import com.weihua.util.ConfigUtil;
import com.weihua.util.DateUtil;
import com.weihua.util.DateUtil.TimePeriod;
import com.weihua.util.DidaListUtil;
import com.weihua.util.DidaListUtil.Task;
import com.weihua.util.DidaListUtil.Task.Item;
import com.weihua.util.DidaListUtil.TaskType;
import com.weihua.util.EmailUtil;
import com.weihua.util.EmailUtil.SendEmailInfo;
import com.weihua.util.ExceptionUtil;
import com.weihua.util.GsonUtil;

/**
 * @author chengwei2
 * @category Family service:housework,plan,future,commodity purchasing;
 */
public class FamilyAssistant extends BaseAssistant {

	private static Logger LOGGER = Logger.getLogger(FamilyAssistant.class);

	private static FamilyDao familyDao = new FamilyDaoImpl();

	private static LifeMotoDao lifeMotoDao = new LifeMotoDaoImpl();

	private static HolidayArrangementDao holidayArrangementDao = new HolidayArrangementDaoImpl();

	private static FoodListDao foodListDao = new FoodListDaoImpl();

	@Override
	public Response getResponse(Request request) {
		Response response = null;
		try {
			BaseRequest baseRequest = (BaseRequest) request;
			if (baseRequest.isLocationPath() == null || baseRequest.isLocationPath() == false) {
				response = getRecordListByWord((BaseRequest) request);
			} else {
				response = invokeLocationMethod((BaseRequest) request);
			}
		} catch (Exception e) {
			ExceptionUtil.propagate(LOGGER, e);
		}
		return response;
	}

	@ServiceLocation(value = "getRecordListByWord")
	public Response getRecordListByWord(BaseRequest request) {
		Map<String, String> extraInfoMap = GsonUtil.getMapFromJson(request.getExtraInfo());
		String word = Strings.nullToEmpty(extraInfoMap == null ? null : extraInfoMap.get("word"));
		List<Map<String, Object>> result = familyDao.findRecordListByWord(word);

		Map<String, Object> model = new HashMap<String, Object>();

		model.put("recordList", result);
		return response(model, "family/index");
	}

	@ServiceLocation(value = "getRecordById")
	public Response getRecordById(BaseRequest request) {
		String extraInfo = request.getExtraInfo();
		Map<String, String> extraInfoMap = GsonUtil.getMapFromJson(extraInfo);
		Integer recordId = extraInfoMap.get("recordId") != null ? Integer.valueOf(extraInfoMap.get("recordId")) : 0;
		Map<String, Object> result = familyDao.findRecordById(recordId);

		Map<String, Object> model = new HashMap<String, Object>();

		model.put("record", result);
		return response(model, "family/item");
	}

	@ServiceLocation(value = "recordLife")
	public Response recordLife(BaseRequest request) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("record_id", "");
		result.put("type_name", "");
		result.put("record_time", "");
		result.put("record_title", "");
		result.put("record_content", "");
		result.put("optimization", "");

		Map<String, Object> model = new HashMap<String, Object>();

		model.put("record", result);
		return response(model, "family/item");
	}

	@ServiceLocation(value = "saveRecord")
	public Response saveRecord(BaseRequest request) {
		Map<String, String> extraInfoMap = GsonUtil.getMapFromJson(request.getExtraInfo());
		String typeName = MoreObjects.firstNonNull(extraInfoMap.get("typeName"), "");
		String recordTime = MoreObjects.firstNonNull(extraInfoMap.get("recordTime"), "");
		String recordTitle = MoreObjects.firstNonNull(extraInfoMap.get("recordTitle"), "");
		String recordContent = MoreObjects.firstNonNull(extraInfoMap.get("recordContent"), "");
		String optimization = MoreObjects.firstNonNull(extraInfoMap.get("optimization"), "");
		int recordId = Integer.valueOf(MoreObjects.firstNonNull(extraInfoMap.get("recordId"), "0"));

		int result = 0;
		if (recordId == 0) {
			result = familyDao.modifyRecord(typeName, recordTitle, recordContent, recordTime, optimization);
		} else {
			result = familyDao.modifyRecord(typeName, recordTitle, recordContent, recordTime, optimization, recordId);
		}

		Map<String, Object> model = new HashMap<String, Object>();

		model.put("status", result > 0 ? 1 : 0);
		model.put("msg",
				result > 0 ? AssistantConstant.FAMILY_ASSISTANT_STRING_1 : AssistantConstant.FAMILY_ASSISTANT_STRING_2);
		return responseJson(model);
	}

	@ServiceLocation(value = "getTriflesByTime")
	public Response getTriflesByTime(BaseRequest request) {
		return null;
	}

	@BackServiceLocation(value = "getHourseWorkByTime")
	public Response getHourseWorkByTime(BaseRequest request) {
		Map<String, Object> model = new HashMap<String, Object>();
		if (isTriggerServiceReminding(request)) {
			bindHourseWorkModel(model);

			SendEmailInfo info = new SendEmailInfo();
			info.setReceiveUser(ConfigUtil.getProperties()
					.get(AssistantConstant.FAMILY_ASSISTANT_EMAIL_HOURSEWORK_REMINDEMAILUSER));
			info.setHeadName(AssistantConstant.FAMILY_ASSISTANT_STRING_12);
			Response response = response(model, "family/housework");
			info.setSendHtml(response.getContent());
			EmailUtil.sendEmail(info);

			return responseJson(null);
		}

		return null;
	}

	private void bindHourseWorkModel(Map<String, Object> model) {
		model.put("title",
				DateUtil.getDateFormat(new Date(), "yyyy-MM-dd") + AssistantConstant.FAMILY_ASSISTANT_STRING_13);
		Map<String, Object> lifeMoto = lifeMotoDao.findRandomRecord();
		model.put("lifeMoto", lifeMoto.get("moto"));

		Map<String, Object> complementary = foodListDao.findRandomRecord(FoodType.COMPLEMENTARY);
		model.put("complementary", complementary);

		Map<String, Object> breakfast = foodListDao.findRandomRecord(FoodType.BREAKFAST);
		model.put("breakfast", breakfast);

		Map<String, Object> lunch = foodListDao.findRandomRecord(FoodType.LUNCH);
		model.put("lunch", lunch);

		Map<String, Object> dinner = foodListDao.findRandomRecord(FoodType.DINNER);
		model.put("dinner", dinner);

		boolean isHoliday = holidayArrangementDao.findIsHoliday(DateUtil.getDateFormat(new Date()));
		List<Map<String, Object>> hourseWorkList = familyDao.findRecordListByTime("00:00", "23:59",
				isHoliday ? AssistantConstant.FAMILY_ASSISTANT_STRING_6 : AssistantConstant.FAMILY_ASSISTANT_STRING_7);

		Date tempDate;
		TimePeriod timePeriod;
		for (Map<String, Object> hourseWork : hourseWorkList) {
			tempDate = DateUtil.formatDate(String.valueOf(hourseWork.get("record_time")), "hh:mm");
			timePeriod = DateUtil.getTimePeriodByDate(tempDate);
			hourseWork.put("time_period_color", TimePeriodColor.fromCode(timePeriod.getCode()).getValue());
		}

		model.put("hourseWorkList", hourseWorkList);
	}

	private List<Map<String, Object>> getScheduleTask() {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		Task task;
		try {
			task = DidaListUtil.getTaskListFromDida365(TaskType.CURRENT_TRIFLES);
			if (task != null && task.items != null && task.items.size() > 0) {
				for (Item item : task.items) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("record_time", AssistantConstant.FAMILY_ASSISTANT_STRING_11);
					map.put("type_name", AssistantConstant.FAMILY_ASSISTANT_STRING_8);
					map.put("record_title", item.title);
					map.put("optimization", "");
					result.add(map);
				}
			} else {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("record_time", AssistantConstant.FAMILY_ASSISTANT_STRING_11);
				map.put("type_name", AssistantConstant.FAMILY_ASSISTANT_STRING_8);
				map.put("record_title", AssistantConstant.FAMILY_ASSISTANT_STRING_9);
				map.put("optimization", AssistantConstant.FAMILY_ASSISTANT_STRING_10);
				result.add(map);
			}
		} catch (Exception e) {
			LOGGER.error("Get task error:", e);
		}
		return result;
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
