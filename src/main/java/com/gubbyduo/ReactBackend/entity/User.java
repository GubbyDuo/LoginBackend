package com.gubbyduo.ReactBackend.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "users")
public class User {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;
	@Column(name="username")
	String userName;
	@Column(name="password")
	String password;
	@Column(name="email")
	String email;
	@Column(name="first_name")
	String firstName;
	@Column(name="profile_picture_link")
	String profilePicLink;
	
	
	public User() {}
	
	public User(String userName, String password, String email, String firstname) {
		this.userName = userName;
		this.password = password;
		this.email = email;
		this.firstName = firstname;
	}
	
	public User(String userName, String password, String email, String firstname, String profilePicLink ) {
		this.userName = userName;
		this.password = password;
		this.email = email;
		this.firstName = firstname;
		this.profilePicLink = profilePicLink;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public void setProfilePicLink(String profilePicLink) {
		this.profilePicLink = profilePicLink;
	}
	
	public String getProfilePicLink() {
		return profilePicLink;
	}
	
	
}
