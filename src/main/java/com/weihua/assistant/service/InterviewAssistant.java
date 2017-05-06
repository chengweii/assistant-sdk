package com.weihua.assistant.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.weihua.assistant.entity.request.BaseRequest;
import com.weihua.assistant.entity.request.Request;
import com.weihua.assistant.entity.response.Response;
import com.weihua.assistant.service.annotation.ServiceLocation;
import com.weihua.database.dao.InterviewDao;
import com.weihua.database.dao.impl.InterviewDaoImpl;
import com.weihua.util.ExceptionUtil;
import com.weihua.util.GsonUtil;

public class InterviewAssistant extends BaseAssistant {

	private static Logger LOGGER = Logger.getLogger(InterviewAssistant.class);

	private static InterviewDao interviewDao = new InterviewDaoImpl();

	@Override
	public Response getResponse(Request request) {
		Response response = null;
		try {
			BaseRequest baseRequest = (BaseRequest) request;
			if (baseRequest.isLocationPath() == null || baseRequest.isLocationPath() == false) {
				response = getSubjectList((BaseRequest) request);
			} else {
				response = invokeLocationMethod((BaseRequest) request);
			}
		} catch (Exception e) {
			ExceptionUtil.propagate(LOGGER, e);
		}
		return response;
	}

	@ServiceLocation(value = "getSubjectList")
	public Response getSubjectList(BaseRequest request) {
		String extraInfo = request.getExtraInfo();
		Map<String, String> extraInfoMap = GsonUtil.getMapFromJson(extraInfo);
		int beginRowNum = 0;
		int rowCount = 3;
		if (extraInfoMap != null) {
			beginRowNum = Integer.valueOf(extraInfoMap.get("beginRowNum"));
			rowCount = Integer.valueOf(extraInfoMap.get("rowCount"));
		}

		List<Map<String, Object>> result = interviewDao.findSubjectList(beginRowNum, rowCount);

		Map<String, Object> model = new HashMap<String, Object>();

		model.put("subjectList", result);
		model.put("beginRowNum", result != null && result.size() < rowCount ? beginRowNum : beginRowNum + rowCount);
		model.put("rowCount", rowCount);
		return response(model, "interview/index");
	}

	@ServiceLocation(value = "getSubjectById")
	public Response getSubjectById(BaseRequest request) {
		String extraInfo = request.getExtraInfo();
		Map<String, String> extraInfoMap = GsonUtil.getMapFromJson(extraInfo);
		int subjectId = extraInfoMap.get("subjectId") != null ? Integer.valueOf(extraInfoMap.get("subjectId")) : 0;
		Map<String, Object> result = interviewDao.findSubjectById(subjectId);

		Map<String, Object> model = new HashMap<String, Object>();

		model.put("subject", result);
		return response(model, "interview/item");
	}

}
