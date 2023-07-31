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
import com.sarthak.project.enterpriseMgmtSys.payload.CardDetailsDTO;
import com.sarthak.project.enterpriseMgmtSys.service.CardService;

// using @RestController to avoid using @ResponseBody annotation
@RestController
@RequestMapping("/api/cards") // base URI
public class CardController {
	private static final Logger logger = LoggerFactory.getLogger(CardController.class);
	@Autowired
	public CardService cardService;

	// REST API - CREATE
	@PostMapping("/create")
	public ResponseEntity<?> createCard(@RequestBody CardDetailsDTO cardDetailsDTO) {
		logger.info("At /api/cards/create");
		ResponseDto response = cardService.createCard(cardDetailsDTO);
		logger.info("Generated response from Card Service");
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	// REST API - GET ALL
	@GetMapping("/getall")
	public ResponseEntity<?> getAllCards() {
		logger.info("At /api/cards/getall");
		ResponseDto response = cardService.getAllCards();
		logger.info("Generated response from Card Service");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// REST API - GET BY ID
	@GetMapping("/get/{id}")
	public ResponseEntity<?> getCardDetails(@PathVariable("id") String cardId) {
		logger.info("At /api/cards/get");
		ResponseDto response = cardService.getCardDetails(cardId);
		logger.info("Generated response from Card Service");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// REST API - UPDATE BY ID
	@PatchMapping("/update/{id}")
	public ResponseEntity<?> updateCard(@RequestBody CardDetailsDTO cardDetailsDTO,
			@PathVariable("id") String cardId) {
		logger.info("At /api/cards/update");
		ResponseDto response = cardService.updateCard(cardDetailsDTO, cardId);
		logger.info("Generated response from Card Service");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	// REST API - ACTIVATE BY ID
	@PatchMapping("/activate/{id}")
	public ResponseEntity<?> activateCard(@PathVariable("id") String cardId) {
		logger.info("At /api/cards/activate");
		ResponseDto response = cardService.activateCard(cardId);
		logger.info("Generated response from Card Service");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// REST API - DELETE BY ID
	@DeleteMapping("/delete/{id}") // specific URI
	public ResponseEntity<?> deleteCard(@PathVariable("id") String cardId) {// path variable - id is passed and
		logger.info("At /api/cards/delete");
		ResponseDto response = cardService.deleteCard(cardId);
		logger.info("Generated response from Card Service");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	//REST API - SEARCH
	@GetMapping("/search/{input}")
	public ResponseEntity<?> searchCard(@PathVariable("input") String input){
		logger.info("At /api/cards/search");
		ResponseDto response = cardService.searchCard(input);
		logger.info("Generated response from Card Service");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}