package com.learn.support.constant;

public class SecurityConstant {
	
	public static final long EXPIRATION_TIME = 432_000_000; // 5 days in milliseconds
	
	//	TOKEN_PREFIX - Bearer:
	//	Bearer in front of the token means that whoever gives the application
	//	this token, the application doesn't have to do any further verification. The app can take
	//	the token and work with it. 
	public static final String TOKEN_PREFIX = "Bearer ";
	
	//	JWT_TOKEN_HEADER = Jwt-Token:
	//	When the request comes in, the user logs in with the credentials, the app checks their 
	//	credentials, they're logged in successfully and the response sent back to the user 
	//	will be the actual token, but the the token is gonna be on the header, a Jwt token served
	//	in the header.
	public static final String JWT_TOKEN_HEADER = "Jwt-Token";
	
	// TOKEN_CANNOT_BE_VERIFIED
	//	Whenever the app is trying to decide for the token and it can't, that means the token has been
	//	tempered with and this message will be sent.
	public static final String TOKEN_CANNOT_BE_VERIFIED = "Token cannot be verified";

	//	YOUR_SUPPORT_COMPANY
	//	Issuer of the token
	public static final String YOUR_SUPPORT_CO = "Your Support Co.";
	
	//	YOUR_SUPPORT_CO_ADMINISTRATION
	//	This token is issued for wide audience.
	public static final String YOUR_SUPPORT_CO_ADMINISTRATION = "User Management Support";
	
	public static final String AUTHORITIES = "authorities";
	
	//	FORBIDDEN_MSG
	//	When token is provided but they're forbidden access to the resource.
	public static final String FORBIDDEN_MESSAGE = "Please log in to access this page. Thank you.";
	
	//	ACCESS_DENIED_MSG
	//	When user doesn't have access to the resource
	public static final String ACCESS_DENIED_MESSAGE = "You do not have permission to access this page.";
	
	//	OPTIONS_HTTP_METHOD
	//	If the request method is optional, e.g: not GET or POST, the application will not take any
	//	action. 
	public static final String OPTIONS_HTTP_METHOD = "OPTIONS";
	
	//	PUBLIC_URLS 
	public static final String[] PUBLIC_URLS = {
				"/user/login", 
				"/user/register", 
				"/user/resetpassword/**", 
				"/user/image/**"};
	
	//	!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	//			ADD DEPENDENCY TO THE POM FILE THAT WILL
	//			USE A LIBRATY TO GENERATE A TOKEN
	//			LINK: https://github.com/auth0/java-jwt
	//	!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	
	
	
}

