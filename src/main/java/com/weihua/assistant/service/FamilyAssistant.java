package com.weihua.assistant.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.gson.reflect.TypeToken;
import com.weihua.assistant.constant.OriginType;
import com.weihua.assistant.context.Context;
import com.weihua.assistant.context.Context.HistoryRecord;
import com.weihua.assistant.entity.request.BaseRequest;
import com.weihua.assistant.entity.request.Request;
import com.weihua.assistant.entity.response.Response;
import com.weihua.assistant.service.FamilyAssistant.Task.Item;
import com.weihua.assistant.service.annotation.ServiceLocation;
import com.weihua.database.dao.FamilyDao;
import com.weihua.database.dao.HolidayArrangementDao;
import com.weihua.database.dao.LifeMotoDao;
import com.weihua.database.dao.impl.FamilyDaoImpl;
import com.weihua.database.dao.impl.HolidayArrangementDaoImpl;
import com.weihua.database.dao.impl.LifeMotoDaoImpl;
import com.weihua.util.DateUtil;
import com.weihua.util.EmailUtil;
import com.weihua.util.EmailUtil.SendEmailInfo;

import okhttp3.ResponseBody;
import retrofit2.Call;

import com.weihua.util.ExceptionUtil;
import com.weihua.util.GsonUtil;
import com.weihua.util.RetrofitUtil;
import com.weihua.util.StringUtil;

public class FamilyAssistant extends BaseAssistant {

	private static Logger LOGGER = Logger.getLogger(FamilyAssistant.class);

	private static FamilyDao familyDao = new FamilyDaoImpl();

	private static LifeMotoDao lifeMotoDao = new LifeMotoDaoImpl();

	private static HolidayArrangementDao holidayArrangementDao = new HolidayArrangementDaoImpl();

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
		String extraInfo = request.getExtraInfo();
		Map<String, String> extraInfoMap = GsonUtil.getMapFromJson(extraInfo);
		String word = null;
		if (extraInfoMap != null && !StringUtil.isEmpty(extraInfoMap.get("word"))) {
			word = extraInfoMap.get("word");
		}
		List<Map<String, Object>> result = null;
		if (word != null)
			result = familyDao.findRecordListByWord(word);

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
		String extraInfo = request.getExtraInfo();
		Map<String, String> extraInfoMap = GsonUtil.getMapFromJson(extraInfo);
		String typeName = extraInfoMap.get("typeName") != null ? String.valueOf(extraInfoMap.get("typeName")) : "";
		String recordTime = extraInfoMap.get("recordTime") != null ? String.valueOf(extraInfoMap.get("recordTime"))
				: "";
		String recordTitle = extraInfoMap.get("recordTitle") != null ? String.valueOf(extraInfoMap.get("recordTitle"))
				: "";
		String recordContent = extraInfoMap.get("recordContent") != null
				? String.valueOf(extraInfoMap.get("recordContent")) : "";
		String optimization = extraInfoMap.get("optimization") != null
				? String.valueOf(extraInfoMap.get("optimization")) : "";
		int recordId = extraInfoMap.get("recordId") != null ? Integer.valueOf(extraInfoMap.get("recordId")) : 0;

		int result = 0;
		if (recordId == 0) {
			result = familyDao.modifyRecord(typeName, recordTitle, recordContent, recordTime, optimization);
		} else {
			result = familyDao.modifyRecord(typeName, recordTitle, recordContent, recordTime, optimization, recordId);
		}

		Map<String, Object> model = new HashMap<String, Object>();

