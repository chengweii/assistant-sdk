package com.weihua.assistant.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.weihua.assistant.entity.request.BaseRequest;
import com.weihua.assistant.entity.request.Request;
import com.weihua.assistant.entity.response.Response;
import com.weihua.assistant.service.annotation.ServiceLocation;
import com.weihua.database.dao.GoalDao;
import com.weihua.database.dao.impl.GoalDaoImpl;
import com.weihua.util.ExceptionUtil;
import com.weihua.util.GsonUtil;

/**
 * @author chengwei2
 * @category 目标规划
 */
public class GoalAssistant extends BaseAssistant {

	private static Logger LOGGER = Logger.getLogger(InterviewAssistant.class);

	private static GoalDao goalDao = new GoalDaoImpl();

	@Override
	public Response getResponse(Request request) {
		Response response = null;
		try {
			BaseRequest baseRequest = (BaseRequest) request;
			if (baseRequest.isLocationPath() == null || baseRequest.isLocationPath() == false) {
				response = getGoalList((BaseRequest) request);
			} else {
				response = invokeLocationMethod((BaseRequest) request);
			}
		} catch (Exception e) {
			ExceptionUtil.propagate(LOGGER, e);
		}
		return response;
	}

	@ServiceLocation(value = "getGoalList")
	public Response getGoalList(BaseRequest request) {
		List<Map<String, Object>> result = goalDao.findGoalList();

		if (result != null) {
			for (Map<String, Object> item : result) {
				item.put("progress_cost_hours", (int) (Double.parseDouble(item.get("goal_cost_hours").toString())
						* Double.parseDouble(item.get("goal_progress").toString()) / 100));
			}
		}

		Map<String, Object> model = new HashMap<String, Object>();

		model.put("goalList", result);
		return response(model, "goal/index");
	}

	@ServiceLocation(value = "getStageListByGoalName")
	public Response getStageListByGoalName(BaseRequest request) {
		String extraInfo = request.getExtraInfo();
		Map<String, String> extraInfoMap = GsonUtil.getMapFromJson(extraInfo);
		String goalName = extraInfoMap.get("goalName") != null ? extraInfoMap.get("goalName") : "";
		List<Map<String, Object>> result = goalDao.findStageListByGoalName(goalName);

		if (result != null) {
			for (Map<String, Object> item : result) {
				item.put("progress_cost_hours", (int) (Double.parseDouble(item.get("stage_cost_hours").toString())
						* Double.parseDouble(item.get("progress").toString()) / 100));
			}
		}

		Map<String, Object> model = new HashMap<String, Object>();

		model.put("stageList", result);
		return response(model, "goal/list");
	}

	@ServiceLocation(value = "getStageById")
	public Response getStageById(BaseRequest request) {
		String extraInfo = request.getExtraInfo();
		Map<String, String> extraInfoMap = GsonUtil.getMapFromJson(extraInfo);
		int stageId = extraInfoMap.get("stageId") != null ? Integer.valueOf(extraInfoMap.get("stageId")) : 0;
		Map<String, Object> result = goalDao.findStageById(stageId);

		Map<String, Object> model = new HashMap<String, Object>();

		model.put("stage", result);
		return response(model, "goal/item");
	}

	@ServiceLocation(value = "modifyStage")
	public Response modifyStage(BaseRequest request) {
		String extraInfo = request.getExtraInfo();
		Map<String, String> extraInfoMap = GsonUtil.getMapFromJson(extraInfo);
		int stageCostHours = extraInfoMap.get("stageCostHours") != null
				? Integer.valueOf(extraInfoMap.get("stageCostHours")) : 0;
		int stageProgress = extraInfoMap.get("stageProgress") != null
				? Integer.valueOf(extraInfoMap.get("stageProgress")) : 0;
		int stageId = extraInfoMap.get("stageId") != null ? Integer.valueOf(extraInfoMap.get("stageId")) : 0;
		int result = goalDao.modifyStage(stageId, stageCostHours, stageProgress, stageId);

		Map<String, Object> model = new HashMap<String, Object>();

		model.put("status", result > 0 ? 1 : 0);
		return responseJson(model);
	}

}
