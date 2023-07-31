package com.sarthak.project.enterpriseMgmtSys.GenericClass;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sarthak.project.enterpriseMgmtSys.repository.CustomerRepository;

@Component
public class IdGeneratorCustomer {
    private static final String PREFIX = "CR";
    private static final int ID_LENGTH = 5;
    @Autowired
    private CustomerRepository customerRepository;
    public IdGeneratorCustomer(CustomerRepository customerRepository) {
    	this.customerRepository = customerRepository;
    }
    
	long incrementedValue;
	String digit;

    public String generateCustomerId() {
    	if(customerRepository.maximumExistingCustomerId() == null) {
    		digit = "1";
    	}
    	else {
    		digit = customerRepository.maximumExistingCustomerId().toString();
    		digit = digit.replaceAll("\\D+", "");    	
    		int incremented = Integer.valueOf(digit);
    		incremented++;
    		digit = String.format("%05d", incremented);
    	}    		
        String paddedNumber = String.format("%0" + ID_LENGTH + "d", Integer.valueOf(digit));
        return PREFIX + paddedNumber;
    }
}
