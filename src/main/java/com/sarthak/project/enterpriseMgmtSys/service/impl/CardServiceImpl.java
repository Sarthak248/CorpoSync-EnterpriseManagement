package com.sarthak.project.enterpriseMgmtSys.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sarthak.project.enterpriseMgmtSys.GenericClass.IdGeneratorCard;
import com.sarthak.project.enterpriseMgmtSys.GenericClass.RegularExpressions;
import com.sarthak.project.enterpriseMgmtSys.GenericClass.ResponseDto;
import com.sarthak.project.enterpriseMgmtSys.GenericClass.StatusResponse;
import com.sarthak.project.enterpriseMgmtSys.entity.CardDetails;
import com.sarthak.project.enterpriseMgmtSys.entity.CustomerDetails;
import com.sarthak.project.enterpriseMgmtSys.payload.CardDetailsDTO;
import com.sarthak.project.enterpriseMgmtSys.repository.CardRepository;
import com.sarthak.project.enterpriseMgmtSys.repository.CustomerRepository;
import com.sarthak.project.enterpriseMgmtSys.service.CardService;

@Service
public class CardServiceImpl implements CardService{
	@Autowired
	public CardRepository cardRepository;
	@Autowired
	public CustomerRepository customerRepository;
	@Autowired
	public ModelMapper mapper;
	
	private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(CustomerServiceImpl.class);	
	
	//REST API - CREATE
	@Override
	public ResponseDto createCard(CardDetailsDTO cardDetailsDTO) {
		logger.info("Going to create card for customer");
		ResponseDto response = new ResponseDto();
		try {
		
		if(!validRequest(cardDetailsDTO, 1)) {
			response.setStatusCode(StatusResponse.BAD_REQUEST_STATUS_CODE);
			response.setMessage(StatusResponse.BAD_REQUEST);
			return response;
		}
		CustomerDetails customerDetails = customerRepository.findById(cardDetailsDTO.getCustomerId()).orElse(null);
		if(customerDetails == null) {
			response.setStatusCode(StatusResponse.FAILURE_STATUS_CODE);
			response.setMessage(StatusResponse.CUSTOMER_DOES_NOT_EXIST);
			return response;
		}
		//check if customer is deactivated
		if(customerDetails.getStatus().equalsIgnoreCase(StatusResponse.DEACTIVE_STATUS)) {
			response.setStatusCode(StatusResponse.FAILURE_STATUS_CODE);
			response.setMessage(StatusResponse.BLOCKED_CUSTOMER);
			return response;
		}
		response = validCard(cardDetailsDTO.getAliasName(), StatusResponse.CREATE_REQUEST);
		if(response.getStatusCode().equalsIgnoreCase(StatusResponse.SUCCESS_STATUS_CODE)) {
			IdGeneratorCard idGen = new IdGeneratorCard(cardRepository);
		
			CardDetails card = new CardDetails();
			card.setCardId(idGen.generateCardId());
			card.setAliasName(cardDetailsDTO.getAliasName());
			card.setCustomerId(cardDetailsDTO.getCustomerId());
			card.setStatus(StatusResponse.ACTIVE_STATUS);
			
			cardRepository.save(card);
			customerDetails.setCardCount(customerDetails.getCardCount()+1);
			customerRepository.save(customerDetails);
			response.setStatusCode(StatusResponse.CREATED_STATUS_CODE);
			response.setMessage(StatusResponse.CARD_CREATED);
			cardDetailsDTO.setCardId(card.getCardId());
			cardDetailsDTO.setStatus(card.getStatus());
			response.setResult(cardDetailsDTO);
			return response;
		}
		return response; // card name did not match
		}catch(Exception e) {
			logger.error(e.getMessage(),e);
			response.setStatusCode(StatusResponse.SERVER_ERROR_STATUS_CODE);
			response.setMessage(StatusResponse.SERVER_ERROR);
			return response;
		}		
	}

	//REST API - GETALL
	@Override
	public ResponseDto getAllCards() {
		ResponseDto response = new ResponseDto();
		try {
		List<CardDetails> cardDetails=cardRepository.findAll();
		if(cardDetails == null || cardDetails.size() == 0) {
			response.setStatusCode(StatusResponse.FAILURE_STATUS_CODE);
			response.setMessage(StatusResponse.NO_CARDS_IN_DB);
			return response;
		}
		response.setStatusCode(StatusResponse.SUCCESS_STATUS_CODE);
		response.setMessage(StatusResponse.FETCHED_ALL_CARDS);
		response.setResult(cardDetails.stream().map(card -> mapper.map(card, CardDetailsDTO.class)).collect(Collectors.toList()));
		return response;
		}catch(Exception e) {
			logger.error(e.getMessage(),e);
			response.setStatusCode(StatusResponse.SERVER_ERROR_STATUS_CODE);
			response.setMessage(StatusResponse.SERVER_ERROR);
			return response;			
		}
	}
	
