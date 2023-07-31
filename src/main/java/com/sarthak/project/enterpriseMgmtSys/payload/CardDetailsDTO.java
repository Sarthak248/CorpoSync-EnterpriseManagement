package com.sarthak.project.enterpriseMgmtSys.payload;

//import javax.persistence.Column;
//import javax.persistence.FetchType;
//import javax.persistence.Id;
//import javax.persistence.JoinColumn;
//import javax.persistence.ManyToOne;

//import com.example.DemoProject.entity.CustomerDetails;
//import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Data;
//import lombok.ToString;

@Data
public class CardDetailsDTO {
	private String cardId;
	
	private String aliasName;
	
	private String customerId;
	
	private String status;
	
	 
	
//	@ToString.Exclude
//	@JsonBackReference
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "customerId", insertable = false, updatable = false)
//	private CustomerDetails customerDetails;
}