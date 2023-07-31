package com.sarthak.project.enterpriseMgmtSys.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.sarthak.project.enterpriseMgmtSys.GenericClass.ResponseDto;
import com.sarthak.project.enterpriseMgmtSys.payload.UserDto;

public interface OnboardService {
	//Login with Thymeleaf
	public String login(HttpServletRequest request, HttpSession session);
	String getErrorMessage(HttpServletRequest request, String key);
	
	//Register with Thymeleaf
	ResponseDto addUser(String username, String password);
	
	
	//Login with Postman
	ResponseDto loginApi(HttpServletRequest request, HttpSession session);
	
	//Register with Postman
}