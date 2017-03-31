package com.weihua.assistant.service;

import com.weihua.assistant.entity.request.Request;
import com.weihua.assistant.entity.response.Response;

public interface Assistant {
	Response getResponse(Request request);
}
