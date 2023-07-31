package com.sarthak.project.enterpriseMgmtSys.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = false)
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NegativeValue extends RuntimeException { // called when a value such as cardCount is negative
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String columnName;
	private Object fieldValue;
	
	public NegativeValue(Object fieldValue, String columnName) {
		super(String.format("The value: %s from the column: %s is negative",fieldValue, columnName));
		this.columnName = columnName;
		this.fieldValue = fieldValue;
	}
}