	//REST API - GETBYID
	@Override
	public ResponseDto getCardDetails(String cardId) {
		CardDetails cardDetails = cardRepository.findById(cardId).orElse(null);	
		ResponseDto response = validCardId(cardId);
		try {			
		if(response!=null && response.getStatusCode().equalsIgnoreCase(StatusResponse.SUCCESS_STATUS_CODE)) {
			CardDetailsDTO cardResponse = mapper.map(cardDetails, CardDetailsDTO.class);
			response.setStatusCode(StatusResponse.SUCCESS_STATUS_CODE);
			response.setMessage(StatusResponse.FETCHED_INDIVIDUAL_CARD);
			response.setResult(cardResponse);
		}
		return response;
		} catch(Exception e) {
			logger.error(e.getMessage(),e);
			response.setStatusCode(StatusResponse.SERVER_ERROR_STATUS_CODE);
			response.setMessage(StatusResponse.SERVER_ERROR);
			return response;
		}
	}

	//REST API - UPDATE
	@Override
	public ResponseDto updateCard(CardDetailsDTO cardDetailsDTO, String cardId) {
		//new card	   - 
		//old card	   - cardRepository.getById(cardId).orElse(null)
		//new customer - customerRepository.getById(cardDetailsDTO.getCustomerId()).orElse(null)
		//old customer - customerRepository.getById(oldCard.getCustomerId()).orElse(null)
		ResponseDto response = new ResponseDto();
		try {
		if(!validRequest(cardDetailsDTO, 4)) {
			response.setStatusCode(StatusResponse.BAD_REQUEST_STATUS_CODE);
			response.setMessage(StatusResponse.BAD_REQUEST);
			return response;
		}
		CardDetails oldCard = cardRepository.findById(cardId).orElse(null); 
		if(oldCard.getStatus().equalsIgnoreCase(StatusResponse.DEACTIVE_STATUS)) {
			response.setStatusCode(StatusResponse.FAILURE_STATUS_CODE);
			response.setMessage(StatusResponse.BLOCKED_CARD);
			return response;
		}
		
		response = validCardId(cardId);
		CardDetailsDTO newCardDto = new CardDetailsDTO();
		if(response!=null && response.getStatusCode().equalsIgnoreCase(StatusResponse.SUCCESS_STATUS_CODE)) { 
			if(cardDetailsDTO.getAliasName() != null) {
				// check for valid name here
				//response = validCard(cardDetailsDTO.getAliasName(), StatusResponse.UPDATE_REQUEST);
				//if(response.getStatusCode().equalsIgnoreCase(StatusResponse.SUCCESS_STATUS_CODE)) {}
				oldCard.setAliasName(cardDetailsDTO.getAliasName());
				newCardDto.setAliasName(oldCard.getAliasName());
				newCardDto.setCardId(oldCard.getCardId());
				newCardDto.setCustomerId(oldCard.getCustomerId());
				
				cardRepository.save(oldCard);
				
				response.setMessage(StatusResponse.CARD_UPDATED);
				response.setResult(newCardDto);
				
			}
			if(cardDetailsDTO.getCustomerId() != null) {
				response = validCustomerId(cardDetailsDTO.getCustomerId()); //checking if new customerId is valid
				if(response!=null && response.getStatusCode().equalsIgnoreCase(StatusResponse.SUCCESS_STATUS_CODE)) {
					CustomerDetails prevCustomer = customerRepository.findById(oldCard.getCustomerId()).orElse(null);
					CustomerDetails newCustomer = customerRepository.findById(cardDetailsDTO.getCustomerId()).orElse(null);
					newCustomer.setCardCount(newCustomer.getCardCount()+1);
					prevCustomer.setCardCount(prevCustomer.getCardCount()-1);
					customerRepository.save(prevCustomer);	
					customerRepository.save(newCustomer);
					oldCard.setCustomerId(newCustomer.getCustomerId());
					cardRepository.save(oldCard);					
					
					newCardDto.setCardId(oldCard.getCardId());
					newCardDto.setAliasName(oldCard.getAliasName());
					newCardDto.setCustomerId(oldCard.getCustomerId());
					
					response.setMessage(StatusResponse.CARD_AND_CUSTOMER_UPDATED);
					response.setResult(newCardDto);
				
					return response;
				}
				return response; //customerId is not valid
			}
			return response;
		}
		return response; //cardId is not valid
		} catch(Exception e) {
			logger.error(e.getMessage(),e);
			response.setStatusCode(StatusResponse.SERVER_ERROR_STATUS_CODE);
			response.setMessage(StatusResponse.SERVER_ERROR);
			return response;
		}

	}

