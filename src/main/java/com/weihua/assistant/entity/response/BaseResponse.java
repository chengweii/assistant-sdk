package com.weihua.assistant.entity.response;

public class BaseResponse implements Response {

	private String content;

	private String metaData;

	public BaseResponse(String response) {
		this.content = response;
	}

	@Override
	public String getContent() {
		return this.content;
	}

	public void setMetaData(String metaData) {
		this.metaData = metaData;
	}

	public String getMetaData() {
		return this.metaData;
	}

}
