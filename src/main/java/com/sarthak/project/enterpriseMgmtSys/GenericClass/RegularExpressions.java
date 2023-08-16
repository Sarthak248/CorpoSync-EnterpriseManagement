package com.sarthak.project.enterpriseMgmtSys.GenericClass;

import org.springframework.stereotype.Component;

@Component
public class RegularExpressions {
	public static final String MOBILE_NUMBER_FORMAT = "^\\d{10}$";
	public static final String USER_NAME_FORMAT = "^[A-Za-z ,.'-]$";
	public static final String EMAIL_FORMAT = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]$";
	public static final String CUSTOMER_NAME_FORMAT = "^[A-Za-z ,.'-]+$";
	public static final String CUSTOMER_ID_FORMAT = "^CR\\d{5}";
	public static final String CARD_NAME_FORMAT = "^[a-z0-9A-Z ,.'-]+$";

	public static final String CARD_ID_FORMAT = "^CARD\\d{5}";
	public static final String ENTERPRISE_ID_FORMAT = "^ENTP\\d{5}";

}