	//REST API - ACTIVATE
	@Override
	public ResponseDto activateCard(String cardId) {
		//check if cardId is valid, check if card is act, check if cust is deact, activate
		CardDetails card = cardRepository.findById(cardId).orElse(null);	
		ResponseDto response = validCardId(cardId);
		try {
			if (card == null) {
				response.setStatusCode(StatusResponse.FAILURE_STATUS_CODE);
				response.setMessage(StatusResponse.CARD_DOES_NOT_EXIST);
				return response;
			}
			CardDetailsDTO cardResponse = mapper.map(card, CardDetailsDTO.class);
			if(cardResponse.getStatus().equalsIgnoreCase(StatusResponse.ACTIVE_STATUS)) {
				//card is already active
				response.setStatusCode(StatusResponse.FAILURE_STATUS_CODE);
				response.setMessage(StatusResponse.CARD_ALREADY_ACTIVE);
				return response;
			}
			CustomerDetails customer = customerRepository.findById(cardResponse.getCustomerId()).orElse(null);
			if(customer.getStatus().equalsIgnoreCase(StatusResponse.DEACTIVE_STATUS)) {
				//customer is de-active
				response.setStatusCode(StatusResponse.FAILURE_STATUS_CODE);
				response.setMessage(StatusResponse.BLOCKED_CUSTOMER);
				return response;
			}
			//activate - cardRepo carCount++
			customer.setCardCount(customer.getCardCount()+1);
			customerRepository.save(customer);
			card.setStatus(StatusResponse.ACTIVE_STATUS);
			cardRepository.save(card);
			cardResponse.setStatus(card.getStatus());
				
			cardResponse.setStatus(StatusResponse.ACTIVE_STATUS);
			response.setStatusCode(StatusResponse.SUCCESS_STATUS_CODE);
			response.setMessage(StatusResponse.CARD_ACTIVATED);
			response.setResult(cardResponse);
			return response;
		} catch(Exception e) {
			logger.error(e.getMessage(),e);
			response.setStatusCode(StatusResponse.SERVER_ERROR_STATUS_CODE);
			response.setMessage(StatusResponse.SERVER_ERROR);
			return response;
		}
	}
	
	
	//REST API - DELETE
	@Override
	public ResponseDto deleteCard(String cardId) { //method only soft-deletes, deleteById not involved
		ResponseDto response = validCardId(cardId);
		try {
		if(response!=null && response.getStatusCode().equalsIgnoreCase(StatusResponse.SUCCESS_STATUS_CODE)) {
			CardDetails cardDetails = cardRepository.findById(cardId).orElse(null);		
//			CustomerDetails customer = customerRepository.findById(cardDetails.getCustomerId()).orElse(null);
//			customer.setCardCount(customer.getCardCount()-1);
//			customerRepository.save(customer);
			CardDetailsDTO cardDto = new CardDetailsDTO();
			cardDto.setAliasName(cardDetails.getAliasName());
			//cardRepository.deleteById(cardId);
			cardDetails.setStatus(StatusResponse.DEACTIVE_STATUS);
			cardRepository.save(cardDetails);
			response.setStatusCode(StatusResponse.SUCCESS_STATUS_CODE);
			response.setMessage(StatusResponse.CARD_DEACTIVATED);	
			response.setResult(cardDto.getAliasName()); //returns name of card deleted
		}
		return response;
		} catch(Exception e) {
			logger.error(e.getMessage(),e);
			response.setStatusCode(StatusResponse.SERVER_ERROR_STATUS_CODE);
			response.setMessage(StatusResponse.SERVER_ERROR);
			return response;			
		}
	}
	
	public ResponseDto searchCard(String input) {
		ResponseDto response = new ResponseDto();
		String inputType = checkInputType(input);
		if(inputType.equalsIgnoreCase("card")) {
			response = getCardDetails(input);
			logger.info("search card complete");
			return response;
		}
		else if(inputType.equalsIgnoreCase("customer")) {
			response = searchByCustomer(input);
			logger.info("cust complete");
			return response;
		}
		else if(inputType.equalsIgnoreCase("alias")) {
			response = searchByName(input);
			logger.info("nane complete");
			return response;
		}
		
		
		
		response.setStatusCode(StatusResponse.BAD_REQUEST_STATUS_CODE);
		response.setMessage(StatusResponse.BAD_REQUEST);
		return response;
	}
	
	public String checkInputType(String input) {
		if(input.matches(RegularExpressions.CARD_ID_FORMAT)) {
			logger.info("Parameter matches card");
			return "card";
		}
		else if(input.matches(RegularExpressions.CUSTOMER_ID_FORMAT))
			return "customer";
		return "alias";
	}
	
