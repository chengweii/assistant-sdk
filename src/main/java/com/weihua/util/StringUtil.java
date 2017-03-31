package com.weihua.util;

import java.util.List;

import org.apache.log4j.Logger;

public class StringUtil {

	private static Logger LOGGER = Logger.getLogger(StringUtil.class);

	public static String getRandomContent(List<String> content) {
		String randomContent = null;
		if (content != null && content.size() > 0) {
			int index = (int) (Math.random() * content.size());
			randomContent = content.get(index);
		}
		return randomContent;
	}

	public static Integer stringToInteger(String in) {
		if (in == null) {
			return null;
		}
		in = in.trim();
		if (in.length() == 0) {
			return null;
		}

		try {
			return Integer.parseInt(in);
		} catch (NumberFormatException e) {
			LOGGER.warn("stringToInteger fail,string=" + in, e);
			return null;
		}
	}

	public static boolean equals(String a, String b) {
		if (a == null) {
			return b == null;
		}
		return a.equals(b);
	}

	public static boolean isEmpty(String value) {
		if (value == null || value.length() == 0) {
			return true;
		}
		return false;
	}

}
