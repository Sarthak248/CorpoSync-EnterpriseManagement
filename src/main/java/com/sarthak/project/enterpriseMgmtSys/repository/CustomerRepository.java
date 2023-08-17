package com.sarthak.project.enterpriseMgmtSys.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

import com.sarthak.project.enterpriseMgmtSys.entity.CustomerDetails;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerDetails, String>{
	
	// query to check if Param mobileNumber exists anywhere in the column customerMobile
	@Query("SELECT COUNT(c) > 0 FROM CustomerDetails c WHERE c.customerMobile = :customerMobile")
    boolean existsByMobileNumber(@Param("customerMobile") Object customerMobile);
	
	
	// query to return Id corresponding to Param mobileNumber
	@Query("SELECT c.customerId FROM CustomerDetails c WHERE c.customerMobile = :customerMobile")
	public String checkIdWithNumber(@Param("customerMobile") Object customerMobile);
	
	//cardDetails.getCustomerId()
	@Query("SELECT c.cardCount FROM CustomerDetails c WHERE c.customerId = :customerId")
	public int checkCountWithId(@Param("customerId") Object customerId);
	
	// query to check if Param Id exists anywhere in the column customerId
	@Query("SELECT COUNT(c) > 0 FROM CustomerDetails c WHERE c.customerId = :customerId")
	boolean existsById(@Param("customerId") String customerId);
	
	@Query("SELECT MAX(c.customerId) FROM CustomerDetails c")
	public Object maximumExistingCustomerId();
	
	CustomerDetails findByCustomerId(String customerId);
	
	List<CustomerDetails> findByCustomerMobile(String customerMobile);
	
	List<CustomerDetails> findByEnterpriseId(String enterpriseId);
	
	List<CustomerDetails> findByCustomerName(String customerName);
}