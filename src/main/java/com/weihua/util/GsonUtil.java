package com.weihua.util;

import java.util.Map;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class GsonUtil {

	private static Logger LOGGER = Logger.getLogger(GsonUtil.class);

	public static Gson gson = null;

	static {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gson = gsonBuilder.setPrettyPrinting().serializeNulls().create();
	}

	public static Map<String, String> getMapFromJson(String json) {
		if (StringUtil.isEmpty(json))
			return null;

		Map<String, String> map = null;
		try {
			map = getEntityFromJson(json, new TypeToken<Map<String, String>>() {
			});
		} catch (Exception e) {
			LOGGER.info("Json string is :" + json);
			ExceptionUtil.propagate(LOGGER, e);
		}
		return map;
	}

	public static <T> T getEntityFromJson(String json, TypeToken<?> typeToken) {
		if (StringUtil.isEmpty(json))
			return null;

		T data = null;
		try {
			data = GsonUtil.gson.fromJson(json, typeToken.getType());
		} catch (Exception e) {
			LOGGER.info("Json string is :" + json);
			ExceptionUtil.propagate(LOGGER, e);
		}
		return data;
	}

	public static String toJson(Object src) throws JsonSyntaxException {
		return GsonUtil.gson.toJson(src);
	}

	public static void main(String[] args) {
	}

}
