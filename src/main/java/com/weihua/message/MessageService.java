package com.weihua.message;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.weihua.util.EmailUtil;
import com.weihua.util.StringUtil;
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
					sendEmailInfo.setSendHtml(serialize(messageContent));
					EmailUtil.sendEmail(sendEmailInfo);
				} catch (Exception e) {
					LOGGER.error("Send message error：[messageSubject:" + messageSubject + ",messageContent:" + messageContent + "]",
							e);
				}
			}
		});
	}

	private static String serialize(String content) {
		if (!StringUtil.isEmpty(content)) {
			return MessageConstant.MAIL_CONTENT_START + content + MessageConstant.MAIL_CONTENT_END;
		}
		return content;
	}
}
