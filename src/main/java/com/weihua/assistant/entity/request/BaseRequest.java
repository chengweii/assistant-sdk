package com.weihua.assistant.entity.request;

import com.google.gson.reflect.TypeToken;
import com.weihua.assistant.constant.AssistantType;
import com.weihua.assistant.constant.OriginType;
import com.weihua.util.GsonUtil;

public class BaseRequest implements Request {

	public static final String REQUEST_PARAM_KEY = "requestContent";

	private OriginType originType;

	private AssistantType assistantType;

	private String function;

	private String requestContent;

	public BaseRequest(String request) {
		deserializeRequest(request);
	}

	private void deserializeRequest(String request) {
		RequestData requestData = GsonUtil.getEntityFromJson(request, new TypeToken<RequestData>() {
		});
		if (requestData != null) {
			this.assistantType = AssistantType.fromCode(requestData.assistantType);
			this.originType = OriginType.fromCode(requestData.originType);
			this.function = requestData.function;
			this.requestContent = requestData.requestContent;
		}
	}

	public static class RequestData {
		public String assistantType;
		public String originType;
		public String function;
		public String requestContent;
	}

	public AssistantType getAssistantType() {
		return this.assistantType;
	}

	public OriginType getOriginType() {
		return this.originType;
	}

	public String getFunction() {
		return this.function;
	}

	@Override
	public String getContent() {
		return this.requestContent;
	}

}
