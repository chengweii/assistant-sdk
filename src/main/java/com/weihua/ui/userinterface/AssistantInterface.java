package com.weihua.ui.userinterface;

import com.weihua.assistant.service.MainAssistant;

public class AssistantInterface implements UserInterface {

	@Override
	public String getResponse(String request) {
		MainAssistant mainAssistant = new MainAssistant();
		return mainAssistant.execute(request);
	}
}
