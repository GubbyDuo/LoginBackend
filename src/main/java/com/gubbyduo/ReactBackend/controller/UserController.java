package com.gubbyduo.ReactBackend.controller;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.gubbyduo.ReactBackend.repository.UserRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import com.gubbyduo.ReactBackend.entity.LoginRequest;
import com.gubbyduo.ReactBackend.entity.User;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {
	
	private final String JWT_KEY = "JWTKEY";
	
	@Autowired
	public UserRepository userRepository;
	
	@Autowired
	public BCryptPasswordEncoder encoder;
	
	public UserController(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	
	@GetMapping("/users")
	public ResponseEntity<List<User>> all(){
		return new ResponseEntity<List<User>>(userRepository.findAll(), HttpStatus.OK);
	}
	
	@GetMapping("user/{id}")
	public ResponseEntity<User> getUser(/*@RequestHeader(value="JWT")*/ String token, @PathVariable long id) {
		Optional<User> userOptional = userRepository.findById(id);
		if(userOptional.isPresent()) {
			return new ResponseEntity<User>(userOptional.get(), HttpStatus.OK);
		}
		else {
			return new ResponseEntity<User>(new User(), HttpStatus.NOT_FOUND);
		}
	}
	
	@PostMapping("user/new")
	public ResponseEntity<User> registerUser(@RequestBody User newUser) {
		String hashedPassword = encoder.encode(newUser.getPassword());
		newUser.setPassword(hashedPassword);
		try {
			return new ResponseEntity<User>(userRepository.save(newUser), HttpStatus.CREATED);
		} catch(Exception e){
			return new ResponseEntity<User>(new User(), HttpStatus.CONFLICT);
		}
	}
	
	@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
	@PostMapping("user/login")
	public ResponseEntity<Void> loginUser(@RequestBody LoginRequest loginRequest){
		//Verify the username and password combo is correct
		User user = userRepository.findUserByUserName(loginRequest.getUserName());
		boolean passwordMatches = encoder.matches(loginRequest.getPassword(), user.getPassword());
		
		//if it is correct, send back a jwt with the username as the subject and a 'set-cookie' header
		
		if(passwordMatches) {
			String jwtKey =  Jwts.builder()
					.setSubject(loginRequest.getUserName())
					.setExpiration(new Date(System.currentTimeMillis() + 86400000 * 30))
					.signWith(SignatureAlgorithm.HS512, JWT_KEY)
					.compact();
			System.out.println(user
					.getUserName());
			HttpHeaders headers = new HttpHeaders();
			headers.add("Set-Cookie", "jwt=" + jwtKey + "; Path=/; Max-Age=604800");
			System.out.println(headers);
			return new ResponseEntity<>(headers, HttpStatus.OK);
			}
		else {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
	}
	
}
