package com.sarthak.project.enterpriseMgmtSys.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sarthak.project.enterpriseMgmtSys.GenericClass.IdGeneratorCustomer;
import com.sarthak.project.enterpriseMgmtSys.repository.CardRepository;
import com.sarthak.project.enterpriseMgmtSys.repository.CustomerRepository;
import com.sarthak.project.enterpriseMgmtSys.service.CardService;
import com.sarthak.project.enterpriseMgmtSys.payload.CardDetailsDTO;
import com.sarthak.project.enterpriseMgmtSys.payload.CustomerDetailsDTO;
import com.sarthak.project.enterpriseMgmtSys.GenericClass.ResponseDto;
import com.sarthak.project.enterpriseMgmtSys.GenericClass.StatusResponse;
import com.sarthak.project.enterpriseMgmtSys.entity.CardDetails;
import com.sarthak.project.enterpriseMgmtSys.entity.CustomerDetails;

import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CustomerServiceImplTest {
	private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImplTest.class);	

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private IdGeneratorCustomer idGen;
    
    @Mock
    private CardRepository cardRepository;
    
    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private CustomerServiceImpl customerService;
    
    @InjectMocks
    private CardServiceImpl cardService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testNewCustomer() {
        // Test case 1: Bad Request
        {
            // Create a CustomerDetailsDTO with incomplete/invalid information
            CustomerDetailsDTO customerDetailsDTO = new CustomerDetailsDTO();
            customerDetailsDTO.setCustomerName(null);
            customerDetailsDTO.setCustomerMobile("1234567890");
            customerDetailsDTO.setEnterpriseId("ABC");
            logger.info("Set bad request in CustServImplTest : testNewCustomer");
            testBadRequestScenario(customerDetailsDTO, null, StatusResponse.CREATE_REQUEST);
//            // Call the service method
//            ResponseDto response = customerService.newCustomer(customerDetailsDTO);
//            
//            // Assertions
//            assertEquals(StatusResponse.BAD_REQUEST_STATUS_CODE, response.getStatusCode());
//            assertEquals(StatusResponse.BAD_REQUEST, response.getMessage());
        }
        // Test case 2: Success scenario
        {
            // Mock behavior of dependencies
            when(idGen.generateCustomerId()).thenReturn("mockedCustomerId");
            when(customerRepository.save(any())).thenReturn(null);

            // Create a CustomerDetailsDTO for testing
            CustomerDetailsDTO customerDetailsDTO = new CustomerDetailsDTO();
            customerDetailsDTO.setCustomerName("John Doe");
            customerDetailsDTO.setCustomerMobile("1234567890");
            customerDetailsDTO.setEnterpriseId("ABC");

            // Call the service method
            ResponseDto response = customerService.newCustomer(customerDetailsDTO);
            CustomerDetailsDTO customerProcessed = (CustomerDetailsDTO) response.getResult();

            // Assertions for success scenario
            assertEquals(StatusResponse.CREATED_STATUS_CODE, response.getStatusCode());
            assertEquals(StatusResponse.CUSTOMER_CREATED, response.getMessage());
            assertNotNull(customerProcessed);
            assertEquals("CR00001", customerProcessed.getCustomerId());
            assertEquals("1234567890", customerProcessed.getCustomerMobile());
            assertEquals("John Doe", customerProcessed.getCustomerName());
            assertEquals("ACTIVE", customerProcessed.getStatus());
            assertEquals("ABC", customerProcessed.getEnterpriseId());
            assertEquals(null, customerProcessed.getAllCards());
            assertEquals(0, customerProcessed.getCardCount());
        }

        // Test case 3: Failed Request scenario
        {
            // Create a CustomerDetailsDTO with invalid data
            CustomerDetailsDTO customerDetailsDTO = new CustomerDetailsDTO();
            customerDetailsDTO.setCustomerName("^^()+-*"); // Invalid name
            customerDetailsDTO.setCustomerMobile("1234567890");
            customerDetailsDTO.setEnterpriseId("ABC");
            logger.info("Set invalid name in : failed request scenario : testNewCustomer");

            // Call the service method
            ResponseDto response = customerService.newCustomer(customerDetailsDTO);

            // Assertions for bad request scenario
            assertEquals(StatusResponse.FAILURE_STATUS_CODE, response.getStatusCode());
            assertEquals(StatusResponse.NAME_INVALID_FORMAT, response.getMessage());
        }
    }

    
    @Test
    public void testGetAllCustomers() {
        // Test case 1: Bad Request
        {
            // Create a CustomerDetailsDTO with incomplete/invalid information
            CustomerDetailsDTO customerDetailsDTO = new CustomerDetailsDTO();
            customerDetailsDTO.setCustomerName("test");
            customerDetailsDTO.setCustomerMobile(null);
            customerDetailsDTO.setEnterpriseId("ABC");
            
            testBadRequestScenario(customerDetailsDTO, null, StatusResponse.GETALL_REQUEST);
            logger.info("came from testBadReqScen : testGetAllCustomers");
//            // Call the service method
//            ResponseDto response = customerService.newCustomer(customerDetailsDTO);
//            
//            // Assertions
//            assertEquals(StatusResponse.BAD_REQUEST_STATUS_CODE, response.getStatusCode());
//            assertEquals(StatusResponse.BAD_REQUEST, response.getMessage());
        }
        {
            List<CustomerDetails> mockCustomers = new ArrayList<>();
            CustomerDetails mockCustomer1 = new CustomerDetails();
            mockCustomer1.setCustomerId("CR00001");
            mockCustomer1.setCustomerName("John Doe");
            mockCustomer1.setCustomerMobile("1234567890");
            mockCustomer1.setEnterpriseId("ABC");
            mockCustomers.add(mockCustomer1);
            
            when(customerRepository.save(any())).thenReturn(mockCustomer1);            
            when(customerRepository.findAll()).thenReturn(mockCustomers);
            when(mapper.map(any(), eq(CustomerDetailsDTO.class))).thenReturn(new CustomerDetailsDTO());
            
            
           
            List<CardDetails> mockCards = new ArrayList<>();
            CardDetails mockCard1 = new CardDetails();
            mockCard1.setCustomerId("CR00001");
            mockCard1.setCardId("CARD00001");
            mockCard1.setAliasName("testCard1");
            mockCard1.setStatus("ACTIVE");
            mockCards.add(mockCard1);
            mockCustomer1.setListCards(mockCards);
            
            when(cardRepository.save(any())).thenReturn(mockCard1);
            when(cardRepository.findAll()).thenReturn(mockCards);
            when(mapper.map(any(), eq(CardDetailsDTO.class))).thenReturn(new CardDetailsDTO());

            CardDetailsDTO mockCard1Dto = mapper.map(mockCard1, CardDetailsDTO.class);
            
            cardService.createCard(mockCard1Dto);

            CustomerDetailsDTO mockCustomer1Dto = mapper.map(mockCustomer1, CustomerDetailsDTO.class);
            
            customerService.newCustomer(mockCustomer1Dto);
            // Call the service method
            ResponseDto response = customerService.getAllCustomers();
            List<CustomerDetailsDTO> allCustomers = (List<CustomerDetailsDTO>) response.getResult();

            // Assertions
            assertEquals(StatusResponse.SUCCESS_STATUS_CODE, response.getStatusCode());
            assertEquals(StatusResponse.FETCHED_ALL_CUSTOMERS, response.getMessage());
            assertNotNull(allCustomers);
            assertEquals(1, allCustomers.size());
            
            CustomerDetailsDTO customer = allCustomers.get(0);
            assertEquals("CR00001", customer.getCustomerId());
            assertEquals("John Doe", customer.getCustomerName());
            assertEquals("1234567890", customer.getCustomerMobile());
            assertEquals("ABC", customer.getEnterpriseId());
            assertEquals(1, customer.getAllCards().size());
            
            CardDetailsDTO card = customer.getAllCards().get(0);
            assertEquals("testCard1", card.getAliasName());
            assertEquals("CR00001", card.getCustomerId());
            assertEquals("CARD00001", card.getCardId());
            assertEquals("ACTIVE", card.getStatus());
        }
    }
    

    // Bad request scenario - common to all
    private void testBadRequestScenario(CustomerDetailsDTO customerDetailsDTO, String customerId,
    									int requestCase) {
    	ResponseDto response = new ResponseDto();
        switch (requestCase) {
		case 1:
			response = customerService.newCustomer(customerDetailsDTO);
			break;
		case 2:
			response = customerService.getAllCustomers();
			break;
		case 3:
			response = customerService.getCustomerById(customerId);
			break;
		case 4:
			response = customerService.updateCustomerById(customerDetailsDTO, customerId);
			break;
		case 5:
			response = customerService.activateCustomer(customerId);
			break;
		case 6:
			response = customerService.deleteCustomerById(customerId);
			break;
		case 7:
			response = customerService.searchCustomer(customerId);
			break;
		default:
			break;
		}
    	
                
        // Assertions
        if(requestCase==1 || requestCase==4) {
        	assertEquals(StatusResponse.BAD_REQUEST_STATUS_CODE, response.getStatusCode());
        	assertEquals(StatusResponse.BAD_REQUEST, response.getMessage());
        }
        
    }
}
