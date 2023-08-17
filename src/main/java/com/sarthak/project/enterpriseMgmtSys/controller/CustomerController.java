package com.sarthak.project.enterpriseMgmtSys.controller;

//import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.sarthak.project.enterpriseMgmtSys.GenericClass.ResponseDto;
import com.sarthak.project.enterpriseMgmtSys.GenericClass.StatusResponse;
//import com.sarthak.project.enterpriseMgmtSys.payload.CardDetailsDTO;
import com.sarthak.project.enterpriseMgmtSys.payload.CustomerDetailsDTO;
//import com.sarthak.project.enterpriseMgmtSys.repository.CustomerRepository;
import com.sarthak.project.enterpriseMgmtSys.service.CustomerService;

// using @RestController to avoid using @ResponseBody annotation
@RestController
@RequestMapping
public class CustomerController {
	private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

	@Autowired
	private CustomerService customerService;
//	@Autowired
//	private CustomerRepository customerRepository;
	
	//POSTMAN / API CLIENTS
	
	// REST API - CREATE
	@PostMapping("/api/customers/create")
	public ResponseEntity<?> newCustomer(@RequestBody CustomerDetailsDTO customerDetailsDTO) {
		ResponseDto response = customerService.newCustomer(customerDetailsDTO);
		logger.info("Generated response from Customer Service");
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}
	

	// REST API - GET ALL
	@GetMapping("/api/customers/getall")
	public ResponseEntity<?> getAllCustomers() { // no parameters, list of Customer objects is returned
		ResponseDto response = customerService.getAllCustomers();
		logger.info("Generated response from Customer Service");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// REST API - GET BY ID
	@GetMapping("/api/customers/get/{id}") // specific URI; full URI is http://localhost:8080/api/customers/{id}
	public ResponseEntity<?> getCustomerById(@PathVariable("id") String customerId) {// path variable - id is passed and
		ResponseDto response = customerService.getCustomerById(customerId);
		logger.info("Generated response from Customer Service");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// REST API - UPDATE BY ID
	@PatchMapping("/api/customers/update/{id}")
	public ResponseEntity<?> updateCustomerById(@RequestBody CustomerDetailsDTO customerDetailsDTO,
			@PathVariable("id") String customerId) {
		ResponseDto response = customerService.updateCustomerById(customerDetailsDTO, customerId);
		logger.info("Generated response from Customer Service");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	// REST API - ACTIVATE
	@PatchMapping("/api/customers/activate/{id}")
	public ResponseEntity<?> activateCustomer(@PathVariable("id") String customerId) {
		ResponseDto response = customerService.activateCustomer(customerId);
		logger.info("Generated response from Customer Service");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// REST API - DELETE BY ID
	@DeleteMapping("/api/customers/delete/{id}") // specific URI
	public ResponseEntity<?> deleteCustomerById(@PathVariable("id") String customerId) {// path variable - id is
		ResponseDto response = customerService.deleteCustomerById(customerId);
		logger.info("Generated response from Customer Service");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// REST API - SEARCH
	@GetMapping("/api/customers/search/{input}")
	public ResponseEntity<?> searchCustomer(@PathVariable("input") String input) {
		ResponseDto response = customerService.searchCustomer(input);
		logger.info("Generated response from Customer Service");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	//THYMLEAF
	
	//REDIRECTS
	@GetMapping("/customers/getall")
	public ModelAndView redirectGetall(HttpServletRequest request, HttpSession session, Model model) {
	    if (request.getHeader("Accept") != null && request.getHeader("Accept").contains("text/html")) {
	        return new ModelAndView("forward:/customers/getall/page/1/customerId/asc");
	    } else {
	        return new ModelAndView("forward:/api/customers/getall");
	    }
	}
	
	@GetMapping("/cardsOfCustomer")
	public ModelAndView viewCustomerCards(@RequestParam("customerId") String customerId, HttpSession session) {	    
	    logger.info("at @Get /cardsOfCustomer");
	        // Return the Thymeleaf template for the browser request
        return new ModelAndView("forward:/customers/"+customerId+"/getall/page/1/cardId/asc");
	    
	}
	
	//FORWARDS TO
	@GetMapping("/customers/getall/page/{pageNo}/{field}/{dir}")
	public ModelAndView findPaginated(@PathVariable(value = "pageNo") int pageNo,
									@PathVariable(value = "field") String sortField,
									@PathVariable(value = "dir") String direction,
								Model model, HttpSession session) {		
		ModelAndView modelAndView = new ModelAndView("getallPage");
		modelAndView.addObject("totalPages", customerService.getPages(StatusResponse.CUSTOMER_REQUEST,null));
		modelAndView.addObject("currentPage", pageNo);
		modelAndView.addObject("totalItems", customerService.findPaginatedCustomers(pageNo,StatusResponse.PAGE_SIZE,
				sortField, direction).getTotalElements());
		modelAndView.addObject("listCustomers",customerService.findPaginatedCustomers(pageNo,StatusResponse.PAGE_SIZE,
				sortField, direction).getContent());
		modelAndView.addObject("emptyCells", customerService.getEmptyCells(StatusResponse.CUSTOMER_REQUEST,null));
		
		modelAndView.addObject("sortField", sortField);
		modelAndView.addObject("direction", direction);
		modelAndView.addObject("reverseSortDirection", (direction.equalsIgnoreCase("asc"))?"desc":"asc");
		session.setAttribute("fromCustomer", false);
	    return modelAndView;
	}
	
	@GetMapping("/customers/{customerId}/getall/page/{pageNo}/{sortField}/{direction}")
	public ModelAndView findPaginatedCards(@PathVariable(value = "customerId") String customerId,
									@PathVariable(value = "pageNo") int pageNo,
									@PathVariable(value = "sortField") String sortField,
									@PathVariable(value = "direction") String direction,
								Model model, HttpSession session) {
		ModelAndView modelAndView = new ModelAndView("cardsOfCustomer");
		modelAndView.addObject("totalPages", customerService.getPages(StatusResponse.CARD_REQUEST,customerId));
		modelAndView.addObject("currentPage", pageNo);
		modelAndView.addObject("totalItems", customerService.findPaginatedCards(customerId,pageNo,StatusResponse.PAGE_SIZE,
				sortField, direction).getTotalElements());
		modelAndView.addObject("cards",customerService.findPaginatedCards(customerId,pageNo,StatusResponse.PAGE_SIZE,
				sortField, direction).getContent());
		modelAndView.addObject("emptyCells", customerService.getEmptyCells(StatusResponse.CARD_REQUEST,customerId));
		
		modelAndView.addObject("sortField", sortField);
		modelAndView.addObject("direction", direction);
		modelAndView.addObject("reverseSortDirection", (direction.equalsIgnoreCase("asc"))?"desc":"asc");
		if((boolean)session.getAttribute("fromCustomer")==true) {
			logger.info("fromCustomer Got from session, now setting in model");
			modelAndView.addObject("fromCustomer", true);
		} else {
			logger.info("fromUser Got from session, now setting in model");
			modelAndView.addObject("fromUser", true);
		}
		
		return modelAndView;
	}
	
}