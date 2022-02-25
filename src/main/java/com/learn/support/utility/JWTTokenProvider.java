package com.learn.support.utility;

import static com.learn.support.constant.SecurityConstant.AUTHORITIES;
import static com.learn.support.constant.SecurityConstant.EXPIRATION_TIME;
import static com.learn.support.constant.SecurityConstant.TOKEN_CANNOT_BE_VERIFIED;
import static com.learn.support.constant.SecurityConstant.YOUR_SUPPORT_CO;
import static com.learn.support.constant.SecurityConstant.YOUR_SUPPORT_CO_ADMINISTRATION;
import static java.util.Arrays.stream;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.learn.support.domain.UserPrincipal;

//=====================================> JWTTokenProvider <=====================================
// The JWTTokenProvider will be used to generate tokens, verify user token and user information.
//==============================================================================================

@Component
public class JWTTokenProvider {
	
	// The "secret" will be a random string to code and/or decode the token.
	// ***	Normally the secret will be in a secure server in a property file
	//			and that will be retrieved from that secure server any time you need it. ***
	
	// @Value annotation will grab the value of the property from the property/yml file.
	// ** Interpolate string **
	@Value("${jwt.secret}")
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
	
	// Method to get authorities from token.
	public List<GrantedAuthority> getAuthorities(String token) {
		String[] permissions = getClaimsFromToken(token);
		return stream(permissions).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
	}
	
	// Method to get the authentication of the user
	// If the token is correct, the application needs to get the authentication of the user from 
	// Spring Security and set that authentication in the Spring Security Context.  
	// Once the application has this authentication it will be able to set it to the Spring Security context to allow 
	// it to process the user's request.
	public Authentication getAuthentication(String username, List<GrantedAuthority> authorities, HttpServletRequest request) {
		UsernamePasswordAuthenticationToken userPasswordAuthToken = 
				new UsernamePasswordAuthenticationToken(username, null, authorities);
		userPasswordAuthToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); // Setting up information about the current user in the Spring Security context
		return userPasswordAuthToken;
	}

	public boolean isTokenValid(String username, String token) {
		JWTVerifier verifier = getJWTVerifier();
		return StringUtils.isNotEmpty(username) && !isTokenExpired(verifier, token);
	}
	
	public String getSubject(String token) {
		JWTVerifier verifier = getJWTVerifier();
		return verifier.verify(token).getSubject();
	}
	
	//	**********************************************
	//					HELPER METHODS
	//	**********************************************
	
	private String[] getClaimsFromToken(String token) {
		JWTVerifier verifier = getJWTVerifier();
		return verifier.verify(token).getClaim(AUTHORITIES).asArray(String.class);
		
		// =================> Code for getClaim() from JWTDecoder Class
		// @Override
		// public Claim getClaim(String name) {
		//  return payload.getClaim(name);
		// }
	}
	
	private boolean isTokenExpired(JWTVerifier verifier, String token) {
		Date expiration = verifier.verify(token).getExpiresAt();
		return expiration.before(new Date());
	}
	
	private JWTVerifier getJWTVerifier() {
		JWTVerifier verifier;
		try {
			
			Algorithm algorithm = Algorithm.HMAC512(secret);
			verifier = JWT.require(algorithm).withIssuer(YOUR_SUPPORT_CO).build();
		
		} catch (JWTVerificationException exception) {
			throw new JWTVerificationException(TOKEN_CANNOT_BE_VERIFIED);
		}
		return verifier;
	}
	
	private String[] getClaimsFromUser(UserPrincipal userPrincipal) {
		List<String> authorities = new ArrayList<>();
		for (GrantedAuthority grantedAuthority : userPrincipal.getAuthorities()) {
			authorities.add(grantedAuthority.getAuthority());
		}
		return authorities.toArray(new String[0]);
	}

}