		model.put("status", result > 0 ? 1 : 0);
		model.put("msg", result > 0 ? "Ok,save succeed." : "Sorry,save failed.");
		return responseJson(model);
	}

	@ServiceLocation(value = "getTriflesByTime")
	public Response getTriflesByTime(BaseRequest request) {
		if (request.getOriginType() != OriginType.WEB)
			return null;

		Map<String, String> timeConfig = GsonUtil.getMapFromJson(request.getExtraInfo());

		String morningRemindTime = timeConfig.get("morningRemindTime");
		String afternoonRemindTime = timeConfig.get("afternoonRemindTime");
		String nightRemindTime = timeConfig.get("nightRemindTime");

		String currentTime = DateUtil.getDateFormat(new Date(), "HH:mm");

		Map<String, Object> model = new HashMap<String, Object>();
		if (currentTime.equals(morningRemindTime) && isNotReminded(morningRemindTime, request)) {
			bindTriflesModel(model, "00:00", "11:59", "morning.jpeg");
			return sendEmail(model, "早上好，这是您上午的日程清单，请查收。");
		} else if (currentTime.equals(afternoonRemindTime) && isNotReminded(afternoonRemindTime, request)) {
			bindTriflesModel(model, "12:00", "18:59", "afternoon.jpeg");
			return sendEmail(model, "中午好，这是您下午的日程清单，请查收。");
		} else if (currentTime.equals(nightRemindTime) && isNotReminded(nightRemindTime, request)) {
			bindTriflesModel(model, "19:00", "23:59", "night.jpeg");
			return sendEmail(model, "晚上好，这是您晚上的日程清单，请查收。");
		}

		return null;
	}

	private void bindTriflesModel(Map<String, Object> model, String timeBegin, String timeEnd, String bannerImage) {
		boolean isHoliday = holidayArrangementDao.findIsHoliday(DateUtil.getDateFormat(new Date()));
		List<Map<String, Object>> result = familyDao.findRecordListByTime(timeBegin, timeEnd,
				isHoliday ? "休息日" : "工作日");
		model.put("recordList", result);
		model.put("taskList", getScheduleTask());
		model.put("bannerImage", bannerImage);
		Map<String, Object> lifeMoto = lifeMotoDao.findRandomRecord();
		model.put("lifeMoto", lifeMoto.get("moto"));
	}

	private List<Map<String, Object>> getScheduleTask() {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		Task task;
		try {
			task = getTaskListFromDida365();
			if (task != null && task.items != null && task.items.size() > 0) {
				for (Item item : task.items) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("record_time", "--:--");
					map.put("type_name", "日程");
					map.put("record_title", item.title);
					map.put("optimization", "");
					result.add(map);
				}
			} else {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("record_time", "--:--");
				map.put("type_name", "日程");
				map.put("record_title", "未安排");
				map.put("optimization", "无事可做是最大的悲哀。");
				result.add(map);
			}
		} catch (Exception e) {
			LOGGER.error("Get task error:", e);
		}
		return result;
	}

	private Response sendEmail(Map<String, Object> model, String title) {
		model.put("title", title);
		SendEmailInfo info = new SendEmailInfo();
		info.setHeadName(title);
		Response response = response(model, "family/email");
		info.setSendHtml(response.getContent());
		EmailUtil.sendEmail(info);
		return responseJson(null);
	}

	private boolean isNotReminded(String remindTime, BaseRequest request) {
		HistoryRecord lastHistoryRecord = Context.findLastBackAssistantHistory(request.getAssistantType(),
				request.getOriginRequest());
		return lastHistoryRecord == null
				|| !remindTime.equals(DateUtil.getDateFormat(lastHistoryRecord.getCreateTime(), "HH:mm"));
	}

	public static Task getTaskListFromDida365() throws Exception {
		LoginInfo loginInfo = new LoginInfo();
		Call<ResponseBody> loginResult = RetrofitUtil.retrofitService.post(
				"https://dida365.com/api/v2/user/signon?wc=true&remember=true",
				RetrofitUtil.getJsonRequestBody(loginInfo), "");

		retrofit2.Response<ResponseBody> loginResponse = loginResult.execute();
		String loginContent = loginResponse.body().string();
		Map<String, String> map = GsonUtil.getMapFromJson(loginContent);

		Call<ResponseBody> taskResult = RetrofitUtil.retrofitService.get("https://dida365.com/api/v2/project/all/tasks",
				"t=" + map.get("token"));

		retrofit2.Response<ResponseBody> taskResponse = taskResult.execute();
		String taskContent = taskResponse.body().string();
		List<Task> taskList = GsonUtil.<ArrayList<Task>> getEntityFromJson(taskContent,
				new TypeToken<ArrayList<Task>>() {
				});

		String taskTitle = DateUtil.getDateFormat(new Date(), "yyyyMMdd") + "日程安排";

		for (Task task : taskList) {
			if (taskTitle.equals(task.title)) {
				return task;
			}
		}

		return null;
	}

	private static final String decodeKey = "huawei";

	private static String encode(String s, String key) {
		String str = "";
		int ch;
		if (key.length() == 0) {
			return s;
		} else if (!s.equals(null)) {
			for (int i = 0, j = 0; i < s.length(); i++, j++) {
				if (j > key.length() - 1) {
					j = j % key.length();
				}
				ch = s.codePointAt(i) + key.codePointAt(j);
				if (ch > 65535) {
					ch = ch % 65535;// ch - 33 = (ch - 33) % 95 ;
				}
				str += (char) ch;
			}
		}
		return str;

	}

	private static String decode(String s, String key) {
		String str = "";
		int ch;
		if (key.length() == 0) {
			return s;
		} else if (!s.equals(key)) {
			for (int i = 0, j = 0; i < s.length(); i++, j++) {
				if (j > key.length() - 1) {
					j = j % key.length();
				}
				ch = (s.codePointAt(i) + 65535 - key.codePointAt(j));
				if (ch > 65535) {
					ch = ch % 65535;// ch - 33 = (ch - 33) % 95 ;
				}
				str += (char) ch;
			}
		}
		return str;
	}

	static class LoginInfo {
		public String username = decode("®°¢ª·ÖÚØÐä", decodeKey);
		public String password = decode("ªª¨¬", decodeKey);
	}
	
	public static void main(String[] args) throws Exception {
		LoginInfo loginInfo=new LoginInfo();
		System.out.println(loginInfo.password);
		System.out.println(loginInfo.username);
	}

	static class Task {
		public String id;
		public String deleted;
		public String startDate;
		public String priority;
		public String title;
		public String content;
		public String status;
		public List<Item> items;

		static class Item {
			public String id;
			public String title;
			public String status;
		}
	}

}
