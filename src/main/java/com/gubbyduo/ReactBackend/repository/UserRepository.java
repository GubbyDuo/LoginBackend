package com.gubbyduo.ReactBackend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gubbyduo.ReactBackend.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	public User findUserByUserName(String userName);
	
	public Optional<User> findUserByUserNameAndPassword(String userName, String Password);
	
}
