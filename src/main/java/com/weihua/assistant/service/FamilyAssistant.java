package com.weihua.assistant.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.weihua.assistant.constant.AssistantConstant;
import com.weihua.assistant.entity.request.BaseRequest;
import com.weihua.assistant.entity.request.Request;
import com.weihua.assistant.entity.response.Response;
import com.weihua.assistant.service.annotation.ServiceLocation;
import com.weihua.assistant.service.base.BaseAssistant;
import com.weihua.database.dao.FamilyDao;
import com.weihua.database.dao.impl.FamilyDaoImpl;
import com.weihua.util.ExceptionUtil;
import com.weihua.util.GsonUtil;

/**
 * @author chengwei2
 * @category Family service:housework,plan,future,commodity purchasing;
 */
public class FamilyAssistant extends BaseAssistant {

	private static Logger LOGGER = Logger.getLogger(FamilyAssistant.class);

	private static FamilyDao familyDao = new FamilyDaoImpl();

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
}
