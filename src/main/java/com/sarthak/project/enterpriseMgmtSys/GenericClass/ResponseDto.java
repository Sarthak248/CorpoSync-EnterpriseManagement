package com.sarthak.project.enterpriseMgmtSys.GenericClass;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDto {

	private String statusCode;

	private String message;

	private Object result;

}