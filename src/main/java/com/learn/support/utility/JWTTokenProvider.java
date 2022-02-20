package com.learn.support.utility;

import static com.learn.support.constant.SecurityConstant.AUTHORITIES;
import static com.learn.support.constant.SecurityConstant.EXPIRATION_TIME;
import static com.learn.support.constant.SecurityConstant.YOUR_SUPPORT_CO;
import static com.learn.support.constant.SecurityConstant.YOUR_SUPPORT_CO_ADMINISTRATION;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.learn.support.domain.UserPrincipal;

public class JWTTokenProvider {
	
	// The secret will be a random string to code and/or decode the token.
	// ***	Normally the secret will be in a secure service in a property file
	//			and that will be retrieved from that secure service any time you need it. ***
	
	// @Value annotation will grab the value of the property from the property/yml file.
	@Value("jwt.secret")
	private String secret;
	
	// Method to generate the token.
	// This method will take a UserPrincipal to verify their information, make sure they exist and once
	// their credentials verify the application can create a userPrincipal.	
	// !BUG(SOLVED): The operator + is undefined for the argument type(s) Date, long
	// !BUG(SOLVED): The method withExpiresAt(Date) in the type JWTCreator.Builder is not applicable for the arguments (long)
	public String generateJwtToken(UserPrincipal userPrincipal) {
		String[] permissions = getClaimsFromUser(userPrincipal);
		return JWT.create()
				.withIssuer(YOUR_SUPPORT_CO)
				.withAudience(YOUR_SUPPORT_CO_ADMINISTRATION)
				.withIssuedAt(new Date())
				.withSubject(userPrincipal.getUsername())
				.withArrayClaim(AUTHORITIES, permissions)
				.withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
				.sign(Algorithm.HMAC512(secret.getBytes()));
	}
	
	private String[] getClaimsFromUser(UserPrincipal userPrincipal) {
		
	}
	
	

}
