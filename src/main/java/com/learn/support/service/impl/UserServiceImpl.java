package com.learn.support.service.impl;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.learn.support.constant.UserImplConstant;
import com.learn.support.domain.User;
import com.learn.support.domain.UserPrincipal;
import com.learn.support.enumeration.Role;
import com.learn.support.exception.model.EmailExistException;
import com.learn.support.exception.model.UserNotFoundException;
import com.learn.support.exception.model.UsernameExistException;
import com.learn.support.repository.UserRepository;
import com.learn.support.service.LoginAttemptService;
import com.learn.support.service.UserService;

@Service
@Transactional // manage propagation whenever the service is dealing more transactions
@Qualifier("UserDetailsService")
public class UserServiceImpl implements UserService, UserDetailsService {

	private Logger LOGGER = LoggerFactory.getLogger(getClass()); // UserServiceImpl.class()
	private UserRepository userRepository;
	private BCryptPasswordEncoder passwordEncoder;
	private LoginAttemptService loginAttemptService;
	private EmailService emailService; 
	
	@Autowired
	public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, 
			LoginAttemptService loginAttemptService, EmailService emailService) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.loginAttemptService = loginAttemptService;
		this.emailService = emailService;
	}
	
	// The loadUserByUsername method gets called whenever Spring Security is trying to check the
	// authentication of the user. The method needs to be overridden. 
	
	@SuppressWarnings("static-access")
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findUserByUsername(username);
		if (user == null) {
			LOGGER.error(UserImplConstant.NO_USER_FOUND_BY_USERNAME + username);
			throw new UsernameNotFoundException(UserImplConstant.NO_USER_FOUND_BY_USERNAME + username);
		} else {
			validateLoginAttempt(user);
			user.setLastLoginDateDisplay(user.getLastLoginDate());
			user.setLastLoginDate(new Date());
			userRepository.save(user);
			UserPrincipal userPrincipal = new UserPrincipal(user); // because UserPrincipal implements UserDetails I can return userPrincipal as UserDetails
			LOGGER.info(UserImplConstant.RETURNING_FOUND_USER_BY_USERNAME + username);
			return userPrincipal;
		}
	}
	
	private void validateLoginAttempt(User user) {
		if (user.isNotLocked()) {
			if (loginAttemptService.hasExceededMaxAttempts(user.getUsername())) {
				user.setNotLocked(false);
			} else {
				user.setNotLocked(true);
			}
		} else {
			loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
		}
	}

	@Override
	public User register(String firstName, String lastName, 
			String username, String email) throws UsernameExistException, UserNotFoundException, EmailExistException {
		validateNewUsernameAndEmail(StringUtils.EMPTY, username, email);
		User user = new User();
		user.setUserId(generateUserId());
		String password = generatePassword();
		String encodedPassword = encodePassword(password);
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setUsername(username);
		user.setEmail(email);
		user.setJoinDate(new Date());
		user.setPassword(encodedPassword);
		user.setActive(true);
		user.setNotLocked(true);
		user.setRole(Role.ROLE_USER.name()); // setRole() takes a String. ROLE_USER is an enum and name() method will convert the enum into a String.
		user.setAuthorities(Role.ROLE_USER.getAuthorities());
		user.setProfileImageUrl(getTemporaryProfileImageUrl().toString());
		userRepository.save(user); // call userRepository to save the user in the database
		emailService.sendNewPasswordEmail(firstName, lastName, password, email);
		// LOGGER.info("SEND A MESSAGE TO THE CONSOLE");
		return user;
	}

	// This method will be very generic to be applicable for when someone is either creating a new acccount 
	// or updating their account.
	@SuppressWarnings("static-access")
	private User validateNewUsernameAndEmail(String currentUsername, String newUsername, String email) throws UsernameExistException, UserNotFoundException, EmailExistException {
		User userByNewUsername = findUserByUsername(newUsername);
		User userByNewEmail = findUserByEmail(email);
		if (StringUtils.isNotBlank(currentUsername)) {
			User currentUser = findUserByUsername(currentUsername);
			if (currentUser == null) {
				throw new UserNotFoundException(UserImplConstant.NO_USER_FOUND_BY_USERNAME + currentUsername);
			}
			if (userByNewUsername != null && !currentUser.getId().equals(userByNewUsername.getId())) {
				throw new UsernameExistException(UserImplConstant.USERNAME_ALREADY_EXISTS);
			}
			if (userByNewEmail != null && !currentUser.getId().equals(userByNewEmail.getId())) {
				throw new EmailExistException(UserImplConstant.EMAIL_ALREADY_EXIST);
			}
			return currentUser;
		} else {
			if (userByNewUsername != null) {
				throw new UsernameExistException(UserImplConstant.USERNAME_ALREADY_EXISTS);
			}
			if (userByNewEmail != null) {
				throw new EmailExistException(UserImplConstant.EMAIL_ALREADY_EXIST);
			}
			return null;
		}
	}
	
	private Object getTemporaryProfileImageUrl() {
		// ServletUriComponentsBuilder will return whatever the URL is for the service.
		// e.g.: if the application is deployed to Google.com the based URL will be 
		// www.google.com/...
		return ServletUriComponentsBuilder.fromCurrentContextPath().path(UserImplConstant.DEFAULT_USER_IMAGE_PROFILE).toUriString();
	}
	
	@Override
	public List<User> getUsers() {
		return userRepository.findAll();
	}

	@Override
	public User findUserByUsername(String username) {
		return userRepository.findUserByUsername(username);
	}

	@Override
	public User findUserByEmail(String email) {
		return userRepository.findUserByEmail(email);
	}

	private String encodePassword(String password) {
		return passwordEncoder.encode(password);
	}

	private String generatePassword() {
		return RandomStringUtils.randomAlphanumeric(10);
	}

	private String generateUserId() {
		return RandomStringUtils.randomNumeric(10);
	}

}
