package com.learn.support.service;

import com.learn.support.domain.User;

public interface UserService {
	
	User register(String firstName, String lastName, String username, String email);
	
	List<User> getUsers();
	
	User findUserByUsername(String username);

	User findUserByEmail(String email);
}
