package com.learn.support.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.learn.support.domain.User;
import com.learn.support.exception.model.EmailExistException;
import com.learn.support.exception.model.EmailNotFoundException;
import com.learn.support.exception.model.UserNotFoundException;
import com.learn.support.exception.model.UsernameExistException;

public interface UserService {
	
	User register(String firstName, String lastName, String username, String email) throws UsernameExistException, UserNotFoundException, EmailExistException;
	
	List<User> getUsers();
	
	User findUserByUsername(String username);

	User findUserByEmail(String email);
	
	User addNewUser(String firstName, String lastName, String username, String email, String role, boolean isNotLocked, boolean isActive) throws UsernameExistException, UserNotFoundException, EmailExistException;

	User updateUser(String currentUsername, String firstName, String lastName, String username, String email, String role, boolean isNotLocked, boolean isActive) throws UsernameExistException, UserNotFoundException, EmailExistException;
	
	void deleteUser(long id);
	
	void resetPassword(String email) throws EmailNotFoundException;
	
	User updateProfileImage(String username, MultipartFile profileImage) throws UsernameExistException, UserNotFoundException, EmailExistException, IOException;
}
