package com.learn.support.constant;

public class EmailConstant {
	
	// method for securing the SMTP using transport layer security
	public static final String SIMPLE_MAIL_TRANSFER_PROTOCOL_SECURE = "smtps"; 
	
	public static final String USERNAME = "houseofelinc@gmail.com";
	public static final String PASSWORD = "dummyemail2@22";
	public static final String FROM_EMAIL = "houseofelinc@gmail.com";
	public static final String CC_EMAIL = "";
	public static final String EMAIL_SUBJECT = "Your Support Co.";
	
	// Gmail SMTP server address
	public static final String GMAIL_SMTP_SERVER = "smtp.gmail.com";
	
	public static final String SMTP_HOST = "mail.smtp.host"; 
	public static final String SMTP_AUTH = "mail.smtp.auth";
	public static final String SMTP_PORT = "mail.smtp.port";
	public static final int DEFAULT_PORT = 465; // Gmail SMTP port (SSL): 465
	
	// enables the use of the STARTTLS command (if supported by the server) to switch 
	// the connection to a TLS-protected connection before issuing any login commands
	public static final String SMTP_STARTTLS_ENABLE = "mail.smtp.starttls.enable"; 
	
	// It often requires email clients to use StartTLS to send mail. Other ports used 
	// to send encrypted mail are 25, 465, and 2525. Since port 25 was designed for mail transfer, not submission, your ISP may block email sent through this port
	public static final String SMTP_STARTTLS_REQUIRED = "mail.smtp.starttls.required"; 
}
