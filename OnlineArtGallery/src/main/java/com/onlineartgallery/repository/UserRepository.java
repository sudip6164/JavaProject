package com.onlineartgallery.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.onlineartgallery.model.User;



public interface UserRepository extends JpaRepository<User, Integer> {
//	User findByUsernameAndPassword(String username,String password);
	boolean existsByEmailAndPassword(String email, String password);
}
