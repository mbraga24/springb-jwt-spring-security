package com.learn.support.service.impl;

import java.util.Date;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.learn.support.domain.User;
import com.learn.support.domain.UserPrincipal;
import com.learn.support.repository.UserRepository;
import com.learn.support.service.UserService;


@Service
@Transactional // manage propagation whenever the service is dealing more transactions
public class UserServiceImpl implements UserService, UserDetailsService {

	private Logger LOGGER = LoggerFactory.getLogger(getClass()); // UserServiceImpl.class()
	private UserRepository userRepository;
	
	@Autowired
	public UserServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	// The loadUserByUsername method gets called whenever Spring Security is trying to check the
	// authentication of the user. The method needs to be overridden. 
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findUserByUsername(username);
		if (user == null) {
			LOGGER.error("USER NOT FOUND BY USERNAME: " + username);
			throw new UsernameNotFoundException("User not found by username: " + username);
		} else {
			user.setLastLoginDateDisplay(user.getLastLoginDate());
			user.setLastLoginDate(new Date());
			UserPrincipal userPrincipal = new UserPrincipal(user); // because UserPrincipal extends UserDetails I can return userPrincipal as UserDetails
			return userPrincipal;
		}
		
	}

}
