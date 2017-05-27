package com.weihua.util;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailUtil {

	private static Properties props = System.getProperties();

	private static String defaultKeySmtp = "mail.smtp.host";
	private static String defaultValueSmtp = "smtp.163.com";

	private static String defaultSendUser;
	private static String defaultSendUname;
	private static String defaultSendPwd;

	private static String defaultSendNickName = "我的助手";

	private static String defaultReceiveUser;

	static {
		props.put("mail.smtp.auth", "true");
	}

	public static void init(String sendUser, String sendPwd, String receiveUser) {
		defaultSendUser = sendUser;
		if (sendUser != null && sendUser.contains("@"))
			defaultSendUname = sendUser.substring(0, sendUser.indexOf("@"));
		defaultSendPwd = sendPwd;
		defaultReceiveUser = receiveUser;
	}

	private EmailUtil() {
	}

	public static boolean sendEmail(final SendEmailInfo sendEmailInfo) {
		try {
			props.setProperty(sendEmailInfo.getKeySmtp(), sendEmailInfo.getValueSmtp());

			Session session = Session.getDefaultInstance(props, new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(sendEmailInfo.getSendUname(), sendEmailInfo.getSendPwd());
				}
			});
			//session.setDebug(true);
			MimeMessage message = new MimeMessage(session);

			String nickName = javax.mail.internet.MimeUtility.encodeText(sendEmailInfo.getSendNickName());
			InternetAddress from = new InternetAddress(nickName + " <" + sendEmailInfo.getSendUser() + ">");
			message.setFrom(from);
			InternetAddress to = new InternetAddress(sendEmailInfo.getReceiveUser());
			message.setRecipient(Message.RecipientType.TO, to);
			message.setSubject(sendEmailInfo.getHeadName());
			String content = sendEmailInfo.getSendHtml().toString();
			message.setContent(content, "text/html;charset=GBK");
			message.saveChanges();
			Transport transport = session.getTransport("smtp");
			transport.connect(sendEmailInfo.getValueSmtp(), sendEmailInfo.getSendUname(), sendEmailInfo.getSendPwd());
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
			return true;
		} catch (Exception e) {

			return false;
		}
	}

	public static class SendEmailInfo {

		private String keySmtp;
		private String valueSmtp;

		private String sendUser;
		private String sendUname;
		private String sendPwd;
		private String sendNickName;

		private String receiveUser;

		private String headName;
		private String sendHtml;

		public String getKeySmtp() {
			return keySmtp == null ? defaultKeySmtp : keySmtp;
		}

		public void setKeySmtp(String keySmtp) {
			this.keySmtp = keySmtp;
		}

		public String getValueSmtp() {
			return valueSmtp == null ? defaultValueSmtp : valueSmtp;
		}

		public void setValueSmtp(String valueSmtp) {
			this.valueSmtp = valueSmtp;
		}

		public String getSendUser() {
			return sendUser == null ? defaultSendUser : sendUser;
		}

		public void setSendUser(String sendUser) {
			this.sendUser = sendUser;
		}

		public String getSendUname() {
			return sendUname == null ? defaultSendUname : sendUname;
		}

		public void setSendUname(String sendUname) {
			this.sendUname = sendUname;
		}

		public String getSendPwd() {
			return sendPwd == null ? defaultSendPwd : sendPwd;
		}

		public void setSendPwd(String sendPwd) {
			this.sendPwd = sendPwd;
		}

		public String getSendNickName() {
			return sendNickName == null ? defaultSendNickName : sendNickName;
		}

		public void setSendNickName(String sendNickName) {
			this.sendNickName = sendNickName;
		}

		public String getReceiveUser() {
			return receiveUser == null ? defaultReceiveUser : receiveUser;
		}

		public void setReceiveUser(String receiveUser) {
			this.receiveUser = receiveUser;
		}

		public String getHeadName() {
			return headName;
		}

		public void setHeadName(String headName) {
			this.headName = headName;
		}

		public String getSendHtml() {
			return sendHtml;
		}

		public void setSendHtml(String sendHtml) {
			this.sendHtml = sendHtml;
		}
	}

	public static void main(String[] args) {
		SendEmailInfo info = new SendEmailInfo();
		info.setHeadName("您上午的待办事项有8件，挺累的，加油吧！");
		info.setSendHtml("邮件内容");
		info.setSendUser("3434@163.com");
		info.setSendUname("3434");
		info.setSendNickName("我的助手");
		info.setSendPwd("343434");
		info.setReceiveUser("34343434@qq.com");

		EmailUtil.sendEmail(info);

		info.setHeadName("您上午的對對對有202件，挺累的，加油吧！");
		info.setSendHtml("水電費水電費内容");

		EmailUtil.sendEmail(info);
	}
}
