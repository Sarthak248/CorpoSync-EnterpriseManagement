package com.sarthak.project.enterpriseMgmtSys.entity;

import javax.persistence.Column;


import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Data;
import lombok.ToString;

// Following columns are considered
// ID , Name, MobileNumber, CardCount

//lombok to reduce boilerplate getter and setter code
@Data 
@ToString
@Entity
@Table(name="CardDetails")
public class CardDetails {
	@Id
	@Column(name = "cardId", nullable = false)
	private String cardId; // primary key
	
	@Column(name="aliasName", nullable = false)
	private String aliasName;
	
	@Column(name="customerId", nullable = false)
	private String customerId;
	
	@Column(name = "status", nullable = false)
	private String status;
	
	@ToString.Exclude
	@JsonBackReference
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customerId", insertable = false, updatable = false)
	private CustomerDetails customerDetails;

}