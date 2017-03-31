package com.weihua.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.FileResourceLoader;
import org.beetl.core.resource.StringTemplateResourceLoader;

public class TemplateUtil {

	private static Logger LOGGER = Logger.getLogger(HttpUtil.class);

	private static String CHARSET = "UTF-8";

	private static Map<String, GroupTemplate> fileGroupTemplateMap = new HashMap<String, GroupTemplate>();

	private static GroupTemplate StringGroupTemplate = null;

	private static TemplateReader customerReader = null;

	static {
		initStringGroupTemplate();
	}

	public static void initTemplateReader(TemplateReader templateReader) {
		customerReader = templateReader;
	}

	private static void initStringGroupTemplate() {
		StringTemplateResourceLoader resourceLoader = new StringTemplateResourceLoader();
		Configuration cfg;
		try {
			cfg = Configuration.defaultConfiguration();
			StringGroupTemplate = new GroupTemplate(resourceLoader, cfg);
		} catch (IOException e) {
			LOGGER.error("FileGroupTemplate init failed.", e);
		}
	}

	public static String renderByTemplateFile(String templateBasePath, String templatePath,
			Map<String, Object> params) {

		GroupTemplate fileGroupTemplate = null;

		if (fileGroupTemplateMap.containsKey(templateBasePath)) {
			fileGroupTemplate = fileGroupTemplateMap.get(templateBasePath);
		} else {
			FileResourceLoader resourceLoader = new FileResourceLoader(templateBasePath, CHARSET);
			Configuration cfg = null;

			try {
				cfg = Configuration.defaultConfiguration();
				fileGroupTemplate = new GroupTemplate(resourceLoader, cfg);
			} catch (IOException e) {
				LOGGER.error("FileGroupTemplate init failed.", e);
			}
		}

		Template template = fileGroupTemplate.getTemplate(templatePath);
		if (params != null) {
			for (Map.Entry<String, Object> entry : params.entrySet()) {
				template.binding(entry.getKey(), entry.getValue());
			}
		}

		return template.render();
	}

	public static String renderByTemplateContent(String templateContent, Map<String, Object> params) {
		Template template = StringGroupTemplate.getTemplate(templateContent);
		if (params != null) {
			for (Map.Entry<String, Object> entry : params.entrySet()) {
				template.binding(entry.getKey(), entry.getValue());
			}
		}
		return template.render();
	}

	public static String renderByTemplateReader(String templateName, Map<String, Object> params) {
		String templateContent = customerReader.getTemplateContent(templateName);
		return renderByTemplateContent(templateContent, params);
	}

	public static void main(String[] args) {
		Map<String, Object> paras = new HashMap<String, Object>();
		paras.put("userName", "beetl");

		String templateBasePath = TemplateUtil.class.getResource("/").toString().replace("file:/", "")
				+ TemplateReader.TEMPLATE_ROOT + "/";
		String result = TemplateUtil.renderByTemplateFile(templateBasePath, "default.htm", paras);
		System.out.println(result);

		paras.put("userName", "wakak");
		String result2 = TemplateUtil.renderByTemplateContent("hello,${userName}", paras);
		System.out.println(result2);
	}

	public interface TemplateReader {
		String TEMPLATE_ROOT = "assets";

		String getTemplateContent(String templateName);
	}

}
