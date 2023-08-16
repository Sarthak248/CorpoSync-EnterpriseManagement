package com.sarthak.project.enterpriseMgmtSys.entity;

import java.util.ArrayList;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Data;
import lombok.ToString;
// Following columns are considered
// ID , Name, MobileNumber, CardCount

@Data // Lombok to reduce boilerplate getter and setter code
@ToString
@Entity
@Table(name = "CustomerDetails")
public class CustomerDetails {
	@Id
	@Column(name = "customerId", nullable = false)
	private String customerId; // primary key

	@Column(name = "customerName", nullable = false)
	private String customerName;

	@Column(name = "customerMobile", nullable = false)
	private String customerMobile;

	@Column(name = "cardCount", nullable = false)
	private int cardCount = 0;

	@Column(name = "enterpriseId", nullable = false)
	private String enterpriseId;
	
	@Column(name = "status", nullable = false)
	private String status;

	@Column(name = "createdOn", nullable = false)
	private String createdOn;
	
	@Column(name = "updatedOn")
	private String updatedOn;
	
	@ToString.Exclude
	@JsonManagedReference
	@OneToMany(mappedBy = "customerId", orphanRemoval = false)
	private List<CardDetails> listCards = new ArrayList<>();
}