package com.weihua.assistant.constant;

import com.weihua.assistant.service.BaseAssistant;

public enum AssistantType {

	MAIN_ASSISTANT("111", BaseAssistant.class.getName());

	private AssistantType(String code, String value) {
		this.code = code;
		this.value = value;
	}

	private String code;
	private String value;

	public String getCode() {
		return code;
	}

	public String getValue() {
		return value;
	}

	public static AssistantType fromCode(String code) {
		for (AssistantType entity : AssistantType.values()) {
			if (entity.getCode().equals(code)) {
				return entity;
			}
		}
		return MAIN_ASSISTANT;
	}
}
