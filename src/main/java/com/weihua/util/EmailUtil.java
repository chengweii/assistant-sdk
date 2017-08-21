package com.weihua.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.URLName;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import javax.mail.search.AndTerm;
import javax.mail.search.FromStringTerm;
import javax.mail.search.SearchTerm;
import javax.mail.search.SubjectTerm;

import org.apache.log4j.Logger;

import com.sun.mail.pop3.POP3Message;

public class EmailUtil {

	private static Logger LOGGER = Logger.getLogger(EmailUtil.class);

	private static Properties props = System.getProperties();

	private static String defaultKeySmtp = "mail.smtp.host";
	private static String defaultValueSmtp = "smtp.163.com";

	private static String defaultSendUser;
	private static String defaultSendUname;
	private static String defaultSendPwd;

	private static String defaultSendNickName = "MyAssistant";

	private static String defaultReceiveUser;

	private static String defaultSyncSendUser;

	static {
		props.put("mail.smtp.auth", "true");
	}

	public static void initDefaultEmailAccountInfo(String dataEmailUser, String dataEmailUserPwd,
			String remindEmailUser, String notifyEmailUser) {
		defaultSendUser = dataEmailUser;
		if (dataEmailUser != null && dataEmailUser.contains("@"))
			defaultSendUname = dataEmailUser.substring(0, dataEmailUser.indexOf("@"));
		defaultSendPwd = dataEmailUserPwd;
		defaultReceiveUser = remindEmailUser;
		defaultSyncSendUser = notifyEmailUser;
	}

	private EmailUtil() {
	}

	private static int overFrequencyRecoveryTime = 12 * 60 * 60 * 1000;

	private static boolean overFrequencyProtect(int overFrequencyTime, Date lastOpreateTime, int overFrequencyCount,
			int lastOpreateCount) {
		long leftTime = 0;

		if (lastOpreateTime != null
				&& (leftTime = DateUtil.getDateDiff(new Date(), lastOpreateTime)) < overFrequencyTime) {
			try {
				LOGGER.error("overFrequencyProtect leftTime:" + leftTime);
				Thread.sleep(leftTime + 100);
			} catch (InterruptedException e) {
				LOGGER.error("overFrequencyProtect Interrupted failed");
				return true;
			}
		}

		if (lastOpreateCount < overFrequencyCount) {
			lastOpreateCount++;
			lastOpreateTime = new Date();
		} else {
			if (leftTime > overFrequencyRecoveryTime) {
				lastOpreateCount = 0;
			}
			LOGGER.error("overFrequencyProtect lastOpreateCount[" + lastOpreateCount + "] > overFrequencyCount["
					+ overFrequencyCount + "]:");
			return true;
		}

		return false;
	}

	private static final int sendEmailOverFrequencyTime = 5 * 1000;
	private static final int sendEmailOverFrequencyCount = 100;
	private static Date sendEmailLastOpreateTime = null;
	private static int sendEmailLastOpreateCount = 0;

	public static void sendEmail(final SendEmailInfo sendEmailInfo) {
		if (overFrequencyProtect(sendEmailOverFrequencyTime, sendEmailLastOpreateTime, sendEmailOverFrequencyCount,
				sendEmailLastOpreateCount)) {
			return;
		}

		try {
			props.setProperty(sendEmailInfo.getKeySmtp(), sendEmailInfo.getValueSmtp());

			Session session = Session.getDefaultInstance(props, new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(sendEmailInfo.getSendUname(), sendEmailInfo.getSendPwd());
				}
			});
			// session.setDebug(true);
			MimeMessage message = new MimeMessage(session);

			String nickName = javax.mail.internet.MimeUtility.encodeText(sendEmailInfo.getSendNickName());
			InternetAddress from = new InternetAddress(nickName + " <" + sendEmailInfo.getSendUser() + ">");
			message.setFrom(from);

			if (sendEmailInfo.getReceiveUser() != null && sendEmailInfo.getReceiveUser().contains(";")) {
				String[] receiveUsers = sendEmailInfo.getReceiveUser().replaceAll("[^0-9@a-zA-Z\\.\\;]*", "")
						.split(";");
				InternetAddress[] to = new InternetAddress[receiveUsers.length];
				int i = 0;
				for (String receiveUser : receiveUsers) {
					to[i] = new InternetAddress(receiveUser);
					i++;
				}
				message.setRecipients(Message.RecipientType.TO, to);
			} else {
				InternetAddress to = new InternetAddress(sendEmailInfo.getReceiveUser());
				message.setRecipient(Message.RecipientType.TO, to);
			}

