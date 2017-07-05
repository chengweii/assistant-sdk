package com.weihua.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.gson.reflect.TypeToken;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class DidaListUtil {

	private static Logger LOGGER = Logger.getLogger(DidaListUtil.class);

	private static final String LOGIN_URL = "https://dida365.com/api/v2/user/signon?wc=true&remember=true";
	private static final String GET_TASK_URL = "https://dida365.com/api/v2/project/all/tasks";
	private static String username;
	private static String password;

	public static void main(String[] args) throws Exception {
		LoginInfo loginInfo = new LoginInfo();
		System.out.println(loginInfo.password);
		System.out.println(loginInfo.username);
		System.out.println(encode(loginInfo.password, decodeKey));
		Task task = DidaListUtil.getTaskListFromDida365(TaskType.FUTURE_WORK);
		System.out.println(task);
	}

	public static Task getTaskListFromDida365(TaskType taskType) throws Exception {

		List<Task> taskList = getTaskListFromDida365();
		String taskTitle = taskType.getCode();
		if (taskType == TaskType.CURRENT_TRIFLES || taskType == TaskType.CURRENT_WORK) {
			taskTitle = DateUtil.getDateFormat(new Date(), "yyyyMMdd") + taskType.getCode();
		}
		if (CollectionUtil.isNotEmpty(taskList)) {
			for (Task task : taskList) {
				if (taskTitle.equals(task.title)) {
					return task;
				}
			}
		}

		return null;
	}

	private static List<Task> getTaskListFromDida365() throws Exception {
		Date currentDate = new Date();
		if (tokenHolder == null || DateUtil.getDateDiff(currentDate, tokenHoldTime) > 600) {
			LoginInfo loginInfo = new LoginInfo();
			if (username == null || password == null) {
				LOGGER.error("Please init loginInfo firstly.");
				return null;
			} else {
				loginInfo.username = decode(username, decodeKey);
				loginInfo.password = decode(password, decodeKey);
			}
			Call<ResponseBody> loginResult = RetrofitUtil.retrofitService.post(LOGIN_URL,
					RetrofitUtil.getJsonRequestBody(loginInfo), "");

			retrofit2.Response<ResponseBody> loginResponse = loginResult.execute();
			String loginContent = loginResponse.body().string();
			Map<String, String> map = GsonUtil.getMapFromJson(loginContent);
			tokenHolder = map.get("token");
			tokenHoldTime = currentDate;
		}

		Call<ResponseBody> taskResult = RetrofitUtil.retrofitService.get(GET_TASK_URL, "t=" + tokenHolder);

		retrofit2.Response<ResponseBody> taskResponse = taskResult.execute();
		String taskContent = taskResponse.body().string();
		List<Task> taskList = GsonUtil.<ArrayList<Task>> getEntityFromJson(taskContent,
				new TypeToken<ArrayList<Task>>() {
				});

		return taskList;
	}

	private static String tokenHolder = null;
	private static Date tokenHoldTime = new Date();

	public static void initDidaListUtil(String uname, String pwd) {
		username = uname;
		password = pwd;
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

	private static class LoginInfo {
		public String username;
		public String password;
	}

	public static enum TaskType {
		CURRENT_TRIFLES("CurrentTrifles", "当前琐事"), 
		CURRENT_WORK("CurrentWork", "当前工作"), 
		FUTURE_TRIFLES("FutureTrifles", "未来琐事"), 
		FUTURE_WORK("FutureWork", "未来工作"), 
		TECHNICAL_STUDY("TechnicalStudy", "技术研究");

		private TaskType(String code, String value) {
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

		public static TaskType fromCode(String code) {
			for (TaskType entity : TaskType.values()) {
				if (entity.getCode().equals(code)) {
					return entity;
				}
			}
			return null;
		}

		public static TaskType fromValue(String value) {
			for (TaskType entity : TaskType.values()) {
				if (entity.getValue().equals(value)) {
					return entity;
				}
			}
			return null;
		}
	}

	public static class Task {
		public String id;
		public String deleted;
		public String startDate;
		public String priority;
		public String title;
		public String content;
		public String status;
		public List<Item> items;

		public static class Item {
			public String id;
			public String title;
			public String status;
		}
	}
}
