package com.weihua.assistant.constant;

public enum FoodType {

	COMPLEMENTARY("1", "辅食"), BREAKFAST("2", "早餐"), LUNCH("3", "午餐"), DINNER("4", "晚餐");

	private FoodType(String code, String value) {
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

	public static FoodType fromCode(String code) {
		for (FoodType entity : FoodType.values()) {
			if (entity.getCode().equals(code)) {
				return entity;
			}
		}
		return null;
	}
}
