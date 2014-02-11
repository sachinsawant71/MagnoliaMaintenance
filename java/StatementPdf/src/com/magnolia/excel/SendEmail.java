package com.magnolia.excel;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendEmail {
	   
		public static void main(String[] args) {
			 
			final String username = "magnolia.pashan@gmail.com";
			final String password = "xxxxx";
	 
			Properties props = new Properties();
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.host", "smtp.gmail.com");
			props.put("mail.smtp.port", "587");
	 
			Session session = Session.getInstance(props,
			  new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
			  });
	 
			try {
	 
				Message message = new MimeMessage(session);
				message.setFrom(new InternetAddress("magnolia.pashan@gmail.com"));
				message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse("sachin_sawant71@yahoo.com"));
				message.setSubject("Magnolia Maintenance Payment Summary for " );
				message.setText("Dear Mail Crawler,"
					+ "\n\n No spam to my email, please!");	 
				message.setContent(message, "text/html; charset=utf-8");
				
				Transport.send(message);
	 
				System.out.println("Done");
	 
			} catch (MessagingException e) {
				throw new RuntimeException(e);
			}
		}
      

}