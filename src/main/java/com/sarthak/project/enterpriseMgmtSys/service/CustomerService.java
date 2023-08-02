package com.sarthak.project.enterpriseMgmtSys.service;

//import java.util.List;

import org.springframework.data.domain.Page;

import com.sarthak.project.enterpriseMgmtSys.GenericClass.ResponseDto;
import com.sarthak.project.enterpriseMgmtSys.payload.CardDetailsDTO;
//import com.example.DemoProject.repository.CustomerRepository;
import com.sarthak.project.enterpriseMgmtSys.payload.CustomerDetailsDTO;

public interface CustomerService {	
	//creates new customer
	ResponseDto newCustomer(CustomerDetailsDTO customerDetailsDTO);
	
	//lists all customers
	ResponseDto getAllCustomers();
	
	//returns customer by passing id
	ResponseDto getCustomerById(String customerId);
	
	//updates the customer by passing customer - complete/incomplete
	ResponseDto updateCustomerById(CustomerDetailsDTO customerDto, String customerId);

	//activates customer
	ResponseDto activateCustomer(String customerId);
	
	//deletes customer by passing id
	ResponseDto deleteCustomerById(String customerId);

	ResponseDto searchCustomer(String input);
	
	//List<CustomerDetailsDTO> thymeleafGetAllCustomers();
	
	//List<CardDetailsDTO> getCardsOfCustomer(String customerId);
	
	public int getPages(int requestCase, String customerId);
	
	public int getEmptyCells(int requestCase, String customerId);
	
	Page<CustomerDetailsDTO> findPaginatedCustomers(int pageNo, int pageSize, String sortField, String direction);
	
	Page<CardDetailsDTO> findPaginatedCards(String customerId, int pageNo, int pageSize, String sortField, String direction);


}