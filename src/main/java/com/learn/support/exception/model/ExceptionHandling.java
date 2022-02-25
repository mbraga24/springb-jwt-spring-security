package com.learn.support.exception.model;

import java.io.IOException;
import java.util.Objects;

import javax.persistence.NoResultException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.learn.support.domain.HttpResponse;

// ===================================> ExceptionHandling <===================================
// Any time a controller extending ExceptionHandling class receives a request, the application
// is going to look into the ExceptionHandling class.
//============================================================================================

@RestControllerAdvice
public class ExceptionHandling implements ErrorController {
	
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());
	private static final String ACCOUNT_LOCKED = "Your account has been locked.";
	private static final String METHOD_IS_NOT_ALLOWED = "This request is not allowed on this endpoint.";
	private static final String INTERNAL_SERVER_ERROR_MSG = "An error occurred while processing the request.";
	private static final String INCORRECT_CREDENTIALS = "Username / password incorrect. Please try again.";
	private static final String ACCOUNT_DISABLED = "Your account has been disabled.";
	private static final String ERROR_PROCESSING_FILE = "Error occurred while processing file.";
	private static final String NOT_ENOUGH_PERMISSION = "You do not have enough permission.";
	private static final String ERROR_PATH = "/error"; // Preferred strategy to a 404 not found error path
	
	@ExceptionHandler(DisabledException.class)
	public ResponseEntity<HttpResponse> accountDisabledException() {
		return createHttpResponse(HttpStatus.BAD_REQUEST, ACCOUNT_DISABLED);
	}
	
	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<HttpResponse> badCredentialsException() {
		return createHttpResponse(HttpStatus.BAD_REQUEST, INCORRECT_CREDENTIALS);
	}
	
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<HttpResponse> accessDeniedException() {
		return createHttpResponse(HttpStatus.FORBIDDEN, NOT_ENOUGH_PERMISSION);
	}
	
	@ExceptionHandler(LockedException.class)
	public ResponseEntity<HttpResponse> lockedException() {
		return createHttpResponse(HttpStatus.UNAUTHORIZED, ACCOUNT_LOCKED);
	}
	
	@ExceptionHandler(TokenExpiredException.class)
	public ResponseEntity<HttpResponse> tokenExpiredException(TokenExpiredException exception) {
		return createHttpResponse(HttpStatus.UNAUTHORIZED, exception.getMessage());
	}
	
	@ExceptionHandler(EmailExistException.class)
	public ResponseEntity<HttpResponse> emailExistException(EmailExistException exception) {
		return createHttpResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
	}
	
	@ExceptionHandler(UsernameExistException.class)
	public ResponseEntity<HttpResponse> usernameExistException(UsernameExistException exception) {
		return createHttpResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
	}
	
	@ExceptionHandler(EmailNotFoundException.class)
	public ResponseEntity<HttpResponse> emailNotFoundException(EmailNotFoundException exception) {
		return createHttpResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
	}
	
	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<HttpResponse> userNotFoundException(UserNotFoundException exception) {
		return createHttpResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
	}
	
	@ExceptionHandler(IOException.class)
	public ResponseEntity<HttpResponse> iOException(IOException exception) {
		LOGGER.error("INTERNAL SERVER ===> " + exception.getMessage());
		return createHttpResponse(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_PROCESSING_FILE);
	}
	
	@ExceptionHandler(NoResultException.class)
	public ResponseEntity<HttpResponse> noResultException(NoResultException exception) {
			LOGGER.error(exception.getMessage());
		return createHttpResponse(HttpStatus.NOT_FOUND, exception.getMessage());
	}
	
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<HttpResponse> methodNotSupportedException(HttpRequestMethodNotSupportedException exception) {
		HttpMethod supportedMethod = Objects.requireNonNull(exception.getSupportedHttpMethods()).iterator().next();
		return createHttpResponse(HttpStatus.METHOD_NOT_ALLOWED, String.format(METHOD_IS_NOT_ALLOWED, supportedMethod));
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<HttpResponse> internalServerErrorException(Exception exception) {
		LOGGER.error(exception.getMessage());
		return createHttpResponse(HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR_MSG);
	}
	
	private ResponseEntity<HttpResponse> createHttpResponse(HttpStatus httpStatus, String message) {
		HttpResponse httpResponse = new HttpResponse(httpStatus.value(), httpStatus, 
		httpStatus.getReasonPhrase().toUpperCase(), message.toUpperCase());
		return new ResponseEntity<>(httpResponse, httpStatus);
	}
	
	// Preferred strategy to a 404 not found error path
	@RequestMapping(ERROR_PATH)
	public ResponseEntity<HttpResponse> notFound404() {
		return createHttpResponse(HttpStatus.NOT_FOUND, "No mapping for this URL.");
	}
	
	// Override method that returns the path to the error. The method that is called to show the page.
	public String getErrorPath() {
		return ERROR_PATH;
	}
}

