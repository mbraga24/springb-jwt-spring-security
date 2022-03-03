package com.learn.support.service;

import java.util.concurrent.ExecutionException;
//import java.util.concurrent.TimeUnit;

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
// In case the user attempt a few times and login successfully the app will remove 
// the initial failed number of attempts
//
// ** Extension: google guava **
//===============================================================================

@Service
public class LoginAttemptService {
	private static final int MAXIMUM_NUMBER_OF_ATTEMPTS = 5;
	private static final int ATTEMPT_INCREMENT = 1;
	private static final String SPEC = "maximumSize=1000,expireAfterWrite=15m"; // maximumSize ??
	private LoadingCache<String, Integer> loginAttemptCache;

  // String | Integer
  // User_1 | 1 + 1 (number of attempts by the user)
	
	// ===> Should I consider blocking the IP Address?
	public LoginAttemptService() {
		super();
		
		// -> newBuilder()
		// Constructs a new CacheBuilder instance with default settings, including strong keys, 
		// strong values, and no automatic eviction of any kind.
		
		// -> build(CacheLoader<? super K1,V1> loader)
		// Builds a cache, which either returns an already-loaded value for a given key or 
		// atomically computes or retrieves it using the supplied CacheLoader.
		
		// -> CacheLoader
		// Computes or retrieves values, based on a key, for use in populating a LoadingCache.
		
		// =====> CODE 1:
		// loginAttemptCache = CacheBuilder.newBuilder().expireAfterWrite(15, TimeUnit.MINUTES)
		//  .maximumSize(100).build(new CacheLoader<String, Integer>(){
		//   public Integer load(String key) {
		//				return 0;
		//   }
		// });
		
		// =====>> CODE 2:
		loginAttemptCache = CacheBuilder.from(SPEC).build(new CacheLoader<String, Integer>(){
			public Integer load(String key) {
				return 0;
			}
		});
	}
	
	// Remove user from the cache
	public void evictUserFromLoginAttemptCache(String username) {
		loginAttemptCache.invalidate(username); // invalidate(): Discards any cached value for key key.
	}
	
	// Increment the number of attempts from user to the cache by one
	public void addUserToLoginAttemptCache(String username) {
		int attempts = 0;
		try {
			attempts = ATTEMPT_INCREMENT + loginAttemptCache.get(username); // get(): Returns the value associated with key in this cache, obtaining that value from valueLoader if necessary.
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		loginAttemptCache.put(username, attempts); // put(): Associates value with key in this cache.	
	}

	// Check if user's number of attempts has exceeded the maximum number of attempts
	public boolean hasExceededMaxAttempts(String username) {
		try {
			return loginAttemptCache.get(username) >= MAXIMUM_NUMBER_OF_ATTEMPTS;			
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return false;
	}
}