			message.setSubject(sendEmailInfo.getHeadName());
			String content = sendEmailInfo.getSendHtml().toString();
			message.setContent(content, "text/html;charset=GBK");
			message.saveChanges();
			Transport transport = session.getTransport("smtp");
			transport.connect(sendEmailInfo.getValueSmtp(), sendEmailInfo.getSendUname(), sendEmailInfo.getSendPwd());
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
			LOGGER.info("Message sent successfully:" + content);
		} catch (Exception e) {
			ExceptionUtil.propagate(LOGGER, e);
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

	private static String getFrom(Message message) throws Exception {
		InternetAddress[] address = (InternetAddress[]) ((MimeMessage) message).getFrom();
		String from = address[0].getAddress();
		if (from == null) {
			from = "";
		}
		return from;
	}

	private static String getSubject(Message message) throws Exception {
		String subject = "";
		if (((MimeMessage) message).getSubject() != null) {
			subject = MimeUtility.decodeText(((MimeMessage) message).getSubject());
		}
		return subject;
	}

	public static void getMailContent(Part part, StringBuffer bodytext) throws Exception {
		String contenttype = part.getContentType();
		int nameindex = contenttype.indexOf("name");
		boolean conname = false;
		if (nameindex != -1)
			conname = true;
		LOGGER.info("CONTENTTYPE: " + contenttype);
		if (part.isMimeType("text/plain") && !conname) {
			bodytext.append((String) part.getContent());
		} else if (part.isMimeType("text/html") && !conname) {
			bodytext.append((String) part.getContent());
		} else if (part.isMimeType("multipart/*")) {
			Multipart multipart = (Multipart) part.getContent();
			int counts = multipart.getCount();
			for (int i = 0; i < counts; i++) {
				getMailContent(multipart.getBodyPart(i), bodytext);
			}
		} else if (part.isMimeType("message/rfc822")) {
			getMailContent((Part) part.getContent(), bodytext);
		} else {
		}
	}

	private static boolean isContainAttach(Part part) throws Exception {
		boolean attachflag = false;
		if (part.isMimeType("multipart/*")) {
			Multipart mp = (Multipart) part.getContent();
			for (int i = 0; i < mp.getCount(); i++) {
				BodyPart mpart = mp.getBodyPart(i);
				String disposition = mpart.getDisposition();
				if ((disposition != null)
						&& ((disposition.equals(Part.ATTACHMENT)) || (disposition.equals(Part.INLINE))))
					attachflag = true;
				else if (mpart.isMimeType("multipart/*")) {
					attachflag = isContainAttach((Part) mpart);
				} else {
					String contype = mpart.getContentType();
					if (contype.toLowerCase().indexOf("application") != -1)
						attachflag = true;
					if (contype.toLowerCase().indexOf("name") != -1)
						attachflag = true;
				}
			}
		} else if (part.isMimeType("message/rfc822")) {
			attachflag = isContainAttach((Part) part.getContent());
		}
		return attachflag;
	}

	private static void saveAttachMent(Part part, String filePath) throws Exception {
		String fileName = "";
		if (part.isMimeType("multipart/*")) {
			Multipart mp = (Multipart) part.getContent();
			for (int i = 0; i < mp.getCount(); i++) {
				BodyPart mpart = mp.getBodyPart(i);
				String disposition = mpart.getDisposition();
				if ((disposition != null)
						&& ((disposition.equals(Part.ATTACHMENT)) || (disposition.equals(Part.INLINE)))) {
					fileName = mpart.getFileName();
					if (fileName != null) {
						fileName = MimeUtility.decodeText(fileName);
						saveFile(fileName, mpart.getInputStream(), filePath);
					}
				} else if (mpart.isMimeType("multipart/*")) {
					saveAttachMent(mpart, filePath);
				} else {
					fileName = mpart.getFileName();
					if (fileName != null) {
						fileName = MimeUtility.decodeText(fileName);
						saveFile(fileName, mpart.getInputStream(), filePath);
					}
				}
			}
		} else if (part.isMimeType("message/rfc822")) {
			saveAttachMent((Part) part.getContent(), filePath);
		}

	}

	private static void saveFile(String fileName, InputStream in, String filePath) throws Exception {
		File storefile = new File(filePath);
		if (!storefile.exists()) {
			storefile.mkdirs();
		}
		BufferedOutputStream bos = null;
		BufferedInputStream bis = null;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(filePath + "\\" + fileName));
			bis = new BufferedInputStream(in);
			int c;
			while ((c = bis.read()) != -1) {
				bos.write(c);
				bos.flush();
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (bos != null) {
				bos.close();
			}
			if (bis != null) {
				bis.close();
			}
		}
	}

	private static final int receiveEmailOverFrequencyTime = 15 * 60 * 1000;
	private static final int receiveEmailOverFrequencyCount = 96;
	private static Date receiveEmailLastOpreateTime = null;
	private static int receiveEmailLastOpreateCount = 0;

	public static List<EmailEntity> receiveMail(final ReceiveEmailInfo receiveEmailInfo) {
		List<EmailEntity> mailList = new ArrayList<EmailEntity>();

		if (overFrequencyProtect(receiveEmailOverFrequencyTime, receiveEmailLastOpreateTime,
				receiveEmailOverFrequencyCount, receiveEmailLastOpreateCount)) {
			return mailList;
		}

		URLName urln = null;
		Store receiveStore = null;
		Folder receiveFolder = null;
		try {
			urln = new URLName(Config.MAIL_TYPE, Config.MAIL_HOST, Config.MAIL_PORT, null,
					receiveEmailInfo.getUserName(), receiveEmailInfo.getPassWord());

			Properties properties = System.getProperties();
			properties.put("mail.smtp.host", Config.MAIL_HOST);
			properties.put("mail.smtp.auth", Config.MAIL_AUTH);
			Session sessionMail = Session.getDefaultInstance(properties, new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(receiveEmailInfo.getUserName(), receiveEmailInfo.getPassWord());
				}
			});

			receiveStore = sessionMail.getStore(urln);
			receiveStore.connect();

			receiveFolder = receiveStore.getFolder("INBOX");
			receiveFolder.open(Folder.READ_WRITE);

			Message[] messages = null;

			if (!StringUtil.isEmpty(receiveEmailInfo.getSenderFilter())) {
				SearchTerm st = new AndTerm(new FromStringTerm(receiveEmailInfo.getSenderFilter()),
						new SubjectTerm(StringUtil.isEmpty(receiveEmailInfo.getSubjectFilter()) ? ""
								: receiveEmailInfo.getSubjectFilter()));
				messages = receiveFolder.search(st);
			} else {
				int count = receiveFolder.getMessageCount();
				messages = receiveFolder.getMessages(count, count);
			}

			if (messages != null && messages.length > 0) {

				LOGGER.info("Email count：" + messages.length);

				for (int i = 0; i < messages.length; i++) {
					EmailEntity entity = new EmailEntity();

					StringBuffer bodytext = new StringBuffer();
					getMailContent((Part) messages[i], bodytext);
					if (isContainAttach((Part) messages[i])) {
						saveAttachMent((Part) messages[i], Config.MAIL_ATTACH_PATH);
					}

					StringBuffer messageLog = new StringBuffer();
					messageLog.append("-----------------Email content start-----------------")
							.append(Config.LOG_SPERATOR);
					String sender = getFrom(messages[i]);
					entity.setSender(sender);
					messageLog.append("sender:").append(sender).append(Config.LOG_SPERATOR);
					String subject = getSubject(messages[i]);
					entity.setSubject(subject);
					messageLog.append("subject:").append(subject).append(Config.LOG_SPERATOR);
					entity.setContent(bodytext.toString());
					// messageLog.append("content:").append(entity.getContent()).append(Config.LOG_SPERATOR);
					String sendTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
							.format(((MimeMessage) messages[i]).getSentDate());
					entity.setSendTime(sendTime);
					messageLog.append("send time:").append(sendTime).append(Config.LOG_SPERATOR);
					boolean hasAttachment = isContainAttach((Part) messages[i]) ? true : false;
					entity.setHasAttachment(hasAttachment);
					messageLog.append("hasAttachment:").append(hasAttachment).append(Config.LOG_SPERATOR);
					messageLog.append("-----------------Email content end-----------------");

					LOGGER.info(messageLog);

					if (receiveEmailInfo.isDelete()) {
						messages[i].setFlag(Flags.Flag.DELETED, true);
					}
					((POP3Message) messages[i]).invalidate(true);

					mailList.add(entity);
				}
			}

			receiveFolder.close(true);
		} catch (Exception e) {
			ExceptionUtil.propagate(LOGGER, e);
		} finally {
			if (receiveFolder != null && receiveFolder.isOpen()) {
				try {
					receiveFolder.close(true);
				} catch (MessagingException e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
			if (receiveStore.isConnected()) {
				try {
					receiveStore.close();
				} catch (MessagingException e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
		}

		return mailList;
	}

	public static class EmailEntity {
		private String sender;
		private String subject;
		private String content;
		private String sendTime;
		private boolean hasAttachment;

		public String getSender() {
			return sender;
		}

		public void setSender(String sender) {
			this.sender = sender;
		}

		public String getSubject() {
			return subject;
		}

		public void setSubject(String subject) {
			this.subject = subject;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

		public String getSendTime() {
			return sendTime;
		}

		public void setSendTime(String sendTime) {
			this.sendTime = sendTime;
		}

		public boolean isHasAttachment() {
			return hasAttachment;
		}

		public void setHasAttachment(boolean hasAttachment) {
			this.hasAttachment = hasAttachment;
		}
	}

	public static class ReceiveEmailInfo {
		private String userName;
		private String passWord;
		private String senderFilter;
		private String subjectFilter;
		private boolean isDelete;

		public String getUserName() {
			return userName == null ? defaultSendUser : userName;
		}

		public void setUserName(String userName) {
			this.userName = userName;
		}

		public String getPassWord() {
			return passWord == null ? defaultSendPwd : passWord;
		}

		public void setPassWord(String passWord) {
			this.passWord = passWord;
		}

		public String getSenderFilter() {
			return senderFilter == null ? defaultSyncSendUser : senderFilter;
		}

		public void setSenderFilter(String senderFilter) {
			this.senderFilter = senderFilter;
		}

		public String getSubjectFilter() {
			return subjectFilter;
		}

		public void setSubjectFilter(String subjectFilter) {
			this.subjectFilter = subjectFilter;
		}

		public boolean isDelete() {
			return isDelete;
		}

		public void setDelete(boolean isDelete) {
			this.isDelete = isDelete;
		}
	}

	public static class Config {
		public static String MAIL_HOST = "pop3.163.com";
		public static int MAIL_PORT = 110;
		public static String MAIL_TYPE = "pop3";
		public static String MAIL_AUTH = "true";
		public static String MAIL_ATTACH_PATH = "upload/recMail/";
		public static String LOG_SPERATOR = "\n";
	}

	public static void main(String[] args) {

		/*
		 * SendEmailInfo info = new SendEmailInfo();
		 * info.setSendUser("erwerw@163.com"); info.setSendUname("erwerw");
		 * info.setSendNickName("数据同步助手"); info.setSendPwd("erwerw");
		 * info.setReceiveUser("erwerw@163.com");
		 * info.setHeadName("family_assistant_data_sync_" +
		 * DateUtil.getDateFormat(new Date()));
		 * info.setSendHtml(GsonUtil.toJson(info));
		 * 
		 * EmailUtil.sendEmail(info);
		 */

		/*
		 * ReceiveEmailInfo rinfo = new ReceiveEmailInfo();
		 * rinfo.setUserName("12312@163.com"); rinfo.setPassWord("123123");
		 * rinfo.setSenderFilter("123123@163.com"); rinfo.setDelete(false);
		 * 
		 * EmailUtil.receiveMail(rinfo);
		 */

		initDefaultEmailAccountInfo("sync_18301166408@163.com", "chengwei123", "sync_18301166408@163.com", null);
		SendEmailInfo info = new SendEmailInfo();
		info.setReceiveUser("295999757@qq.com;18301166408@163.com");
		info.setHeadName("family_assistant_data_sync_" + DateUtil.getDateFormat(new Date()));
		info.setSendHtml(GsonUtil.toJson(info));
		sendEmail(info);
	}
}
