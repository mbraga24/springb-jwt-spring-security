package com.learn.support.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.learn.support.domain.User;
import com.learn.support.exception.model.EmailExistException;
import com.learn.support.exception.model.ExceptionHandling;
import com.learn.support.exception.model.UserNotFoundException;
import com.learn.support.exception.model.UsernameExistException;
import com.learn.support.service.UserService;

@RestController
@RequestMapping(path = { "/", "/user" })
public class UserResource extends ExceptionHandling {

	private UserService userService;
	
	@Autowired
	public UserResource(UserService userService) {
		super();
		this.userService = userService;
	}

	@PostMapping("/register")	
	public ResponseEntity<User> register(@RequestBody User user) throws UserNotFoundException, UsernameExistException, EmailExistException {
		User newUser = userService.register(user.getFirstName(), user.getLastName(), user.getUsername(), user.getEmail());
		return new ResponseEntity<>(newUser, HttpStatus.OK);
	}
	
}
