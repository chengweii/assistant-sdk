package com.weihua.message;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.weihua.util.EmailUtil;
import com.weihua.util.EmailUtil.SendEmailInfo;

public class MessageService {
	private static Logger LOGGER = Logger.getLogger(MessageService.class);
	private static ExecutorService executor;

	static {
		executor = Executors.newCachedThreadPool();
	}

	public static void send(final String messageSubject, final String messageContent) {
		executor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					SendEmailInfo sendEmailInfo = new SendEmailInfo();
					sendEmailInfo.setHeadName(messageSubject);
					sendEmailInfo.setSendHtml(messageContent);
					EmailUtil.sendEmail(sendEmailInfo);
				} catch (Exception e) {
					LOGGER.error("消息发送异常：[messageSubject:" + messageSubject + ",messageContent:" + messageContent + "]",
							e);
				}
			}
		});
	}
}
