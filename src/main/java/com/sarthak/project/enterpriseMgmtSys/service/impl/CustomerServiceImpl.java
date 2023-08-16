package com.sarthak.project.enterpriseMgmtSys.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sarthak.project.enterpriseMgmtSys.GenericClass.IdGeneratorCustomer;
import com.sarthak.project.enterpriseMgmtSys.GenericClass.RegularExpressions;
import com.sarthak.project.enterpriseMgmtSys.GenericClass.ResponseDto;
import com.sarthak.project.enterpriseMgmtSys.GenericClass.StatusResponse;
import com.sarthak.project.enterpriseMgmtSys.entity.CardDetails;
import com.sarthak.project.enterpriseMgmtSys.entity.CustomerDetails;
import com.sarthak.project.enterpriseMgmtSys.payload.CardDetailsDTO;
import com.sarthak.project.enterpriseMgmtSys.payload.CustomerDetailsDTO;
import com.sarthak.project.enterpriseMgmtSys.repository.CardRepository;
import com.sarthak.project.enterpriseMgmtSys.repository.CustomerRepository;
import com.sarthak.project.enterpriseMgmtSys.service.CustomerService;
import java.lang.reflect.Field;


@Service
public class CustomerServiceImpl implements CustomerService {
	// Inject customer repository dependency
	@Autowired
	public CustomerRepository customerRepository;
	@Autowired
	public CardRepository cardRepository;
	@Autowired
	public IdGeneratorCustomer idGen;
	@Autowired
	public ModelMapper mapper;

