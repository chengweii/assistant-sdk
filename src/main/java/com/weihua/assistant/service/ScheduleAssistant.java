package com.weihua.assistant.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.weihua.assistant.constant.OriginType;
import com.weihua.assistant.entity.request.BaseRequest;
import com.weihua.assistant.entity.request.Request;
import com.weihua.assistant.entity.response.Response;
import com.weihua.assistant.service.annotation.ServiceLocation;
import com.weihua.assistant.service.base.BaseAssistant;
import com.weihua.util.DidaListUtil;
import com.weihua.util.DidaListUtil.Task;
import com.weihua.util.DidaListUtil.Task.Item;
import com.weihua.util.DidaListUtil.TaskType;
import com.weihua.util.ExceptionUtil;
import com.weihua.util.GsonUtil;

/**
 * @author chengwei2
 * @category Schedule Service
 */
public class ScheduleAssistant extends BaseAssistant {

	private static Logger LOGGER = Logger.getLogger(ScheduleAssistant.class);

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
		Task task = DidaListUtil.getTaskListFromDida365(taskType);

		Map<String, Object> model = new HashMap<String, Object>();
		model.put("taskCode", taskType.getCode());
		model.put("taskValue", taskType.getValue());
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		if (task != null && task.items != null && task.items.size() > 0) {
			for (Item item : task.items) {
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

}
