package com.weihua.assistant.entity.response;

public class BaseResponse implements Response {

	private String content;

	public BaseResponse(String response) {
		this.content = response;
	}

	@Override
	public String getContent() {
		return this.content;
	}

}
