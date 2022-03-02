package com.learn.support.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import com.learn.support.domain.User;
import com.learn.support.service.LoginAttemptService;

@Component
public class AuthenticationSuccessListener {

	private LoginAttemptService loginAttemptService;
	
	@Autowired
	public AuthenticationSuccessListener(LoginAttemptService loginAttemptService) {
		this.loginAttemptService = loginAttemptService;
	}
	
	//	When the user successfully logs in event will get back the whole user.
	//	When trying to get the authentication and principal, the app will return
	//	the user back.
	@EventListener
	public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
		Object principal = event.getAuthentication().getPrincipal();
		if (principal instanceof User) {
			User user = (User) event.getAuthentication().getPrincipal();
			loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
		}
	}
	
}
