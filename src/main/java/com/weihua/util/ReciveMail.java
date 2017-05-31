package com.weihua.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
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

public class ReciveMail {

	private static Logger LOGGER = Logger.getLogger(ReciveMail.class);

	private static Session getSessionMail() throws Exception {
		Properties properties = System.getProperties();
		properties.put("mail.smtp.host", Config.MAIL_HOST);
		properties.put("mail.smtp.auth", Config.MAIL_AUTH);
		Session sessionMail = Session.getDefaultInstance(properties, null);
		return sessionMail;
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

	public static void receiveMail(String userName, String passWord, String senderFilter, String subjectFilter,
			boolean isDelete) {
		Store store = null;
		Folder folder = null;
		URLName urln = null;
		try {
			urln = new URLName(Config.MAIL_TYPE, Config.MAIL_HOST, Config.MAIL_PORT, null, userName, passWord);
			store = getSessionMail().getStore(urln);
			store.connect();
			folder = store.getFolder("INBOX");
			folder.open(Folder.READ_WRITE);

			Message[] messages = null;

			if (!StringUtil.isEmpty(senderFilter)) {
				SearchTerm st = new AndTerm(new FromStringTerm(senderFilter),
						new SubjectTerm(StringUtil.isEmpty(subjectFilter) ? "" : subjectFilter));
				messages = folder.search(st);
			} else {
				int count = folder.getMessageCount();
				messages = folder.getMessages(count, count);
			}

			LOGGER.info("Email count：" + messages.length);

			for (int i = 0; i < messages.length; i++) {
				StringBuffer bodytext = new StringBuffer();
				getMailContent((Part) messages[i], bodytext);
				if (isContainAttach((Part) messages[i])) {
					saveAttachMent((Part) messages[i], Config.MAIL_ATTACH_PATH);
				}

				StringBuffer messageLog = new StringBuffer();
				messageLog.append("-----------------Email content start.-----------------").append(Config.LOG_SPERATOR);
				messageLog.append("sender:").append(getFrom(messages[i])).append(Config.LOG_SPERATOR);
				messageLog.append("subject:").append(getSubject(messages[i])).append(Config.LOG_SPERATOR);
				messageLog.append("content:").append(bodytext).append(Config.LOG_SPERATOR);
				messageLog.append("send time:").append(
						new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(((MimeMessage) messages[i]).getSentDate()))
						.append(Config.LOG_SPERATOR);
				messageLog.append("hasAttachment:").append((isContainAttach((Part) messages[i]) ? true : false))
						.append(Config.LOG_SPERATOR);
				messageLog.append("-----------------Email content end.-----------------");

				LOGGER.info(messageLog);

				if (isDelete) {
					messages[i].setFlag(Flags.Flag.DELETED, true);
				}
				((POP3Message) messages[i]).invalidate(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (folder != null && folder.isOpen()) {
				try {
					folder.close(true);
				} catch (MessagingException e) {
					e.printStackTrace();
				}
			}
			if (store.isConnected()) {
				try {
					store.close();
				} catch (MessagingException e) {
					e.printStackTrace();
				}
			}
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
		try {
			ReciveMail.receiveMail("dfd", "dfdf", "ztokefu@zto.cn", null, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}