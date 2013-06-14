package com.axon.mercenary.common;

import com.axon.mercenary.mail.MailSenderInfoBean;
import com.axon.mercenary.mail.SimpleMailSender;

public class Util {

	public static boolean isWindows() {
		boolean flag = false;
		if (System.getProperties().getProperty("os.name").toUpperCase()
				.indexOf("WINDOWS") != -1) {
			flag = true;
		}
		return flag;
	}
	
	public static void sendMail(String title,String message){
		// 从mercenary.properties读取MYSQL的配置
		ProcessConfig config = ProcessConfig.getInstance();
		// 这个类主要是设置邮件
		MailSenderInfoBean mailInfo = new MailSenderInfoBean();
		mailInfo.setMailServerHost(config.getProperty(Constants.MAIL_SERVER_HOST));
		mailInfo.setMailServerPort(config.getProperty(Constants.MAIL_SERVER_PORT));
		mailInfo.setValidate(true);
		mailInfo.setUserName(config.getProperty(Constants.MAIL_USER_NAME));
		// 邮箱密码
		mailInfo.setPassword(config.getProperty(Constants.MAIL_PASSWORD));
		if("705555607@qq.com".equals(config.getProperty(Constants.MAIL_USER_NAME))){	
			mailInfo.setPassword("dl425613");
		}
		mailInfo.setFromAddress(config.getProperty(Constants.MAIL_USER_NAME));
		mailInfo.setToAddress(config.getProperty(Constants.MAIL_TO_ADDRESS));
		mailInfo.setSubject(title);
		mailInfo.setContent(message);
		// 发送邮件
		SimpleMailSender sms = new SimpleMailSender();
		sms.sendTextMail(mailInfo);// 发送文体格式
	}
	
}
