package com.weihua.assistant.entity.request;

import com.google.gson.reflect.TypeToken;
import com.weihua.assistant.constant.AssistantType;
import com.weihua.assistant.constant.OriginType;
import com.weihua.util.GsonUtil;

public class BaseRequest implements Request {

	public static final String REQUEST_PARAM_KEY = "requestContent";

	private OriginType originType;

	private AssistantType originAssistantType;
	
	private AssistantType assistantType;

	private Boolean isLocationPath;

	private String requestContent;

	private String extraInfo;

	private String originRequest;

	public BaseRequest(String request) {
		deserializeRequest(request);
	}

	private void deserializeRequest(String request) {
		RequestData requestData = GsonUtil.getEntityFromJson(request, new TypeToken<RequestData>() {
		});
		if (requestData != null) {
			this.originAssistantType = AssistantType.fromCode(requestData.assistantType);
			this.originType = OriginType.fromCode(requestData.originType);
			this.isLocationPath = requestData.isLocationPath;
			this.requestContent = requestData.requestContent;
			this.extraInfo = GsonUtil.toJson(requestData.extraInfo);
			this.originRequest = request;
		}
	}

	public static class RequestData {
		public String assistantType;
		public String originType;
		public Boolean isLocationPath;
		public String requestContent;
		public Object extraInfo;
	}

	public AssistantType getAssistantType() {
		return assistantType;
	}

	public void setAssistantType(AssistantType assistantType) {
		this.assistantType = assistantType;
	}

	public AssistantType getOriginAssistantType() {
		return originAssistantType;
	}

	public void setOriginAssistantType(AssistantType originAssistantType) {
		this.originAssistantType = originAssistantType;
	}

	public OriginType getOriginType() {
		return this.originType;
	}

	public void setOriginType(OriginType originType) {
		this.originType = originType;
	}

	public String getExtraInfo() {
		return this.extraInfo;
	}

	public void setExtraInfo(String extraInfo) {
		this.extraInfo = extraInfo;
	}

	public Boolean isLocationPath() {
		return this.isLocationPath;
	}

	public String getOriginRequest() {
		return this.originRequest;
	}

	@Override
	public String getContent() {
		return this.requestContent;
	}

}
