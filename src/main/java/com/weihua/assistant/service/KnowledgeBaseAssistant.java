package com.weihua.assistant.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.weihua.assistant.entity.request.BaseRequest;
import com.weihua.assistant.entity.request.Request;
import com.weihua.assistant.entity.response.Response;
import com.weihua.assistant.service.annotation.ServiceLocation;
import com.weihua.database.dao.KnowledgeListDao;
import com.weihua.database.dao.impl.KnowledgeListDaoImpl;
import com.weihua.util.ExceptionUtil;

/**
 * @author chengwei2
 * @category 知识库
 */
public class KnowledgeBaseAssistant extends BaseAssistant {

	private static Logger LOGGER = Logger.getLogger(KnowledgeBaseAssistant.class);

	private static KnowledgeListDao knowledgeListDao = new KnowledgeListDaoImpl();

	@Override
	public Response getResponse(Request request) {
		Response response = null;
		try {
			BaseRequest baseRequest = (BaseRequest) request;
			if (baseRequest.isLocationPath() == null || baseRequest.isLocationPath() == false) {
				response = getKnowledgeBase((BaseRequest) request);
			} else {
				response = invokeLocationMethod((BaseRequest) request);
			}
		} catch (Exception e) {
			ExceptionUtil.propagate(LOGGER, e);
		}
		return response;
	}

	@ServiceLocation(value = "getKnowledgeBase")
	public Response getKnowledgeBase(BaseRequest request) {
		
		Map<String, Object> model = new HashMap<String, Object>();
		List<Map<String, Object>> knowledgeList = knowledgeListDao.findRecordList();
		model.put("knowledgeList", knowledgeList);
		return response(model, "knowledgebase/index");
	}
}
