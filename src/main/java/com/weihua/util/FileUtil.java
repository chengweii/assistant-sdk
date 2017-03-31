package com.weihua.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileUtil {

	private static String CHARSET_NAME = "UTF-8";

	public static void writeFileContent(String content, String filePath) throws IOException {
		File file = new File(filePath);
		if (!file.exists())
			file.createNewFile();
		FileOutputStream out = new FileOutputStream(file, false);
		out.write(content.getBytes(CHARSET_NAME));
		out.close();
	}

	public static String getFileContent(String filePath) throws IOException {
		String line;
		StringBuilder stringBuilder = new StringBuilder();
		InputStreamReader streamReader = new InputStreamReader(new FileInputStream(new File(filePath)), CHARSET_NAME);
		BufferedReader reader = new BufferedReader(streamReader);
		while ((line = reader.readLine()) != null) {
			stringBuilder.append(line);
		}
		reader.close();
		return stringBuilder.toString();
	}

	public static boolean isFileExists(String filePath) {
		File file = new File(filePath);
		return file.exists();
	}

}
