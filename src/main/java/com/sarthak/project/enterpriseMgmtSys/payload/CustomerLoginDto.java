package com.sarthak.project.enterpriseMgmtSys.payload;

import java.util.List;

//import lombok.Data;

public class CustomerLoginDto {
	private String customerId; // primary key

	private String customerName;

	private String customerMobile;
	
	private String encodedMobile;

	private int cardCount = 0;

	private String enterpriseId;
	
	private String status;
	
	private List<CardDetailsDTO> allCards;

	public String getCustomerId() {
		return customerId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public String getCustomerMobile() {
		return customerMobile;
	}
	
	public String getEncodedMobile() {
		return encodedMobile;
	}

	public int getCardCount() {
		return cardCount;
	}

	public String getEnterpriseId() {
		return enterpriseId;
	}

	public List<CardDetailsDTO> getAllCards() {
		return allCards;
	}
	

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public void setCustomerMobile(String customerMobile) {
		this.customerMobile = customerMobile;
	}
	
	public void setEncodedMobile(String encodedMobile) {
		this.encodedMobile = encodedMobile;
	}

	public void setCardCount(int cardCount) {
		this.cardCount = cardCount;
	}

	public void setEnterpriseId(String enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

	public void setAllCards(List<CardDetailsDTO> allCards) {
		this.allCards = allCards;
	}
	
}