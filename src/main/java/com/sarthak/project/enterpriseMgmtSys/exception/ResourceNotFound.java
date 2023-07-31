package com.sarthak.project.enterpriseMgmtSys.exception;

import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=false) //no superclass
@Data
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFound extends RuntimeException { // exception called when specific entry does not exist in the database
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; // identifier for serialization
	private String resourceName;
	private String fieldName;
	private Object fieldValue;
	
	//status code - 404, status message - "Resource Not Found", object result - String
	
	public ResourceNotFound(String resourceName, String fieldName, Object fieldValue) {
		super(String.format("%s not found with %s : '%s'",resourceName, fieldName, fieldValue));
		this.resourceName = resourceName;
		this.fieldName = fieldName;
		this.fieldValue = fieldValue;
	}
}