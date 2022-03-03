package com.learn.support.service.impl;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import com.sun.mail.smtp.SMTPTransport;

import org.springframework.stereotype.Service;

import com.learn.support.constant.EmailConstant;

//=============================> EmailService <=============================
// The Email Session uses all EmailConstants properties to set up the system 
// and create the session.
//==========================================================================
@Service
public class EmailService {
	
	// Creating an instance (or a bean) of EmailService and calling sendNewPasswordEmail with
	// the proper arguments it will send an email for the new user.
	public void sendNewPasswordEmail(String firstName, String lastName, String password, String email) {
		try {
			Message message = createEmail(firstName, lastName, password, email);
			SMTPTransport smtpTransport = (SMTPTransport) getEmailSession().getTransport(EmailConstant.SIMPLE_MAIL_TRANSFER_PROTOCOL_SECURE);
			smtpTransport.connect(EmailConstant.GMAIL_SMTP_SERVER, EmailConstant.USERNAME, EmailConstant.PASSWORD);
			smtpTransport.sendMessage(message, message.getAllRecipients());
			smtpTransport.close();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
	
	private Message createEmail(String firstName, String lastName, String password, String email) throws MessagingException {
		Message message = new MimeMessage(getEmailSession());
		message.setFrom(new InternetAddress(EmailConstant.FROM_EMAIL));
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email, false));
		message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(EmailConstant.CC_EMAIL, false));
		message.setSubject(EmailConstant.EMAIL_SUBJECT);
		message.setText("Hello " + firstName + " " + lastName + ", \n \nWelcome to the team! \n \n"
				+ "We will give you a temporary password for now. But relax, you can change it later "
				+ "in your profile settings. \n \n Your password is: " + password + " \n \nWe're "
						+ "looking forward to working with you! \n \n Your Support Team.");
		message.setSentDate(new Date());
		message.saveChanges();
		return message;
	}
	
	private Session getEmailSession() {
		Properties properties = System.getProperties();
		properties.put(EmailConstant.SMTP_HOST, EmailConstant.GMAIL_SMTP_SERVER);
		properties.put(EmailConstant.SMTP_AUTH, true);
		properties.put(EmailConstant.SMTP_PORT, EmailConstant.DEFAULT_PORT);
		properties.put(EmailConstant.SMTP_STARTTLS_ENABLE, true);
		properties.put(EmailConstant.SMTP_STARTTLS_REQUIRED, true);
		return Session.getInstance(properties, null);
	}

}
