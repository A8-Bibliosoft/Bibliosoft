package com.lib.bibliosoft.utils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class SendEmail {
	 public static boolean sendMessage(String message, String toEmail) throws MessagingException {
		 try {
            Properties props = new Properties();
            // 开启debug调试
            props.setProperty("mail.debug", "true");
            // 发送服务器需要身份验证
            props.setProperty("mail.smtp.auth", "true");
            // 设置邮件服务器主机名
            props.setProperty("mail.host", "smtp.qq.com");
            // 发送邮件协议名称
            props.setProperty("mail.transport.protocol", "smtp");
            // 设置环境信息
            Session session = Session.getInstance(props);
            session.setDebug(true);

            // 创建邮件对象
            Message msg = new MimeMessage(session);

            msg.setSubject("bibliosoft");
            // 设置邮件内容
            msg.setText(message);
            // 设置发件人
            msg.setFrom(new InternetAddress("1448318125@qq.com"));

            Transport transport = session.getTransport();
            // 连接邮件服务器
            transport.connect("1448318125@qq.com", "ajhyxrxdkyipjjcg");  //******代表我的邮箱授权码，需要自己再邮箱内开启SMTP协议
            // 发送邮件
            transport.sendMessage(msg, new Address[] {new InternetAddress(toEmail)});
            // 关闭连接
            transport.close();
            return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	 }  
}