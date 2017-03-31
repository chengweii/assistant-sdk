package com.weihua.util;

import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class GsonUtil {

	public static Gson gson = null;

	static {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gson = gsonBuilder.setPrettyPrinting().serializeNulls().create();
	}

	public static Map<String, String> getMapFromJson(String json) throws JsonSyntaxException {
		if (StringUtil.isEmpty(json))
			return null;

		Map<String, String> map = getEntityFromJson(json, new TypeToken<Map<String, String>>() {
		});
		return map;
	}

	public static <T> T getEntityFromJson(String json, TypeToken<?> typeToken) throws JsonSyntaxException {
		if (StringUtil.isEmpty(json))
			return null;

		T data = GsonUtil.gson.fromJson(json, typeToken.getType());
		return data;
	}

	public static String toJson(Object src) throws JsonSyntaxException {
		return GsonUtil.gson.toJson(src);
	}

	public static void main(String[] args) {
	}

}
