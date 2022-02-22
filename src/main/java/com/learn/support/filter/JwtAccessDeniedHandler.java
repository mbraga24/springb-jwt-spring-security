package com.learn.support.filter;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learn.support.constant.SecurityConstant;
import com.learn.support.domain.HttpResponse;

//=================================> JwtAuthenticationEntryPoint <=================================
// This call will send an Access Denied message to the user if they try to access a resource but 
// they don't have enough permission to access it. This class will override the default method 
// handle().
//=================================================================================================

public class JwtAccessDeniedHandler implements AccessDeniedHandler {

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException, ServletException {
		
		HttpResponse httpResponse = new 
				HttpResponse(HttpStatus.UNAUTHORIZED.value(), 
						     HttpStatus.UNAUTHORIZED, 
						     HttpStatus.UNAUTHORIZED.getReasonPhrase().toUpperCase(),
						     SecurityConstant.ACCESS_DENIED_MESSAGE);
		
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		OutputStream outputStream = response.getOutputStream();
		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(outputStream, httpResponse);
		outputStream.flush();
	}
}
