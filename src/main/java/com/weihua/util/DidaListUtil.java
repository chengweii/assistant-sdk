package com.weihua.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import com.google.gson.reflect.TypeToken;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class DidaListUtil {

	private static Logger LOGGER = Logger.getLogger(DidaListUtil.class);
	private static String username;
	private static String password;

	private static void initUtilConfig() {
		ResourceBundle emailBundle = ResourceBundle.getBundle("assets/config", Locale.getDefault());
		Map<String, String> map = new HashMap<String, String>();
		for (String key : emailBundle.keySet()) {
			map.put(key, emailBundle.getString(key));
		}
		ConfigUtil.init(map);
	}

	public static void main(String[] args) throws Exception {
		initUtilConfig();
		Task task = DidaListUtil.getTaskListFromDida365(TaskType.CURRENT_WORK);
		System.out.println(task);
	}

	private static String tokenHolder = null;
	private static Date tokenHoldTime = new Date();
	private static final String LOGIN_URL = "https://dida365.com/api/v2/user/signon?wc=true&remember=true";

	private static void login() {
		Date currentDate = new Date();
		try {
			if (tokenHolder == null || DateUtil.getDateDiff(currentDate, tokenHoldTime) > 600) {
				LoginInfo loginInfo = new LoginInfo();
				if (username == null || password == null) {
					LOGGER.error("Please init loginInfo firstly.");
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
		} catch (Exception e) {
			LOGGER.error("login didalist failed.", e);
		}
	}

	private static final String GET_PROJECT_URL = "https://api.dida365.com/api/v2/projects";

	private static List<Map<String, Object>> getProjectList() throws Exception {
		Call<ResponseBody> taskResult = RetrofitUtil.retrofitService.get(GET_PROJECT_URL, "t=" + tokenHolder);

		retrofit2.Response<ResponseBody> taskResponse = taskResult.execute();
		String projectContent = taskResponse.body().string();
		List<Map<String, Object>> projectList = GsonUtil.<ArrayList<Map<String, Object>>> getEntityFromJson(
				projectContent, new TypeToken<ArrayList<Map<String, Object>>>() {
				});
		return projectList;
	}

	private static final String GET_TASK_URL = "https://api.dida365.com/api/v2/project/{project_id}/tasks/?from=&to={end_time}&limit={limit_count}";

	private static List<Task> getTaskList(String projectId, String endTime, String limitCount, TaskStatus taskStatus)
			throws Exception {
		if (StringUtil.isEmpty(projectId) || StringUtil.isEmpty(endTime) || StringUtil.isEmpty(limitCount)) {
			LOGGER.error("projectId,endTime,limitCount be required.");
			return null;
		}
		String url = GET_TASK_URL.replace("{project_id}", projectId).replace("{end_time}", endTime)
				.replace("{limit_count}", limitCount);
		Call<ResponseBody> taskResult = RetrofitUtil.retrofitService.get(url, "t=" + tokenHolder);

		retrofit2.Response<ResponseBody> taskResponse = taskResult.execute();
		String taskContent = taskResponse.body().string();
		List<Task> taskList = GsonUtil.<ArrayList<Task>> getEntityFromJson(taskContent,
				new TypeToken<ArrayList<Task>>() {
				});
		if (!CollectionUtil.isEmpty(taskList) && taskStatus != null) {
			List<Task> filterTaskList = new ArrayList<Task>();
			for (Task task : taskList) {
				if (String.valueOf(taskStatus.ordinal()).equals(task.status)) {
					filterTaskList.add(task);
				}
			}
			return filterTaskList;
		}

		return taskList;
	}

	public static Task getTaskListFromDida365(TaskType taskType) throws Exception {
		login();
		List<Map<String, Object>> projectList = getProjectList();
		if (!CollectionUtil.isEmpty(projectList)) {
			String projectId = null;
			for (Map<String, Object> map : projectList) {
				if (map.get("name").equals(taskType.getValue())) {
					projectId = String.valueOf(map.get("id"));
					break;
				}
			}
			String endTime = DateUtil.getDateFormat(new Date(), DateUtil.DATETIME_DEFAULT_FORMAT);
			List<Task> taskList = getTaskList(projectId, endTime, "100", TaskStatus.UNFINISH);
			if (!CollectionUtil.isEmpty(taskList)) {
				LOGGER.info("Query task size:" + taskList.size());
			}
		}
		return null;
	}

	public static enum TaskType {
		CURRENT_TRIFLES("CurrentTrifles", "当前琐事"), CURRENT_WORK("CurrentWork", "我的工作"), FUTURE_TRIFLES("FutureTrifles",
				"未来琐事"), FUTURE_WORK("FutureWork", "未来工作"), TECHNICAL_STUDY("TechnicalStudy", "技术研究");

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

	public static enum TaskStatus {
		UNFINISH, DOING, FINISHED;
	}

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

	public static class Task {
		public String id;
		public String deleted;
		public String createdTime;
		public String completedTime;
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
