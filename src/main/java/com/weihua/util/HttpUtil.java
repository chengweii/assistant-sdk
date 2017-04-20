package com.weihua.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

public class HttpUtil {

	private static Logger LOGGER = Logger.getLogger(HttpUtil.class);

	private static final int CONNECT_TIMEOUT = 5000;

	private static final int READ_TIMEOUT = 5000;

	private static final String CHARSET = "UTF-8";

	private static enum HttpMethod {
		POST, DELETE, GET, PUT, HEAD;
	};

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static String invokeUrl(String url, Map params, Map<String, String> headers, int connectTimeout,
			int readTimeout, String encoding, HttpMethod method) {
		StringBuilder paramsStr = null;
		if (params != null) {
			paramsStr = new StringBuilder();
			Set<Map.Entry> entries = params.entrySet();
			for (Map.Entry entry : entries) {
				String value = (entry.getValue() != null) ? (String.valueOf(entry.getValue())) : "";
				paramsStr.append(entry.getKey() + "=" + value + "&");
			}
			if (method != HttpMethod.POST) {
				url += "?" + paramsStr.toString();
			}
		}

		URL uUrl = null;
		HttpURLConnection conn = null;
		BufferedWriter out = null;
		BufferedReader in = null;
		try {
			uUrl = new URL(url);
			conn = (HttpURLConnection) uUrl.openConnection();
			conn.setRequestProperty("content-type", "application/x-www-form-urlencoded");
			conn.setRequestMethod(method.toString());
			// conn.setDoOutput(true);
			// conn.setDoInput(true);
			conn.setConnectTimeout(connectTimeout);
			conn.setReadTimeout(readTimeout);

			if (headers != null && headers.size() > 0) {
				Set<String> headerSet = headers.keySet();
				for (String key : headerSet) {
					conn.setRequestProperty(key, headers.get(key));
				}
			}

			if (paramsStr != null && method == HttpMethod.POST) {
				out = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), encoding));
				out.write(paramsStr.toString());
				out.flush();
			}

			StringBuilder result = new StringBuilder();
			in = new BufferedReader(new InputStreamReader(conn.getInputStream(), encoding));
			if (in != null) {
				String line = "";
				while ((line = in.readLine()) != null) {
					result.append(line);
				}
			}
			return result.toString();
		} catch (Exception e) {
			LOGGER.error("Request failed,request URL:" + url + ",params:" + params, e);
			try {
				byte[] buf = new byte[100];
				InputStream es = conn.getErrorStream();
				if (es != null) {
					while (es.read(buf) > 0) {
						;
					}
					es.close();
				}
			} catch (Exception e1) {
				LOGGER.error("Retry request failed,request URL:" + url + ",params:" + params, e1);
			}
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (Exception e) {
				LOGGER.error("BufferedWriter close failed", e);
			}
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e) {
				LOGGER.error("BufferedReader close failed", e);
			}
			if (conn != null) {
				conn.disconnect();
			}
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	public static String post(String url, Map params, int connectTimeout, int readTimeout, String charset) {
		return invokeUrl(url, params, null, connectTimeout, readTimeout, charset, HttpMethod.POST);
	}

	@SuppressWarnings("rawtypes")
	public static String post(String url, Map params, Map<String, String> headers, int connectTimeout, int readTimeout,
			String charset) {
		return invokeUrl(url, params, headers, connectTimeout, readTimeout, charset, HttpMethod.POST);
	}

	@SuppressWarnings("rawtypes")
	public static String post(String url, Map params, Map<String, String> headers) {
		return invokeUrl(url, params, headers, CONNECT_TIMEOUT, READ_TIMEOUT, CHARSET, HttpMethod.POST);
	}

	@SuppressWarnings("rawtypes")
	public static String get(String url, Map params, int connectTimeout, int readTimeout, String charset) {
		return invokeUrl(url, params, null, connectTimeout, readTimeout, charset, HttpMethod.GET);
	}

	@SuppressWarnings("rawtypes")
	public static String get(String url, Map params, Map<String, String> headers, int connectTimeout, int readTimeout,
			String charset) {
		return invokeUrl(url, params, headers, connectTimeout, readTimeout, charset, HttpMethod.GET);
	}

	@SuppressWarnings("rawtypes")
	public static String get(String url, Map params, Map<String, String> headers) {
		return invokeUrl(url, params, headers, CONNECT_TIMEOUT, READ_TIMEOUT, CHARSET, HttpMethod.GET);
	}

	@SuppressWarnings("rawtypes")
	public static String put(String url, Map params, int connectTimeout, int readTimeout, String charset) {
		return invokeUrl(url, params, null, connectTimeout, readTimeout, charset, HttpMethod.PUT);
	}

	@SuppressWarnings("rawtypes")
	public static String put(String url, Map params, Map<String, String> headers, int connectTimeout, int readTimeout,
			String charset) {
		return invokeUrl(url, params, headers, connectTimeout, readTimeout, charset, HttpMethod.PUT);
	}

	@SuppressWarnings("rawtypes")
	public static String delete(String url, Map params, int connectTimeout, int readTimeout, String charset) {
		return invokeUrl(url, params, null, connectTimeout, readTimeout, charset, HttpMethod.DELETE);
	}

	@SuppressWarnings("rawtypes")
	public static String delete(String url, Map params, Map<String, String> headers, int connectTimeout,
			int readTimeout, String charset) {
		return invokeUrl(url, params, headers, connectTimeout, readTimeout, charset, HttpMethod.DELETE);
	}

	@SuppressWarnings("rawtypes")
	public static String head(String url, Map params, int connectTimeout, int readTimeout, String charset) {
		return invokeUrl(url, params, null, connectTimeout, readTimeout, charset, HttpMethod.HEAD);
	}

	@SuppressWarnings("rawtypes")
	public static String head(String url, Map params, Map<String, String> headers, int connectTimeout, int readTimeout,
			String charset) {
		return invokeUrl(url, params, headers, connectTimeout, readTimeout, charset, HttpMethod.HEAD);
	}

	public static void main(String[] args) {
		String content = get("http://web.sqt.gtimg.cn/q=sz000651?r=0.6450336522583517",null, 5000, 5000,"GBK");
		System.out.println(content);
	}

}
