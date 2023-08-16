package com.sarthak.project.enterpriseMgmtSys.GenericClass;

import org.springframework.stereotype.Component;

@Component
public final class StatusResponse {
	//REQUEST IDS
	public static final int CREATE_REQUEST = 1;
	public static final int GETALL_REQUEST = 2;
	public static final int GET_REQUEST = 3;
	public static final int UPDATE_REQUEST = 4;
	public static final int ACTIVATE_REQUEST = 5;
	public static final int DELETE_REQUEST = 6;
	public static final int SEARCH_REQUEST = 7;
	
	public static final int CUSTOMER_REQUEST = 100;
	public static final int CARD_REQUEST = 200;
	
	//PAGE
	public static final int PAGE_SIZE = 5;
//	public static final int SEARCH_REQUEST = 7;
	
	//STATUS
	public static final String ACTIVE_STATUS = "ACTIVE";
	public static final String DEACTIVE_STATUS = "DEACTIVE";

	//HTTP STATUS CODES
	public static final String CREATED_STATUS_CODE = "201";
	public static final String SUCCESS_STATUS_CODE = "200";
	public static final String FAILURE_STATUS_CODE = "404";
	public static final String BAD_REQUEST_STATUS_CODE = "400";
	public static final String SERVER_ERROR_STATUS_CODE = "500";

	//STATUS MESSAGES
	public static final String BAD_REQUEST = "Bad Request: Invalid or Incomplete request";
	
	public static final String CUSTOMER_CREATED = "Customer has been created successfully";
	public static final String CARD_CREATED = "Card has been created successfully";
	
	public static final String NO_CUSTOMERS_IN_DB = "No customers have been created yet";//db is empty
	public static final String NO_CARDS_IN_DB= "No cards have been created yet";
	public static final String CUSTOMER_DOES_NOT_EXIST = "Customer not found"; //associated with some id
	public static final String CARD_DOES_NOT_EXIST = "Card not found"; //with associated params like id, name
	
	public static final String FETCHED_ALL_CUSTOMERS = "Fetched all customers successfully";
	public static final String FETCHED_ALL_CARDS = "Fetched all cards successfully";
	
	public static final String FETCHED_INDIVIDUAL_CUSTOMER = "Fetched customer details successfully";
	public static final String FETCHED_INDIVIDUAL_CARD = "Fetched card details successfully";

	public static final String CUSTOMER_UPDATED = "Updated customer details successfully";
	public static final String CARD_UPDATED = "Card successfully updated";
	public static final String CARD_AND_CUSTOMER_UPDATED = "Card and Customer updated successfully"; //when changing a card's owner
	
	public static final String CUSTOMER_DEACTIVATED = "Deactivated Customer and respective cards successfully";
	public static final String CARD_DEACTIVATED = "Card deactivated successfully";
	
	public static final String MOBILE_INVALID_FORMAT = "Mobile number is of invalid format";
	public static final String NAME_INVALID_FORMAT = "Name is of invalid format";
	public static final String EMAIL_INVALID_FORMAT = "Email is of invalid format";
	
	public static final String USERNAME_ALREADY_EXISTS = "User with this name already exists";
	public static final String EMAIL_ALREADY_EXISTS = "User with this email already exists";
	public static final String MOBILE_ALREADY_EXISTS = "Mobile number already exists"; //no duplicates
	
	public static final String CUSTOMER_HAS_NO_CARDS = "Customer does not have any cards";
	
	public static final String CUSTOMER_ALREADY_ACTIVE = "Customer is already active";
	public static final String CARD_ALREADY_ACTIVE = "Card is already active";
	public static final String CUSTOMER_ALREADY_DEACTIVE = "Customer is already deactive";
	public static final String CARD_ALREADY_DEACTIVE = "Card is already deactive";
	
	public static final String CARD_ACTIVATED = "Card has been activated again";
	public static final String CUSTOMER_ACTIVATED = "Customer has been activated again";
	
	public static final String BLOCKED_CUSTOMER = "Customer is deactivated, cannot perform action";
	public static final String BLOCKED_CARD = "Card is deactivated, cannot perform action";
	
	public static final String SERVER_ERROR = "OOPS! There was a technical error";

}