package com.learn.support.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.learn.support.service.LoginAttemptService;

@Component
public class AuthenticationFailureListener {

	private LoginAttemptService loginAttemptService;
	
	@Autowired
	public AuthenticationFailureListener(LoginAttemptService loginAttemptService) {
		this.loginAttemptService = loginAttemptService;
	}
	// =====> .getAuthentication()
	// Getters for the Authentication request that caused the event. Returns
	// the authentication request.
	
	// =====> .getPrincipal()
	// The identity of the principal being authenticated. In the case of an 
	// authentication request with username and password, this would be the 
	// username. Callers are expected to populate the principal for an 
	// authentication request. Return the Principal being authenticated 
	// or the authenticated principal after authentication.
	
	@EventListener
	public void onAuthenticationFailure(AuthenticationFailureBadCredentialsEvent event) {
		Object principal = event.getAuthentication();
		System.out.println("Returns a String representation of this EventObject.");
		System.out.println(principal.toString());
		principal = ((Authentication) principal).getPrincipal();
		if (principal instanceof String) {
			String username = (String) event.getAuthentication().getPrincipal();
			loginAttemptService.addUserToLoginAttemptCache(username); // if user fails to login it will be added to the cache
		}
	}
}
