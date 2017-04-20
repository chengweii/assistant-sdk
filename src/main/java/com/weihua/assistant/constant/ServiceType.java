package com.weihua.assistant.constant;

public enum ServiceType {

	FRONT_SERVICE("1", "前台服务"), 
	BACK_SERVICE("2", "后台服务");

	private ServiceType(String code, String value) {
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

	public static ServiceType fromCode(String code) {
		for (ServiceType entity : ServiceType.values()) {
			if (entity.getCode().equals(code)) {
				return entity;
			}
		}
		return null;
	}

}
