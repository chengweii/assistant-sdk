package com.weihua.util;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

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

	public static void init(ResourceBundle emailBundle) {
		if (emailBundle != null) {
			for (String key : emailBundle.keySet()) {
				properties.put(key, emailBundle.getString(key));
			}
			inited = true;
		}
	}
}
