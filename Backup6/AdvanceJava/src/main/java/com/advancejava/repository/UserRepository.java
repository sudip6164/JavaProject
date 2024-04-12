package com.advancejava.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.advancejava.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
	User findByEmail(String email);
	boolean existsByEmail(String email);
	boolean existsByEmailAndPassword(String username, String password);
}
