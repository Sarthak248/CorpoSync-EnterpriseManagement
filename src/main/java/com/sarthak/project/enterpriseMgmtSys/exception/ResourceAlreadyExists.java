package com.sarthak.project.enterpriseMgmtSys.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceAlreadyExists extends RuntimeException { // exception called when entry already exists, prevents duplication
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String columnName;
	private Object fieldValue;
	
	public ResourceAlreadyExists(Object fieldValue, String columnName) {
		super(String.format("The value: %s already exists in the column: %s",fieldValue, columnName));
		this.columnName = columnName;
		this.fieldValue = fieldValue;
	}
}