package com.learn.support.resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.learn.support.exception.model.ExceptionHandling;
import com.learn.support.exception.model.UserNotFoundException;

@RestController
@RequestMapping(value = "/user")
public class UserResource extends ExceptionHandling {

	@GetMapping("/home")
	public String showUser() throws UserNotFoundException {
		
		throw new UserNotFoundException("User not found.");
//		return "Application Works";
	}
	
}
