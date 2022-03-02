package com.learn.support.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.learn.support.domain.UserPrincipal;
import com.learn.support.service.LoginAttemptService;

@Component
public class AuthenticationSuccessListener {

	private LoginAttemptService loginAttemptService;
	
	@Autowired
	public AuthenticationSuccessListener(LoginAttemptService loginAttemptService) {
		this.loginAttemptService = loginAttemptService;
	}
	
	// When the user successfully logs in, AuthenticationSuccessEvent will 
	// get back the whole user.
	// When trying to get the authentication and principal, the app will return
	// the user back.
	@EventListener
	public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
		// Object principal = event.getAuthentication().getPrincipal();
		Object principal = event.getAuthentication();
		// ---------------------------------------------------------------------------
		// System.out.println("Returns a String representation of this EventObject.");
		// System.out.println(principal.toString());
		// ---------------------------------------------------------------------------
		principal = ((Authentication) principal).getPrincipal();
		 
		if (principal instanceof UserPrincipal) {
			UserPrincipal userPrincipal = (UserPrincipal) event.getAuthentication().getPrincipal();
			loginAttemptService.evictUserFromLoginAttemptCache(userPrincipal.getUsername());
		}
	}
	
}
