package com.learn.support.domain;

import java.io.Serializable;
import java.sql.Date;

public class User implements Serializable {
	
	private Long id;
	private String userId;
	private String firstName;
	private String lastName;
	private String username;
	private String password;
	private String email;
	private String imageUrl;
	private Date lastLoginDate;
	private Date lastLoginDateDisplay;
	private Date joinDate;
	private String[] roles; // will hold the role of users - ROLE_ADMIN{ read, edit, delete, update, create }
	private String[] authorities; // { read, edit, delete, update, create }
	private boolean isActive;
	private boolean isNotLocked;	
	
}
