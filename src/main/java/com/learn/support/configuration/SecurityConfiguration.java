package com.learn.support.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.learn.support.constant.SecurityConstant;
import com.learn.support.filter.JwtAccessDeniedHandler;
import com.learn.support.filter.JwtAuthenticationEntryPoint;
import com.learn.support.filter.JwtAuthorizationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) // will allow security at a method level instead of just having at this class level.
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	private JwtAuthorizationFilter jwtAuthorizationFilter;
	private JwtAccessDeniedHandler jwtAccessDeniedHandler;
	private JwtAuthenticationEntryPoint jwtAuthenticationEntreyPoint;
	private UserDetailsService userDetailsService;
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	public SecurityConfiguration(JwtAuthorizationFilter jwtAuthorizationFilter,
			JwtAccessDeniedHandler jwtAccessDeniedHandler, JwtAuthenticationEntryPoint jwtAuthenticationEntreyPoint,
			@Qualifier("UserDetailsService")UserDetailsService userDetailsService, BCryptPasswordEncoder bCryptPasswordEncoder) {
		super();
		this.jwtAuthorizationFilter = jwtAuthorizationFilter;
		this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
		this.jwtAuthenticationEntreyPoint = jwtAuthenticationEntreyPoint;
		this.userDetailsService = userDetailsService;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}

	// There are two methods that I need to override to create the security configuration
	// in the application.
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		// The AuthenticationManagerBuilder need to tell which UserDetailsService the application
		// is using. 
		auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// Pass all the information that Spring Security needs to manage endpoint or url. 
		// e.g.: which url is secure, which one is open, session management, filters and handlers.
		
		http.csrf().disable().cors() // disable CSRF (cross site request forgery) ; add CORS (cross origin resource sharing) anyone that tries to access the application with a domain that hasn't been specified will be rejected
		.and()
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // specify session management as STATELESS. the app will not use session to keep track of users, but will use JWT instead by a token that will be provided and will check if the user has it. 
		.and()
		.authorizeRequests().antMatchers(SecurityConstant.PUBLIC_URLS).permitAll() // specify all the URLs that are public and permit them all.
		.anyRequest().authenticated() // any request that it not part of the permitted URLs will be authenticated
		.and()
		.exceptionHandling().accessDeniedHandler(jwtAccessDeniedHandler) // custom accessDeniedHandler
		.authenticationEntryPoint(jwtAuthenticationEntreyPoint) // custom authenticationEntryPoint 
		.and()
		.addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class); // authorization filter
	}
	
	// Bring a bean for the authentication manager
	
	@Bean
	@Override
	public AuthenticationManager authenticationManager() throws Exception {
		return super.authenticationManagerBean();
	}
	
}
