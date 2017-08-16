package com.weihua.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public class ConfigUtil {
	private static Logger LOGGER = Logger.getLogger(ClassUtil.class);
	private static Map<String, String> properties = new HashMap<String, String>();

	public static Map<String, String> getProperties() {
		if (!inited) {
			ExceptionUtil.propagate(LOGGER, new RuntimeException("please init ConfigUtil firstly"));
		}
		return properties;
	}

	private static boolean inited = false;

	public static void init(Map<String, String> configs) {
		if (configs != null) {
			for (String key : configs.keySet()) {
				properties.put(key, configs.get(key));
			}
			inited = true;

			EmailUtil.initDefaultEmailAccountInfo(properties.get("email.dataEmailUser"),
					properties.get("email.dataEmailUserPwd"), properties.get("email.remindEmailUser"),
					properties.get("email.notifyEmailUser"));
			DidaListUtil.initDidaListUtil(properties.get("didalist.username"), properties.get("didalist.password"));
		}
	}
}
