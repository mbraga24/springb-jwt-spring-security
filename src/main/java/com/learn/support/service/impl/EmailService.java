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
//
// ** Extension: javax mail **
//==========================================================================
@Service
public class EmailService {
	
	// Creating an instance (or a bean) of EmailService and calling sendNewPasswordEmail with
	// the proper arguments it will send an email for the new user.
	public void sendNewPasswordEmail(String firstName, String lastName, String password, String email) {
		
		// -> getTransport(): Get a Transport object that implements this user's desired Transport protcol. 
		// The mail.transport.protocol property specifies the desired protocol
		
		try {
			Message message = createEmail(firstName, lastName, password, email);
			SMTPTransport smtpTransport = (SMTPTransport) getEmailSession().getTransport(EmailConstant.SIMPLE_MAIL_TRANSFER_PROTOCOL_SECURE);
			smtpTransport.connect(EmailConstant.GMAIL_SMTP_SERVER, EmailConstant.USERNAME, EmailConstant.PASSWORD); // Connect to the specified address. This method provides a simple authentication scheme that requires a username and password.
			smtpTransport.sendMessage(message, message.getAllRecipients()); // Send the Message to the specified list of addresses. TransportEvent is thrown under a few circumstances.
			smtpTransport.close(); // Close the Transport and terminate the connection to the server
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
	
	private Message createEmail(String firstName, String lastName, String password, String email) throws MessagingException {
		
		// -> new MimeMessage(): Default constructor. An empty message object is created.
		
		Message message = new MimeMessage(getEmailSession());
		message.setFrom(new InternetAddress(EmailConstant.FROM_EMAIL));
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email, false));
		message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(EmailConstant.CC_EMAIL, false));
		message.setSubject(EmailConstant.EMAIL_SUBJECT);
		message.setText("Hello " + firstName + " " + lastName + ", \n \nWelcome to the team! \n \n"
				+ "We will give you a temporary password for now. But relax, you can change it later "
				+ "in your profile settings. \n \nYour password is: " + password + " \n \nWe're "
						+ "looking forward to working with you! \n \n Your Support Team.");
		message.setSentDate(new Date());
		message.saveChanges();
		return message;
	}
	
	private Session getEmailSession() {
		
		// -> getProperties(): The java.lang.System.getProperties() method determines the current 
		// system properties.
		
		// -> Properties: The Properties class represents a persistent set of properties. The Properties can 
		// be saved to a stream or loaded from a stream. In the example below I want to assign a Properties
		// object to the properties variable.
		
		Properties properties = System.getProperties();
		properties.put(EmailConstant.SMTP_HOST, EmailConstant.GMAIL_SMTP_SERVER);
		properties.put(EmailConstant.SMTP_AUTH, true);
		properties.put(EmailConstant.SMTP_PORT, EmailConstant.DEFAULT_PORT);
		properties.put(EmailConstant.SMTP_STARTTLS_ENABLE, true);
		properties.put(EmailConstant.SMTP_STARTTLS_REQUIRED, true);
		
		// -> Session.getInstance(): Get a new Session object.
		
		return Session.getInstance(properties, null);
	}

}
