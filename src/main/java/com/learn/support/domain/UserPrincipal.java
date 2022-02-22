package com.learn.support.domain;

import static java.util.Arrays.stream;
import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

//================================> UserPrincipal <================================
// This class is where I can map isActive, authorities and isNotLocked from 
// the User class to the Spring user in the security context.

// UserDetails comes from Spring Security - it provides core user information
// from a security perspective. The UserPrincipal is the user that Spring Security
// is going to need to do all the work. Because UserDetails is an interface it 
// already has boilerplate methods that work with Spring Security. 
// Some I will need to pass the actual the value from the User class so Spring
// Security can map those values and determine if they're correct or not. Other 
// methods I might need to set a generic value to guarantee the application will 
// run.
//==================================================================================

@SuppressWarnings("serial")
public class UserPrincipal implements UserDetails {
	
	private User user;
	
	public UserPrincipal(User user) {
		super();
		this.user = user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// Take all the getAuthorities, which is a String array, and map through 
		// each one of them and create a new object of SimpleGrantedAuthority and
		// collect them to a list of string.
		return stream(this.user.getAuthorities()).map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList());
	}

	@Override
	public String getPassword() {
		return this.user.getPassword();
	}

	@Override
	public String getUsername() {
		return this.user.getUsername();
	}

	// I didn't define this field so I don't have this field in 
	// the user class. When it's set to "true", the app won't fail 
	// when tries to log in
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return this.user.isNotLocked();
	}

	// I didn't define this field so I don't have this field in 
	// the user class. When it's set to "true", the app won't fail 
	// when tries to log in
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return this.user.isActive();
	}	
}
