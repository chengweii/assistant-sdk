package com.weihua.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.log4j.Logger;

public class ExceptionUtil {

	public static String getStackTrace(Exception e) {
		StringWriter sw = null;
		PrintWriter pw = null;
		try {
			sw = new StringWriter();
			pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			pw.flush();
			sw.flush();
		} finally {
			if (sw != null) {
				try {
					sw.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if (pw != null) {
				pw.close();
			}
		}
		return sw.toString();
	}

	public static void propagate(Logger logger, Exception e) {
		logger.error(e.getMessage(), e);
		throw new RuntimeException(e);
	}
}
