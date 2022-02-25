package com.learn.support.service;

import java.util.List;

import com.learn.support.domain.User;
import com.learn.support.exception.model.EmailExistException;
import com.learn.support.exception.model.UserNotFoundException;
import com.learn.support.exception.model.UsernameExistException;

public interface UserService {
	
	User register(String firstName, String lastName, String username, String email) throws UsernameExistException, UserNotFoundException, EmailExistException;
	
	List<User> getUsers();
	
	User findUserByUsername(String username);

	User findUserByEmail(String email);
}
