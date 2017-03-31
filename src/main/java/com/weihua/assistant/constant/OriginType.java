package com.weihua.assistant.constant;

public enum OriginType {

	WEB("1", "web"), MOBILE("2", "mobile");

	private OriginType(String code, String value) {
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

	public static OriginType fromCode(String code) {
		for (OriginType entity : OriginType.values()) {
			if (entity.getCode().equals(code)) {
				return entity;
			}
		}
		return WEB;
	}
}
