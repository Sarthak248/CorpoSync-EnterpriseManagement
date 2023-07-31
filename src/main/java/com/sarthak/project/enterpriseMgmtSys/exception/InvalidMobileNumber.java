package com.sarthak.project.enterpriseMgmtSys.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = false)
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class InvalidMobileNumber extends RuntimeException { // exception called when format of phone number is incorrect
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Object resourceName;	
	public InvalidMobileNumber(Object resourceName) {
		super(String.format("%s is an Invalid Mobile Number",resourceName));
		this.resourceName = resourceName;
	}
}