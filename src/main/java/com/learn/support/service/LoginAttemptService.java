package com.learn.support.service;

import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

//=============================> LoginAttemptService <===========================
// The LoginAttemptService class will work as a service to check the number of 
// times an user has attempted to login. In the cache the app will keep track of
// the user and the number of time they try to login. Every time someone tries to
// login, the logic will check how many times they have already tried and lock
// the user's account if the user reaches the maximum number of attempts. 
// 
// ** In case they attempt a few times and login successfully we will remove the
// initial failed number of attempts **
//===============================================================================

@Service
public class LoginAttemptService {
	private static final int MAXIMUM_NUMBER_OF_ATTEMPTS = 5;
	private static final int ATTEMPT_INCREMENT = 1;
	private LoadingCache<String, Interger> loginAttemptCache;

  // String | Integer
  // User_1 | 1 + 1 (number of attempts by the user)
	
	public LoginAttemptService() {
		super();
		loginAttemptCache = CacheBuilder.newBuilder().expireAfterWrite(15, TimeUnit.MINUTES)
				.maximumSize(100).build(new CacheLoader<String, Integer>(){
					public Integer load(String key) {
						return 0;
					}
				});
	}
}
