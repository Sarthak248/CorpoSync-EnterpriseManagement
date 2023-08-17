package com.sarthak.project.enterpriseMgmtSys.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.ui.Model;

import com.sarthak.project.enterpriseMgmtSys.GenericClass.ResponseDto;
import com.sarthak.project.enterpriseMgmtSys.payload.UserDto;

public interface AuthenticationService {
	//Login with Thymeleaf
	public String login(HttpServletRequest request, HttpSession session, Model model);
	String getErrorMessage(HttpServletRequest request, String key);
	
	public ResponseDto memberLogin(String customerId);
	public ResponseDto customerIdVerification(String customerId, String mobile);
	
	
	//Register with Thymeleaf
	ResponseDto addUser(String username, String password, String email);
	
	//Forgot with Thyme
	ResponseDto forgotPass(String userName);
	ResponseDto verifyEmail(UserDto userDto, String email);
	
	//Reset with Thyme
	ResponseDto resetPass(UserDto user, String newPass, String confirmPass);
	
	//Login with Postman
	ResponseDto loginApi(HttpServletRequest request, HttpSession session);
	
	
	//Register with Postman
}