	public ResponseDto searchByCustomer(String input) {
		ResponseDto response = new ResponseDto();
		//does customer exist:
		response = validCustomerId(input);
		if(response.getStatusCode().equalsIgnoreCase(StatusResponse.FAILURE_STATUS_CODE)) {
			return response;
		}
		List<CardDetails> cardDetails = cardRepository.findByCustomerId(input);
		if(cardDetails.isEmpty()) {
			logger.info("No such cust");
			response.setStatusCode(StatusResponse.FAILURE_STATUS_CODE);
			response.setMessage(StatusResponse.NO_CARDS_IN_DB);
			return response;
		}
		List<CardDetailsDTO> cardDto = cardDetails.stream().map(card -> mapper.map(card, CardDetailsDTO.class)).collect(Collectors.toList());
		response.setStatusCode(StatusResponse.SUCCESS_STATUS_CODE);
		response.setMessage(StatusResponse.FETCHED_ALL_CARDS);
		response.setResult(cardDto);
		return response;
	}
	
	public ResponseDto searchByName(String input) {
		ResponseDto response = new ResponseDto();
		List<CardDetails> cardDetails = cardRepository.findByAliasName(input);
		if(cardDetails.isEmpty()) {
			logger.info("No such card name");
			response.setStatusCode(StatusResponse.FAILURE_STATUS_CODE);
			response.setMessage(StatusResponse.CUSTOMER_DOES_NOT_EXIST);
			return response;
		}
		List<CardDetailsDTO> cardDto = cardDetails.stream().map(card -> mapper.map(card, CardDetailsDTO.class)).collect(Collectors.toList());
		response.setStatusCode(StatusResponse.SUCCESS_STATUS_CODE);
		response.setMessage(StatusResponse.FETCHED_ALL_CARDS);
		response.setResult(cardDto);
		return response;
	}
	
	// FETCH ENTRIES
	public long fetchCardEntries() {
		return cardRepository.count();
	}
	
	// VALID CARD ID
	public ResponseDto validCardId(String cardId) {
		ResponseDto statusResponse = new ResponseDto();
		if(cardRepository.findById(cardId).orElse(null)==null) {
			statusResponse.setStatusCode(StatusResponse.FAILURE_STATUS_CODE);
			statusResponse.setMessage(StatusResponse.CARD_DOES_NOT_EXIST);
			return statusResponse;
		}
		statusResponse.setStatusCode(StatusResponse.SUCCESS_STATUS_CODE);
		return statusResponse;
	}
	
	// VALIDATE CUSTOMER ID
	public ResponseDto validCustomerId(String id) {
		ResponseDto statusResponse = new ResponseDto();
		if(customerRepository.findById(id).orElse(null) == null) {
			statusResponse.setStatusCode(StatusResponse.FAILURE_STATUS_CODE);
			statusResponse.setMessage(StatusResponse.CUSTOMER_DOES_NOT_EXIST);
			return statusResponse;
		}
		statusResponse.setStatusCode(StatusResponse.SUCCESS_STATUS_CODE);
		return statusResponse;
		
	}
	
	public ResponseDto validCard(String name, int RequestCase) {
		ResponseDto response = new ResponseDto();
		// on create: check format and check if it name already exists as another card of SAME customer
		// on update: check format and check if updated name already exists, and check if this name's id is different than the existing cardId
		if(name.matches(RegularExpressions.CARD_NAME_FORMAT)) {
			response.setStatusCode(StatusResponse.SUCCESS_STATUS_CODE);
			return response;
		}
		response.setStatusCode(StatusResponse.FAILURE_STATUS_CODE);
		response.setMessage(StatusResponse.NAME_INVALID_FORMAT);
		return response;
	}
	
	
	// CHECKS FOR BAD REQUESTS
		public boolean validRequest(CardDetailsDTO cardDto, int requestCase) {
			switch (requestCase) {
			//create - paramters: name, customerId
			case 1:
				if(cardDto.getAliasName() != null &&
					cardDto.getCustomerId() != null) {
					if(cardDto.getCardId() == null &&
						cardDto.getStatus() == null)
						return true;
				}
				return false;
			//getall - parameters: none
			case 2:
				if(cardDto == null)
					return true;
				return false;
			//getbyid - paramters: (card id)
			case 3:
				if(cardDto == null)
					return true;
				return false;
			//update - parameters: (card id), optionally- name, customerId
			case 4:
				if(cardDto.getAliasName() != null ||
				cardDto.getCustomerId() != null) {
				if(cardDto.getCardId() == null &&
					cardDto.getStatus() == null)
					return true;
			}
			return false;
			//activate - parameters: (card id)
			case 5:
				if(cardDto == null)
					return true;
				return false;
			//delete
			case 6:
				break;
			//search
			case 7:
				break;
			default:
				break;
			}
			return false;
		}
}