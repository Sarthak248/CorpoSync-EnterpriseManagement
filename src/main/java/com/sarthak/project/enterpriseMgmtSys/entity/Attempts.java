package com.sarthak.project.enterpriseMgmtSys.entity;

import javax.persistence.Column;
import javax.persistence.Entity; 
import javax.persistence.GeneratedValue; 
import javax.persistence.GenerationType; 
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import lombok.Data;
import lombok.ToString; 

@Data
@ToString
@Entity 
@Table(name = "Attempts")
public class Attempts { 
   @Id 
   @GeneratedValue(strategy = GenerationType.IDENTITY) 
   @Column(name = "attemptId", nullable = false)
   private int id;
   
   @Column(name = "userName", nullable = false)
   private String username; 
    @JoinColumn(name = "username", insertable = false, updatable = false)
	private User user;
   
   @Column(name = "numberOfAttempts", nullable = false)
   private int attempts;
   
   
}