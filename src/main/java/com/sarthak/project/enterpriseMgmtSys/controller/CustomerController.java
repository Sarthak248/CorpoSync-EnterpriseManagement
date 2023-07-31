package com.sarthak.project.enterpriseMgmtSys.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sarthak.project.enterpriseMgmtSys.GenericClass.ResponseDto;
import com.sarthak.project.enterpriseMgmtSys.payload.CustomerDetailsDTO;
import com.sarthak.project.enterpriseMgmtSys.service.CustomerService;

// using @RestController to avoid using @ResponseBody annotation
@RestController
@RequestMapping("/api/customers") // base URI
public class CustomerController {
	private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

	@Autowired
	private CustomerService customerService;

	// REST API - CREATE
	@PostMapping("/create")
	public ResponseEntity<?> newCustomer(@RequestBody CustomerDetailsDTO customerDetailsDTO) {
		ResponseDto response = customerService.newCustomer(customerDetailsDTO);
		logger.info("Generated response from Customer Service");
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}
	

	// REST API - GET ALL
	@GetMapping("/getall")
	public ResponseEntity<?> getAllCustomers() { // no parameters, list of Customer objects is returned
		ResponseDto response = customerService.getAllCustomers();
		logger.info("Generated response from Customer Service");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// REST API - GET BY ID
	@GetMapping("/get/{id}") // specific URI; full URI is http://localhost:8080/api/customers/{id}
	public ResponseEntity<?> getCustomerById(@PathVariable("id") String customerId) {// path variable - id is passed and
		ResponseDto response = customerService.getCustomerById(customerId);
		logger.info("Generated response from Customer Service");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// REST API - UPDATE BY ID
	@PatchMapping("/update/{id}")
	public ResponseEntity<?> updateCustomerById(@RequestBody CustomerDetailsDTO customerDetailsDTO,
			@PathVariable("id") String customerId) {
		ResponseDto response = customerService.updateCustomerById(customerDetailsDTO, customerId);
		logger.info("Generated response from Customer Service");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	// REST API - ACTIVATE
	@PatchMapping("/activate/{id}")
	public ResponseEntity<?> activateCustomer(@PathVariable("id") String customerId) {
		ResponseDto response = customerService.activateCustomer(customerId);
		logger.info("Generated response from Customer Service");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// REST API - DELETE BY ID
	@DeleteMapping("/delete/{id}") // specific URI
	public ResponseEntity<?> deleteCustomerById(@PathVariable("id") String customerId) {// path variable - id is
		ResponseDto response = customerService.deleteCustomerById(customerId);
		logger.info("Generated response from Customer Service");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// REST API - SEARCH
	@GetMapping("/search/{input}")
	public ResponseEntity<?> searchCustomer(@PathVariable("input") String input) {
		ResponseDto response = customerService.searchCustomer(input);
		logger.info("Generated response from Customer Service");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}