package com.weihua.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.codec.binary.Base64;

import gui.ava.html.image.generator.HtmlImageGenerator;

public class ImageUtil {

	/**
	 * 图片文件转base64Data
	 * @param imgFilePath
	 * @return
	 */
	public static String encodeToString(String imgFilePath) {
		InputStream inputStream = null;
		byte[] data = null;
		try {
			inputStream = new FileInputStream(imgFilePath);
			data = new byte[inputStream.available()];
			inputStream.read(data);
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Base64 encoder = new Base64();
		return encoder.encodeToString(data);
	}

	/**
	 * base64Data转图片文件
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
	 * @param html
	 * @param imgFilePath
	 */
	public static void generateImageFromHtml(String html, String imgFilePath) {
		HtmlImageGenerator imageGenerator = new HtmlImageGenerator();
		imageGenerator.loadHtml(html);
		imageGenerator.getBufferedImage();
		imageGenerator.saveAsImage(imgFilePath);
	}

	public static void main(String[] args) {
		generateImageFromHtml("<div><img src='http://zxpic.imtt.qq.com/zxpic_imtt/2017/07/02/1910/originalimage/191645_1466236765_1_550_367.jpg'><p>编号:10001</p></div>",
				"E:/10001.png");
		System.out.println(encodeToString("E:/10001.png"));
	}

}
