package com.sarthak.project.enterpriseMgmtSys.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.sarthak.project.enterpriseMgmtSys.entity.CardDetails;

@Repository
public interface CardRepository extends JpaRepository<CardDetails, String> {
	
	@Query("SELECT MAX(c.cardId) FROM CardDetails c")
	public String maximumExistingCardId();
	
	List<CardDetails> findByAliasName(String aliasName);
	
	List<CardDetails> findByCustomerId(String customerId);
}