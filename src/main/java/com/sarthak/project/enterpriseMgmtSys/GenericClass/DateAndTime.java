package com.sarthak.project.enterpriseMgmtSys.GenericClass;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class DateAndTime {
//	@Autowired
//	private CardRepository cardRepository;	
//	
//	@Autowired
//	private CustomerRepository customerRepository;
//	
//	@Autowired
//	private UserRepository userRepository;
	
	public static String generateDateAndTime() {
		LocalDateTime localDateTime = LocalDateTime.now();
		DateTimeFormatter formattedDateTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS");
		return localDateTime.format(formattedDateTime);
	}
}
