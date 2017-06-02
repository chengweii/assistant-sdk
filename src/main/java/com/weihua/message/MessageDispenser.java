package com.weihua.message;

import java.util.List;

import org.apache.log4j.Logger;

import com.weihua.util.ClassUtil;
import com.weihua.util.EmailUtil;
import com.weihua.util.StringUtil;
import com.weihua.util.EmailUtil.EmailEntity;
import com.weihua.util.EmailUtil.ReceiveEmailInfo;

public class MessageDispenser {
	private static Logger LOGGER = Logger.getLogger(MessageDispenser.class);

	public static void notifyConsumers() {
		try {
			ReceiveEmailInfo rinfo = new ReceiveEmailInfo();
			rinfo.setDelete(true);
			List<EmailEntity> mailList = EmailUtil.receiveMail(rinfo);
			if (mailList != null && mailList.size() > 0) {
				LOGGER.info("Message count:" + mailList.size());
				for (EmailEntity entity : mailList) {
					String className = entity.getSubject();
					MessageConsumer consumer = ClassUtil.<MessageConsumer> getInstanceByClassName(className);
					if (consumer != null) {
						consumer.doHandle(deserialize(entity.getContent()));
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	private static String deserialize(String content) {
		if (!StringUtil.isEmpty(content)) {
			int start = content.indexOf(MessageConstant.MAIL_CONTENT_START);
			int end = content.indexOf(MessageConstant.MAIL_CONTENT_END);
			if (start != -1 && end > start) {
				return content.substring(MessageConstant.MAIL_CONTENT_START.length(), end);
			}
		}
		return content;
	}

}
