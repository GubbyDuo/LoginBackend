package com.gubbyduo.ReactBackend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.gubbyduo.ReactBackend.repository.UserRepository;
import com.gubbyduo.ReactBackend.configuration.PasswordEncoder;
import com.gubbyduo.ReactBackend.entity.LoginRequest;
import com.gubbyduo.ReactBackend.entity.User;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	BCryptPasswordEncoder encoder;
	
	public UserController(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	
	@GetMapping("/users")
//	String all() {
//	return "it works!";
	List<User> all(){
		return userRepository.findAll();
	}
	
//	@RequestMapping(value = "/error")
//	String error() {
//		return "error";
//	}
	
	@GetMapping("user/{id}")
	User getUser(@PathVariable long id) {
		return userRepository.findById(id)
				.orElse(null);
	}
	
	@PostMapping("user/new")
	User registerUser(@RequestBody User newUser) {
		String hashedPassword = encoder.encode(newUser.getPassword());
		newUser.setPassword(hashedPassword);
		return userRepository.save(newUser);
	}
	
	@PostMapping("user/login")
	User loginUser(@RequestBody LoginRequest loginRequest){
		User user = userRepository.findUserByUserName(loginRequest.getUserName());
		Boolean passwordMatches = encoder.matches(loginRequest.getPassword(), user.getPassword());
		if(passwordMatches) {
			System.out.println(passwordMatches);
			return userRepository.save(user);
		}
		else return new User();
	}
	
}
