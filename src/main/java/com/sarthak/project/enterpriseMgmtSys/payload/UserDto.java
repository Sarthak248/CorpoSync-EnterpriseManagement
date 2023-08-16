package com.sarthak.project.enterpriseMgmtSys.payload;

public class UserDto {
	private String username;
	
	private String email;
	
	private String encodedEmail;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getEncodedEmail() {
		return encodedEmail;
	}

	public void setEncodedEmail(String encodedEmail) {
		this.encodedEmail = encodedEmail;
	}
	
	
}