package com.gubbyduo.ReactBackend.controller;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.gubbyduo.ReactBackend.repository.UserRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoder;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.gubbyduo.ReactBackend.entity.LoginRequest;
import com.gubbyduo.ReactBackend.entity.User;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {
	
	private final String JWT_KEY = "JWTKEY";
	
	@Autowired
	public AmazonS3 amazonS3;
	
	@Autowired
	public UserRepository userRepository;
	
	@Autowired
	public BCryptPasswordEncoder encoder;
	
	public UserController(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	@CrossOrigin(origins = "http://localhost:3000")
	@PostMapping(path = "/user/uploadProfilePicture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public void uploadProfilePicture(@RequestParam("File") MultipartFile file, @RequestHeader("Authorization") String jwt){
		System.out.println(jwt);
		Claims userInfo = Jwts.parser().setSigningKey(JWT_KEY).parseClaimsJws(jwt).getBody();
		System.out.println(userInfo.get("userId"));
		long userId = (long) userInfo.get("userId");
		UUID fileKey = UUID.randomUUID();
		
		Optional<User> user = userRepository.findById(userId);
		user.get().setProfilePicLink(fileKey.toString());
		
		
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(file.getSize());
		try {
			amazonS3.putObject("timeline.profile.pictures", fileKey.toString(), file.getInputStream(), metadata);
		} catch (AmazonServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SdkClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
//					.setExpiration(new Date(System.currentTimeMillis() + 86400000 * 30))
					.signWith(SignatureAlgorithm.HS512, JWT_KEY)
					.claim("userId", user.getId())
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
