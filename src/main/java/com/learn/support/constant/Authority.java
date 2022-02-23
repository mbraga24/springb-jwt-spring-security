package com.learn.support.constant;

//======================================> Authority <====================================
// Will define arrays that will hold user authorities. These authorities will be passed to
// certain roles and assign these roles to users.
//=======================================================================================

public class Authority {
	public static final String[] USER_AUTHORITIES = { "user:read" };
	public static final String[] HR_AUTHORITIES = { "user:read", "user:update" };
	public static final String[] MANAGER_AUTHORITIES = { "user:read", "user:update" };
	public static final String[] ADMIN_AUTHORITIES = { "user:read", "user:create", "user:update" };
	public static final String[] SUPER_ADMIN_AUTHORITIES = { "user:read", "user:create", "user:update", "user:delete" };
	
	
}
