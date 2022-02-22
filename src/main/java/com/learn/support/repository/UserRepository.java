package com.learn.support.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.learn.support.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
	
	User findUserByUsername(String username);
	
	User findUserByEmail(String email);
}
