package com.learn.support.enumeration;

import com.learn.support.constant.Authority;

//========================================> Role <========================================
// This enum will be available to the rest of the application. When using one of the enums, 
// getAuthorities() is called, it will return the authorities associated to that role.
// This way if the a role is passed to a user, I can get the authorities for that user.
//========================================================================================

public enum Role {
	
	ROLE_USER(Authority.USER_AUTHORITIES),
	ROLE_HR(Authority.HR_AUTHORITIES),
	ROLE_MANAGER(Authority.MANAGER_AUTHORITIES),
	ROLE_ADMIN(Authority.ADMIN_AUTHORITIES),
	ROLE_SUPER_ADMIN(Authority.SUPER_ADMIN_AUTHORITIES);

	private String[] authorities;

	Role(String... authorities) {
		this.authorities = authorities;
	}
	
	public String[] getAuthorities() {
		return authorities;
	}
}
