package com.weihua.util;

import org.apache.log4j.Logger;

public class ClassUtil {
	private static Logger LOGGER = Logger.getLogger(ClassUtil.class);

	@SuppressWarnings("unchecked")
	public static <T> T getInstanceByClassName(String className) {
		try {
			Class<?> clz = Class.forName(className);
			return (T) clz.newInstance();
		} catch (Exception e) {
			ExceptionUtil.propagate(LOGGER, e);
		}
		return null;
	}

}