	private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);	
	
	// CREATE
	@Override
	public ResponseDto newCustomer(CustomerDetailsDTO customerDetailsDTO) {
		CustomerDetails customer = new CustomerDetails();
		ResponseDto response = new ResponseDto();
		IdGeneratorCustomer idGen = new IdGeneratorCustomer(customerRepository);
		try {
			if(!validRequest(customerDetailsDTO, StatusResponse.CREATE_REQUEST)) {
				logger.error("Bad Request Error - SJ");
				response.setStatusCode(StatusResponse.BAD_REQUEST_STATUS_CODE);
				response.setMessage(StatusResponse.BAD_REQUEST);
				return response;
			}
			
			response = validateNumberOnCreation(customerDetailsDTO.getCustomerMobile());
			if (response.getStatusCode().equalsIgnoreCase(StatusResponse.SUCCESS_STATUS_CODE)) {
				logger.info("Mobile Number is valid on creation. Proceeding to check name");
				response = validateCustomerName(customerDetailsDTO.getCustomerName(), StatusResponse.CREATE_REQUEST);
				if(response.getStatusCode().equalsIgnoreCase(StatusResponse.SUCCESS_STATUS_CODE)) {
					customer.setCustomerId(idGen.generateCustomerId());
					customer.setCustomerName(customerDetailsDTO.getCustomerName());
					customer.setCustomerMobile(customerDetailsDTO.getCustomerMobile());
					customer.setEnterpriseId(customerDetailsDTO.getEnterpriseId());
					customer.setStatus(StatusResponse.ACTIVE_STATUS);
					
					customerRepository.save(customer);
					
					customerDetailsDTO.setCustomerId(customer.getCustomerId());
					customerDetailsDTO.setStatus(customer.getStatus());
		
					response.setStatusCode(StatusResponse.CREATED_STATUS_CODE);
					response.setMessage(StatusResponse.CUSTOMER_CREATED);
					response.setResult(customerDetailsDTO);
					
					return response;
				}
				return response;
			} else {
				return response; // returns null object - resource not found exception
			}
		} catch(Exception e) {
			logger.error(e.getMessage(),e);
			response.setStatusCode(StatusResponse.SERVER_ERROR_STATUS_CODE);
			response.setMessage(StatusResponse.BAD_REQUEST);
			return response;
		}
	}
	
	

	// GET ALL
	@Override
	public ResponseDto getAllCustomers() {
		ResponseDto response = new ResponseDto();
		try {
			if(!validRequest(null, StatusResponse.GETALL_REQUEST)) {
				response.setStatusCode(StatusResponse.BAD_REQUEST_STATUS_CODE);
				response.setMessage(StatusResponse.BAD_REQUEST);
			}
		List<CustomerDetails> customers = customerRepository.findAll();
		if (customers == null || customers.size() == 0) {
			//
			response.setStatusCode(StatusResponse.FAILURE_STATUS_CODE);
			response.setMessage(StatusResponse.NO_CUSTOMERS_IN_DB);
			return response;
		}
		List<CustomerDetailsDTO> allCustomers = customers.stream()
				.map(customer -> mapper.map(customer, CustomerDetailsDTO.class)).collect(Collectors.toList());
		List<CardDetailsDTO> allCards = cardRepository.findAll().stream()
				.map(card -> mapper.map(card, CardDetailsDTO.class)).collect(Collectors.toList());

		for (CustomerDetailsDTO customer : allCustomers) {
			List<CardDetailsDTO> cardsOfCustomer = new ArrayList<CardDetailsDTO>();
			if(allCards.isEmpty() || allCards.size() == 0 || allCards == null) {
				logger.info("No cards in db yet");
			}
			else {
				logger.info(String.format("CardSize: %s : T", allCards.size()));
				for (CardDetailsDTO card : allCards) {
					if (card.getCustomerId().equalsIgnoreCase(customer.getCustomerId())) {
						cardsOfCustomer.add(card);
						// allCards.remove(card);
					}
				}
			}
			customer.setAllCards(cardsOfCustomer);
		}

		response.setStatusCode(StatusResponse.SUCCESS_STATUS_CODE);
		response.setMessage(StatusResponse.FETCHED_ALL_CUSTOMERS);
		response.setResult(allCustomers);
		return response;
		} catch(Exception e) {
			logger.error(e.getMessage(),e);
			response.setStatusCode(StatusResponse.SERVER_ERROR_STATUS_CODE);
			response.setMessage(StatusResponse.BAD_REQUEST);
			return response;
		}
	}

	// GET BY ID
	@Override
	public ResponseDto getCustomerById(String customerId) {
		ResponseDto response = new ResponseDto();
		CustomerDetails customer = customerRepository.findById(customerId).orElse(null);
		try {
		if (customer == null) {
			response.setStatusCode(StatusResponse.FAILURE_STATUS_CODE);
			response.setMessage(StatusResponse.CUSTOMER_DOES_NOT_EXIST);
			return response;
		}
		CustomerDetailsDTO customerResponse = mapper.map(customer, CustomerDetailsDTO.class);

		List<CardDetailsDTO> allCards = cardRepository.findAll().stream()
				.map(card -> mapper.map(card, CardDetailsDTO.class)).collect(Collectors.toList());
		List<CardDetailsDTO> cardList = new ArrayList<CardDetailsDTO>();
		for (CardDetailsDTO itr : allCards) {
			if (itr.getCustomerId().equalsIgnoreCase(customerId)) {
				cardList.add(itr);
			}
		}

		customerResponse.setAllCards(cardList);

		response.setStatusCode(StatusResponse.SUCCESS_STATUS_CODE);
		response.setMessage(StatusResponse.FETCHED_INDIVIDUAL_CUSTOMER);
		response.setResult(customerResponse);
		return response;
		} catch(Exception e) {
			logger.error(e.getMessage(),e);
			response.setStatusCode(StatusResponse.SERVER_ERROR_STATUS_CODE);
			response.setMessage(StatusResponse.BAD_REQUEST);
			return response;
		}
	}

	
	// UPDATE BY ID
	@Override
	public ResponseDto updateCustomerById(CustomerDetailsDTO customerDto, String customerId) {
		ResponseDto response = new ResponseDto();
		try {
		if(!validRequest(customerDto, StatusResponse.UPDATE_REQUEST)) {
			response.setStatusCode(StatusResponse.BAD_REQUEST_STATUS_CODE);
			response.setMessage(StatusResponse.BAD_REQUEST);
			return response;
		}
		CustomerDetails existingCustomer = customerRepository.findById(customerId).orElse(null);		
		if (existingCustomer == null) {
			response.setStatusCode(StatusResponse.FAILURE_STATUS_CODE);
			response.setMessage(StatusResponse.CUSTOMER_DOES_NOT_EXIST);
			return response;
		}
		if(existingCustomer.getStatus().equalsIgnoreCase(StatusResponse.DEACTIVE_STATUS)) {
			response.setStatusCode(StatusResponse.FAILURE_STATUS_CODE);
			response.setMessage(StatusResponse.BLOCKED_CUSTOMER);
			return response;
		}
		
		
		CustomerDetailsDTO customerDtoFinal = new CustomerDetailsDTO();
		customerDtoFinal.setCustomerId(existingCustomer.getCustomerId());
		if (customerDto.getCustomerName() != null) {
			existingCustomer.setCustomerName(customerDto.getCustomerName());
			customerDtoFinal.setCustomerName(existingCustomer.getCustomerName());
		}
		if (customerDto.getEnterpriseId() != null) {
			existingCustomer.setEnterpriseId(customerDto.getEnterpriseId());
			customerDtoFinal.setEnterpriseId(existingCustomer.getEnterpriseId());
		}
		if (customerDto.getCustomerMobile() != null) {
			response = validateNumberOnUpdate(customerDto);
			if (response.getStatusCode().equalsIgnoreCase(StatusResponse.SUCCESS_STATUS_CODE)) {
				existingCustomer.setCustomerMobile(customerDto.getCustomerMobile());
				customerDtoFinal.setCustomerMobile(existingCustomer.getCustomerMobile());
			}
		}
		customerRepository.save(existingCustomer);

		customerDtoFinal.setCustomerName(existingCustomer.getCustomerName());
		customerDtoFinal.setEnterpriseId(existingCustomer.getEnterpriseId());
		customerDtoFinal.setCustomerMobile(existingCustomer.getCustomerMobile());
		customerDtoFinal.setCardCount(existingCustomer.getCardCount());

		List<CardDetailsDTO> allCards = cardRepository.findAll().stream()
				.map(card -> mapper.map(card, CardDetailsDTO.class)).collect(Collectors.toList());
		// Log.info("all cards size is "+allCards.size());

		List<CardDetailsDTO> cardList = new ArrayList<CardDetailsDTO>();
		for (CardDetailsDTO itr : allCards) {
			if (itr.getCustomerId().equalsIgnoreCase(customerId)) {
				cardList.add(itr);
			}
		}
		customerDtoFinal.setAllCards(cardList);

		response.setStatusCode(StatusResponse.SUCCESS_STATUS_CODE);
		response.setMessage(StatusResponse.CUSTOMER_UPDATED);
		response.setResult(customerDtoFinal);
		return response;
		} catch(Exception e) {
			logger.error(e.getMessage(),e);
			response.setStatusCode(StatusResponse.SERVER_ERROR_STATUS_CODE);
			response.setMessage(StatusResponse.BAD_REQUEST);
			return response;
		}
	}

	//ACTIVATE
	@Override
	public ResponseDto activateCustomer(String customerId) {
		ResponseDto response = new ResponseDto();
		CustomerDetails customer = customerRepository.findById(customerId).orElse(null);
		try {
			//check if id is valid, check if customer deactive, activate
			if (customer == null) {
				response.setStatusCode(StatusResponse.FAILURE_STATUS_CODE);
				response.setMessage(StatusResponse.CUSTOMER_DOES_NOT_EXIST);
				return response;
			}
			CustomerDetailsDTO customerResponse = mapper.map(customer, CustomerDetailsDTO.class);
			if(customerResponse.getStatus().equalsIgnoreCase(StatusResponse.ACTIVE_STATUS)) {
				//customer is already active
				response.setStatusCode(StatusResponse.FAILURE_STATUS_CODE);
				response.setMessage(StatusResponse.CUSTOMER_ALREADY_ACTIVE);
				return response;
			}
			//activate
			customer.setStatus(StatusResponse.ACTIVE_STATUS);
			customerRepository.save(customer);
			
			List<CardDetailsDTO> allCards = cardRepository.findAll().stream()
					.map(card -> mapper.map(card, CardDetailsDTO.class)).collect(Collectors.toList());
			List<CardDetailsDTO> cardList = new ArrayList<CardDetailsDTO>();
			for (CardDetailsDTO itr : allCards) {
				if (itr.getCustomerId().equalsIgnoreCase(customerId)) {
					cardList.add(itr);
				}
			}
			customerResponse.setAllCards(cardList);				
			customerResponse.setStatus(StatusResponse.ACTIVE_STATUS);
			response.setStatusCode(StatusResponse.SUCCESS_STATUS_CODE);
			response.setMessage(StatusResponse.CUSTOMER_ACTIVATED);
			response.setResult(customerResponse);
			return response;
			
			
		}catch(Exception e) {
			logger.error(e.getMessage(),e);
			response.setStatusCode(StatusResponse.SERVER_ERROR_STATUS_CODE);
			response.setMessage(StatusResponse.BAD_REQUEST);
			return response;
		}
	}
	
	
	// DELETE BY ID
	@Override
	public ResponseDto deleteCustomerById(String customerId) { //soft - delete
		ResponseDto response = new ResponseDto();
		CustomerDetails customer = customerRepository.findById(customerId).orElse(null);
		try {
			if (customer != null) {
				if(customer.getStatus().equalsIgnoreCase(StatusResponse.DEACTIVE_STATUS)) {		
					response.setStatusCode(StatusResponse.FAILURE_STATUS_CODE);
					response.setMessage(StatusResponse.CUSTOMER_ALREADY_DEACTIVE);
					return response;
				}
				List<CardDetails> customerCards = customer.getListCards();
				for(CardDetails card : customerCards) {
					//cardRepository.delete(card); //delete all cards of customer
					card.setStatus(StatusResponse.DEACTIVE_STATUS);
					cardRepository.save(card);
				}
				//customerRepository.deleteById(customerId);
				customer.setStatus(StatusResponse.DEACTIVE_STATUS);
				customerRepository.save(customer);
				
			} else { // invalid customer id
				response.setStatusCode(StatusResponse.FAILURE_STATUS_CODE);
				response.setMessage(StatusResponse.CUSTOMER_DOES_NOT_EXIST);
				return response;
			}
	
			response.setStatusCode(StatusResponse.SUCCESS_STATUS_CODE);
			response.setMessage(StatusResponse.CUSTOMER_DEACTIVATED);
			return response;
		} catch(Exception e) {
			logger.error(e.getMessage(),e);
			response.setStatusCode(StatusResponse.SERVER_ERROR_STATUS_CODE);
			response.setMessage(StatusResponse.BAD_REQUEST);
			return response;
		}
	}

	@Override
	public ResponseDto searchCustomer(String input) {
		ResponseDto response = new ResponseDto();
		try {
		//input can either by customerId, customerMobile, customerName
		//with customerName, multiple customers must be shown
		String inputType = checkInputType(input);
		if(inputType.equalsIgnoreCase("mobile")) {
			response = searchWithMobile(input);
			logger.info("search mobile complete");
			return response;
		}
		else if(inputType.equalsIgnoreCase("id")) {
			response = getCustomerById(input);
			logger.info("searchId complete");
			return response;
		}
		else if(inputType.equalsIgnoreCase("enterpriseId")) {
			response = searchWithEnterpriseId(input);
			return response;
		}
		else if(inputType.equalsIgnoreCase("name")){
			response = searchWithName(input);
			return response;
		}
		
		//invalid --> not a valid parameter, search by either name, id, or mobile
		response.setStatusCode(StatusResponse.BAD_REQUEST_STATUS_CODE);
		response.setMessage(StatusResponse.BAD_REQUEST);
		return response;
		} catch(Exception e) {
			logger.error(e.getMessage(),e);
			response.setStatusCode(StatusResponse.SERVER_ERROR_STATUS_CODE);
			response.setMessage(StatusResponse.BAD_REQUEST);
			return response;
		}
	}
	
	public String checkInputType(String input) {
		try {
			if(input.matches(RegularExpressions.MOBILE_NUMBER_FORMAT)) {
				logger.info("Parameter matches mobile");
				return "mobile";
			}
			else if(input.matches(RegularExpressions.CUSTOMER_ID_FORMAT))
				return "id";
			else if(input.matches(RegularExpressions.ENTERPRISE_ID_FORMAT))
				return "enterpriseId";
			else if(input.matches(RegularExpressions.CUSTOMER_NAME_FORMAT))
				return "name";		
			return "name";
		}catch(Exception e) {
				logger.error(e.getMessage(),e);
				return null;
		}
	}
	
	public ResponseDto searchWithMobile(String input) {
		ResponseDto response = new ResponseDto();
		try {
			List<CustomerDetails> customer = customerRepository.findByCustomerMobile(input);
			logger.info("Gone to Repo");
			if(customer.isEmpty()) {
				logger.info("No such mobile and cust");
				response.setStatusCode(StatusResponse.FAILURE_STATUS_CODE);
				response.setMessage(StatusResponse.CUSTOMER_DOES_NOT_EXIST);
				return response;
			}
			CustomerDetailsDTO customerDto = mapper.map(customer.get(0), CustomerDetailsDTO.class);
			response.setStatusCode(StatusResponse.SUCCESS_STATUS_CODE);
			response.setMessage(StatusResponse.FETCHED_INDIVIDUAL_CUSTOMER);
			response.setResult(customerDto);
			return response;
		} catch(Exception e) {
			logger.error(e.getMessage(),e);
			response.setStatusCode(StatusResponse.SERVER_ERROR_STATUS_CODE);
			response.setMessage(StatusResponse.BAD_REQUEST);
			return response;
		}
	}
	
	public ResponseDto searchWithEnterpriseId(String input) {
		ResponseDto response = new ResponseDto();
		try {
			List<CustomerDetails> customerDetails = customerRepository.findByEnterpriseId(input);
			if(customerDetails.isEmpty()) {
				logger.info("No such entpId and cust");
				response.setStatusCode(StatusResponse.FAILURE_STATUS_CODE);
				response.setMessage(StatusResponse.CUSTOMER_DOES_NOT_EXIST);
				return response;
			}
			List<CustomerDetailsDTO> customerDto = customerDetails.stream().map(customer -> mapper.map(customer, CustomerDetailsDTO.class)).collect(Collectors.toList());
			response.setStatusCode(StatusResponse.SUCCESS_STATUS_CODE);
			response.setMessage(StatusResponse.FETCHED_ALL_CUSTOMERS);
			response.setResult(customerDto);
			return response;
		} catch(Exception e) {
			logger.error(e.getMessage(),e);
			response.setStatusCode(StatusResponse.SERVER_ERROR_STATUS_CODE);
			response.setMessage(StatusResponse.BAD_REQUEST);
			return response;
		}
	}
	
	public ResponseDto searchWithName(String input) {
		ResponseDto response = new ResponseDto();
		try {
			List<CustomerDetails> customerDetails = customerRepository.findByCustomerName(input);
			if(customerDetails.isEmpty()) {
				logger.info("No such Name and cust");
				response.setStatusCode(StatusResponse.FAILURE_STATUS_CODE);
				response.setMessage(StatusResponse.CUSTOMER_DOES_NOT_EXIST);
				return response;
			}
			List<CustomerDetailsDTO> customerDto = customerDetails.stream().map(customer -> mapper.map(customer, CustomerDetailsDTO.class)).collect(Collectors.toList());
			response.setStatusCode(StatusResponse.SUCCESS_STATUS_CODE);
			response.setMessage(StatusResponse.FETCHED_ALL_CUSTOMERS);
			response.setResult(customerDto);
			return response;
		} catch(Exception e) {
			logger.error(e.getMessage(),e);
			response.setStatusCode(StatusResponse.SERVER_ERROR_STATUS_CODE);
			response.setMessage(StatusResponse.BAD_REQUEST);
			return response;
		}
	}
	
	// MOIBLE REGEX FORMAT CHECK
	public boolean validFormatOfMobile(String mobile) {
		try {
			return mobile.matches(RegularExpressions.MOBILE_NUMBER_FORMAT); // if mobile parameter matches the regex, true boolean is returned
		} catch(Exception e) {
			logger.error(e.getMessage(),e);
			return false;
		}
	}

	// VALIDATE MOBILE ON CREATION
	public ResponseDto validateNumberOnCreation(String number) {
		ResponseDto statusResponse = new ResponseDto();
		try {
			if (!validFormatOfMobile(number)) {
				statusResponse.setStatusCode(StatusResponse.FAILURE_STATUS_CODE);
				statusResponse.setMessage(StatusResponse.MOBILE_INVALID_FORMAT);
				return statusResponse;
			}
			// check for already existing mobileNumber
			else if (customerRepository.existsByMobileNumber(number)) {
				statusResponse.setStatusCode(StatusResponse.FAILURE_STATUS_CODE);
				statusResponse.setMessage(StatusResponse.MOBILE_ALREADY_EXISTS);
				return statusResponse;
			}
			// the mobile is unique and can be updated
			else {
				statusResponse.setStatusCode(StatusResponse.SUCCESS_STATUS_CODE);
				return statusResponse;
			}
		} catch(Exception e) {
			logger.error(e.getMessage(),e);
			statusResponse.setStatusCode(StatusResponse.SERVER_ERROR_STATUS_CODE);
			statusResponse.setMessage(StatusResponse.BAD_REQUEST);
			return statusResponse;
		}
	}

	// VALIDATE MOBILE ON UPDATE
	public ResponseDto validateNumberOnUpdate(CustomerDetailsDTO customer) {
		ResponseDto statusResponse = new ResponseDto();
		try {
			if (customer.getCustomerMobile() == null) {
			}
			// if mobile number entered is of incorrect format
			else if (!validFormatOfMobile(customer.getCustomerMobile())) {
				statusResponse.setStatusCode(StatusResponse.FAILURE_STATUS_CODE);
				statusResponse.setMessage(StatusResponse.MOBILE_ALREADY_EXISTS);
				return statusResponse;
			}
			// Condition 1: Mobile number entered already exists in database
			// Condition 2: This no.'s associated Id is not the same as the Id entered in
			// request body(Entered mob. no. is not of the customer that is being updated)
			// if both conditions are true, an exception is thrown
			else if (customerRepository.existsByMobileNumber(customer.getCustomerMobile()) && !customerRepository
					.checkIdWithNumber(customer.getCustomerMobile()).equalsIgnoreCase(customer.getCustomerId())
					&& customer.getCustomerId() != null) {
				statusResponse.setStatusCode(StatusResponse.FAILURE_STATUS_CODE);
				statusResponse.setMessage("Mobile Number already exists");
				return statusResponse;
			}
			statusResponse.setStatusCode(StatusResponse.SUCCESS_STATUS_CODE);
			return statusResponse;
		} catch(Exception e) {
			logger.error(e.getMessage(),e);
			statusResponse.setStatusCode(StatusResponse.SERVER_ERROR_STATUS_CODE);
			statusResponse.setMessage(StatusResponse.BAD_REQUEST);
			return statusResponse;
		}
	}
	
	//VALIDATE NAME ON UPDATE/CREATE
	public ResponseDto validateCustomerName(String name, int requestCase) {
		ResponseDto response = new ResponseDto();
		try {
			// name can be null, contain spaces or numbers, or be valid
			// NOTE: no use case of requestCase as of now
			if(name.matches(RegularExpressions.CUSTOMER_NAME_FORMAT)) {
				logger.info("matched in validate function - name");
				response.setStatusCode(StatusResponse.SUCCESS_STATUS_CODE);
			}
			else {
				logger.info("did not match in validate function - name");
				response.setStatusCode(StatusResponse.FAILURE_STATUS_CODE);
				response.setMessage(StatusResponse.NAME_INVALID_FORMAT);
			}
			return response;
		} catch(Exception e) {
			logger.error(e.getMessage(),e);
			response.setStatusCode(StatusResponse.SERVER_ERROR_STATUS_CODE);
			response.setMessage(StatusResponse.BAD_REQUEST);
			return response;
		}
	}
	
	// CHECKS FOR BAD REQUESTS
	public boolean validRequest(CustomerDetailsDTO customerDto, int requestCase) {
		try {
			switch (requestCase) {
			//create - paramters: name, mobile, enterprise
			case 1:
				//not null parameters
				if(customerDto.getCustomerMobile() != null &&
					customerDto.getCustomerName() != null &&
					customerDto.getEnterpriseId() != null) { // null parametrs
					if(customerDto.getAllCards() == null &&
						customerDto.getStatus() == null &&
						customerDto.getCustomerId() == null)
						return true;
				}
				return false;
			//getall - parameters: none
			case 2:
				if(customerDto == null)
					return true;
				return false;
			//getbyid - paramters: (id)
			case 3:
				if(customerDto == null)
					return true;
				return false;
			//update - parameters: (id), optionally- name, mobile, enterprise, request can't be empty
			case 4:
				if(customerDto.getCustomerMobile() != null ||
					customerDto.getCustomerName() != null ||
					customerDto.getEnterpriseId() != null) { // null parameters
					if(customerDto.getAllCards() == null &&
						customerDto.getStatus() == null &&
						customerDto.getCustomerId() == null)
						return true;
				}
				return false;
			//delete - parameters: (id)
			case 5:
				if(customerDto == null)
					return true;
				return false;
			default:
				break;
			}
			return false;
		} catch(Exception e) {
			logger.error(e.getMessage(),e);
			return false;
		}
	}
	
	@Override
	public Page<CustomerDetailsDTO> findPaginatedCustomers(int pageNo, int pageSize, String sortField,
													String direction){
		try {
			Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
						Sort.by(sortField).ascending() :
						Sort.by(sortField).descending();
			Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
			Page<CustomerDetails> customerPage = customerRepository.findAll(pageable);
	        return customerPage.map(customer -> mapper.map(customer, CustomerDetailsDTO.class));
		} catch(Exception e) {
			logger.info(e.getMessage(),e);
			return null;
		}
	}
	
	@Override
	public int getPages(int requestCase, String customerId) {
		try {
			int pageSize, noOfPages;
			double count, floatPages;
			switch (requestCase) {
				case 100: // customer request
					pageSize = StatusResponse.PAGE_SIZE; // 5
					count = customerRepository.count(); // assuming count returns a double or float value
					floatPages = Math.ceil(count / pageSize); // 18.0 / 5 = 3.6 --> 4.0
					noOfPages = (int) floatPages; // 4
					return noOfPages;
				case 200: //card request
					pageSize = StatusResponse.PAGE_SIZE;
	//				count is the number of cards that the customer has
					count = customerRepository.findById(customerId).orElse(null).getCardCount();					
					floatPages = Math.ceil(count / pageSize); // 18.0 / 5 = 3.6 --> 4.0
					noOfPages = (int) floatPages; // 4
					return (noOfPages==0)?1:noOfPages;
				default:
					return -1;
			}
		
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return -1;
		}
	}
	
	@Override
	public int getEmptyCells(int requestCase, String customerId) {
		try {
			int filledCells;
			switch (requestCase) {
			case 100: //customer request
				filledCells = (int) (customerRepository.count()%StatusResponse.PAGE_SIZE);//20%5=0
				return (filledCells!=0) ? StatusResponse.PAGE_SIZE-filledCells : 0;
			case 200: //card request 
				filledCells = (int) (customerRepository.findById(customerId).orElse(null).getCardCount()%StatusResponse.PAGE_SIZE);//20%5=0
				if(filledCells==0 && customerRepository.findById(customerId).orElse(null).getCardCount() == 0)
					return 5;
				return (filledCells!=0) ? StatusResponse.PAGE_SIZE-filledCells : 0;				
			default:
				return -1;
			}
		
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return -1;
		}
	}
	
	@Override
	public Page<CardDetailsDTO> findPaginatedCards(String customerId, int pageNo, 
		    int pageSize, String sortField, String direction) {
		    try {
		        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
	                Sort.by(sortField).ascending() :
	                Sort.by(sortField).descending();
		        logger.info(sort.toString());
		        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);

		        CustomerDetails customer = customerRepository.findById(customerId).orElse(null);
		        List<CardDetails> cards = customer.getListCards();
		        List<CardDetailsDTO> cardDtos = cards.stream()
		                .map(card -> mapper.map(card, CardDetailsDTO.class))
		                .collect(Collectors.toList());

		        
		     // Get the field from CardDetailsDTO based on sortField using Reflection
		        Field field = CardDetailsDTO.class.getDeclaredField(sortField);
		        field.setAccessible(true);

		        // Sort the cardDtos list based on the specified sorting order and field
		        cardDtos.sort((card1, card2) -> {
		            try {
		                Comparable value1 = (Comparable) field.get(card1);
		                Comparable value2 = (Comparable) field.get(card2);
		                int comparison = value1.compareTo(value2);
		                return direction.equalsIgnoreCase("asc") ? comparison : -comparison;
		            } catch (Exception e) {
		                // Handle exception if the field does not exist or is inaccessible
		            	logger.info("field does not exist or is inaccessible");
		                e.printStackTrace();
		                return 0;
		            }
		        });
		        
		        // Calculate the starting index and the ending index of the current page
		        int start = (int) pageable.getOffset();
		        int end = Math.min((start + pageable.getPageSize()), cardDtos.size());
       
		        
		     // Create a sublist of cardDtos for the current page
		        List<CardDetailsDTO> pageContent = cardDtos.subList(start, end);
		        
		        // Create a PageImpl from the sublist and the pageable object
		        return new PageImpl<>(pageContent, pageable, cardDtos.size());
		    } catch (Exception e) {
		        logger.info(e.getMessage(), e);
		        return null;
		    }
		}

	
	
	
} // end class