package com.learn.support.resource;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.learn.support.constant.SecurityConstant;
import com.learn.support.domain.HttpResponse;
import com.learn.support.domain.User;
import com.learn.support.domain.UserPrincipal;
import com.learn.support.exception.model.EmailExistException;
import com.learn.support.exception.model.EmailNotFoundException;
import com.learn.support.exception.model.ExceptionHandling;
import com.learn.support.exception.model.UserNotFoundException;
import com.learn.support.exception.model.UsernameExistException;
import com.learn.support.service.UserService;
import com.learn.support.utility.JWTTokenProvider;

@RestController
@RequestMapping(path = { "/", "/user" })
public class UserResource extends ExceptionHandling {

	private static final String RESET_CONFIRMED_EMAIL_SENT = "You're all set! Your password reset is confirmed. An email with your new password was sent to: ";
	private UserService userService;
	private AuthenticationManager authenticationManager;
	private JWTTokenProvider jwtTokenProvider;
	private SecurityConstant securityConstant;
	
	@Autowired
	public UserResource(UserService userService, AuthenticationManager authenticationManager,
			JWTTokenProvider jwtTokenProvider) {
		super();
		this.userService = userService;
		this.authenticationManager = authenticationManager;
		this.jwtTokenProvider = jwtTokenProvider;
	}

	@PostMapping("/login")
	public ResponseEntity<User> login(@RequestBody User user) {
		authenticateUser(user.getUsername(), user.getPassword());
		User loginUser = userService.findUserByUsername(user.getUsername());
		UserPrincipal userPrincipal = new UserPrincipal(loginUser);
		HttpHeaders jwtHeader = getJwtHeader(userPrincipal);
		return new ResponseEntity<>(loginUser, jwtHeader, HttpStatus.OK);
	}

	@PostMapping("/register")	
	public ResponseEntity<User> register(@RequestBody User user) throws UserNotFoundException, UsernameExistException, EmailExistException {
		User newUser = userService.register(user.getFirstName(), user.getLastName(), user.getUsername(), user.getEmail());
		return new ResponseEntity<>(newUser, HttpStatus.OK);
	}
	
	@PostMapping("/add")
	public ResponseEntity<User> addNewUser(@RequestParam("firstName") String firstName,
			                               @RequestParam("lastName") String lastName,
			                               @RequestParam("email") String email,
			                               @RequestParam("username") String username,
			                               @RequestParam("role") String role,
	                                       @RequestParam("isNotLocked") String isNotLocked,
	                                       @RequestParam("isActive") String isActive) 
			                               throws UsernameExistException, UserNotFoundException, EmailExistException {
		
		User newUser = userService.addNewUser(firstName, lastName, username, email, role, 
				Boolean.parseBoolean(isNotLocked), Boolean.parseBoolean(isActive));
		return new ResponseEntity<>(newUser, HttpStatus.OK);
	}
	
	@PostMapping("/update")
	public ResponseEntity<User> update(@RequestParam("currentUsername") String currentUsername,
			                           @RequestParam("firstName") String firstName,
			                           @RequestParam("lastName") String lastName,
                                       @RequestParam("email") String email,
                                       @RequestParam("username") String username,
                                       @RequestParam("role") String role,
                                       @RequestParam("isNotLocked") String isNotLocked,
                                       @RequestParam("isActive") String isActive) throws UsernameExistException, UserNotFoundException, EmailExistException {
		
		User updatedUser = userService.updateUser(currentUsername, firstName, lastName, username, email, role, 
				Boolean.parseBoolean(isNotLocked), Boolean.parseBoolean(isActive));
		return new ResponseEntity<>(updatedUser, HttpStatus.OK);
	}
	
	@GetMapping("/find/{username}")
	public ResponseEntity<User> getUser(@PathVariable("username") String username) {
		User user = userService.findUserByUsername(username);
		return new ResponseEntity<>(user, HttpStatus.OK);
	}
	
	@GetMapping("/list")
	public ResponseEntity<List<User>> getAllUsers() {
		List<User> users = userService.getUsers();
		return new ResponseEntity<>(users, HttpStatus.OK);
	}
	
	@GetMapping("/resetPassword/{email}")
	public ResponseEntity<HttpResponse> resetPassword(@PathVariable("email") String email) throws EmailNotFoundException {
		userService.resetPassword(email);
		return response(HttpStatus.OK, RESET_CONFIRMED_EMAIL_SENT + email);
	}
	
	private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
		return null;
	}

	private void authenticateUser(String username, String password) {
		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
	}
	
	@SuppressWarnings("static-access")
	private HttpHeaders getJwtHeader(UserPrincipal userPrincipal) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(securityConstant.JWT_TOKEN_HEADER, jwtTokenProvider.generateJwtToken(userPrincipal));
		return headers;
	}
}
