package com.weihua.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import com.google.common.io.ByteStreams;

import gui.ava.html.image.generator.HtmlImageGenerator;

public class ImageUtil {

	private static Logger LOGGER = Logger.getLogger(ImageUtil.class);

	/**
	 * 图片文件转base64Data
	 * 
	 * @param imgFilePath
	 * @param imageSource
	 * @return
	 */
	public static String encodeToString(String imgFilePath, ImageSource imageSource) {
		InputStream inputStream = null;
		byte[] data = null;
		HttpURLConnection conn = null;
		try {
			if (imageSource == ImageSource.WEB_FOR_HTTP) {
				URL url = new URL(imgFilePath);
				conn = (HttpURLConnection) url.openConnection();
				conn.setDoInput(true);
				conn.setConnectTimeout(5000);
				conn.connect();
				inputStream = conn.getInputStream();
			} else {
				inputStream = new FileInputStream(imgFilePath);
			}
			data = ByteStreams.toByteArray(inputStream);
		} catch (Exception e) {
			LOGGER.error("encodeToString error", e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					LOGGER.error("close inputStream error");
				}
			}
		}
		Base64 encoder = new Base64();
		return encoder.encodeToString(data);
	}

	/**
	 * base64Data转图片文件
	 * 
	 * @param base64Data
	 * @param imgFilePath
	 * @return
	 */
	public static boolean decoderToImage(String base64Data, String imgFilePath) {
		if (base64Data == null)
			return false;
		Base64 decoder = new Base64();
		try {
			byte[] b = decoder.decode(base64Data);
			for (int i = 0; i < b.length; ++i) {
				if (b[i] < 0) {
					b[i] += 256;
				}
			}
			OutputStream out = new FileOutputStream(imgFilePath);
			out.write(b);
			out.flush();
			out.close();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * html转成图片（注意：图片链接仅支持http不支持https）
	 * 
	 * @param html
	 * @param imgFilePath
	 */
	public static void generateImageFromHtml(String html, String imgFilePath) {
		HtmlImageGenerator imageGenerator = new HtmlImageGenerator();
		imageGenerator.loadHtml(html);
		imageGenerator.getBufferedImage();
		imageGenerator.saveAsImage(imgFilePath);
	}

	enum ImageSource {
		LOCAL, WEB_FOR_HTTP
	}

	public static void main(String[] args) throws IOException {

		generateImageFromHtml(FileUtil.getFileContent("C:/Users/Administrator/Desktop/临时/email.htm"), "E:/10001.png");
		//System.out.println(encodeToString("E:/10001.png",ImageSource.LOCAL));
		System.out.println(encodeToString("http://upload-images.jianshu.io/upload_images/2986704-4e9d3dfd16cfd418.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240",ImageSource.WEB_FOR_HTTP));
	}

}
