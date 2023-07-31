package com.sarthak.project.enterpriseMgmtSys.service;

import com.sarthak.project.enterpriseMgmtSys.GenericClass.ResponseDto;
import com.sarthak.project.enterpriseMgmtSys.payload.CardDetailsDTO;

public interface CardService {
	//all services and business-logic functions
	ResponseDto createCard(CardDetailsDTO cardDetailsDTO);
	
	ResponseDto getAllCards();
	
	ResponseDto getCardDetails(String cardId);	
	
	ResponseDto updateCard(CardDetailsDTO cardDetailsDTO, String cardId);
	
	ResponseDto activateCard(String cardId);
	
	ResponseDto deleteCard(String cardId);
	
	ResponseDto searchCard(String input);
	
}