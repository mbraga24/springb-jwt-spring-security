package com.learn.support.filter;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learn.support.constant.SecurityConstant;
import com.learn.support.domain.HttpResponse;

//=====================================> JwtAuthenticationEntryPoint <=====================================
// This filter will trigger whenever the authentication fails. Generally if the user tries to do anything 
// and they are not authenticated there is a forbidden entry point that is going to be trigger. This class
// will override the generic response sent to the user when they are not authenticated.
//=========================================================================================================

public class JwtAuthenticationEntryPoint extends Http403ForbiddenEntryPoint {
	
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
			throws IOException {
		
		HttpResponse httpResponse = new 
				HttpResponse(HttpStatus.FORBIDDEN.value(), 
						     HttpStatus.FORBIDDEN, 
						     HttpStatus.FORBIDDEN.getReasonPhrase().toUpperCase(),
						     SecurityConstant.FORBIDDEN_MESSAGE);
		
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setStatus(HttpStatus.FORBIDDEN.value());
		OutputStream outputStream = response.getOutputStream();
		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(outputStream, httpResponse);
		outputStream.flush();
	}
	 
}
