package com.sarthak.project.enterpriseMgmtSys.GenericClass;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sarthak.project.enterpriseMgmtSys.repository.CardRepository;

@Component
public class IdGeneratorCard {
	private static final String PREFIX = "CARD";
	private static final int ID_LENGTH = 5;

	@Autowired
	private CardRepository cardRepository;	

	public IdGeneratorCard(CardRepository cardRepository) {
		super();
		this.cardRepository = cardRepository;
	}

	public String generateCardId() {
		String digit;
		int incremented=0;
		String cardIdNumber = cardRepository.maximumExistingCardId();
		if (cardIdNumber == null || cardIdNumber.isEmpty()) {
			digit = "1";
		} else {
			digit = cardIdNumber;
			digit = digit.replaceAll("\\D+", "");
			 incremented = Integer.valueOf(digit) + 1;
		}
		String paddedNumber = String.format("%0" + ID_LENGTH + "d", incremented);
		return PREFIX + paddedNumber;
	}
}
