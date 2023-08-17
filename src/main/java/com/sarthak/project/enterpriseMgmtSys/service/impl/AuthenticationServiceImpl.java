package com.sarthak.project.enterpriseMgmtSys.service.impl;

import org.apache.catalina.connector.Response;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.sarthak.project.enterpriseMgmtSys.GenericClass.DateAndTime;
import com.sarthak.project.enterpriseMgmtSys.GenericClass.RegularExpressions;
import com.sarthak.project.enterpriseMgmtSys.GenericClass.ResponseDto;
import com.sarthak.project.enterpriseMgmtSys.GenericClass.StatusResponse;
import com.sarthak.project.enterpriseMgmtSys.entity.CustomerDetails;
import com.sarthak.project.enterpriseMgmtSys.entity.User;
import com.sarthak.project.enterpriseMgmtSys.payload.CustomerLoginDto;
import com.sarthak.project.enterpriseMgmtSys.payload.UserDto;
import com.sarthak.project.enterpriseMgmtSys.repository.CustomerRepository;
import com.sarthak.project.enterpriseMgmtSys.repository.UserRepository;
import com.sarthak.project.enterpriseMgmtSys.service.AuthenticationService;

import javax.servlet.http.HttpServletRequest; 
import javax.servlet.http.HttpSession;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
	private static final Logger logger = LoggerFactory.getLogger(AuthenticationServiceImpl.class);
	@Autowired
    private UserDetailsManager userDetailsManager;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private UserRepository userRepository;
	
    @Autowired
	public ModelMapper mapper;

    @Autowired
    public CustomerRepository customerRepository;
    //THYMELEAF
    
    
	// Retrieve the exception from the session
	public String login(HttpServletRequest request, HttpSession session, Model model) { 
	       // Retrieve the exception from the session
		try {
	       Exception exception = (Exception) request.getSession().getAttribute("SPRING_SECURITY_LAST_EXCEPTION");
	       logger.info("Recieved exception from getSession");
	       if(exception!=null)
	    	   logger.error(exception.getMessage(),exception);
	       // Check if the exception is a BadCredentialsException
	       if (exception instanceof BadCredentialsException) { 
	    	   logger.info("BAD CREDS");
	           session.setAttribute("error", "Invalid username and password!"); 
	       } else if (exception instanceof LockedException) { 
	           // If it's a LockedException, set the error message to the exception's message
	    	   logger.info("At locked exception, sent to session from AuthProvider.\nAccount is locked and should not be able to log in");
	           session.setAttribute("error", exception.getMessage()); 
	       } else { 
	           // For any other exception, set the default error message
	    	   logger.info("DEFAULT ERROR WHILE LOGGIN IN");
	           session.setAttribute("error", "Invalid username and password! - DEFAULT"); 
	       } 
	       logger.info("returning string login to thymeleaf");
	       model.addAttribute("justLoggedIn", true);
		   logger.info("add justLoggedIn attribute to model");
	       return "index"; 
	   } catch(Exception e) {
		   logger.info("Caught error in controller");
		   session.setAttribute("error", "User not found!");
		   model.addAttribute("justLoggedIn", true);
//		   logger.info("add justLoggedIn attribute to model");
           return "index";
	   }
	}

	public String getErrorMessage(HttpServletRequest request, String key) {
	      Exception exception = (Exception) request.getSession().getAttribute(key); 
	      logger.info("At getErrorMessage");
	      if(exception!=null)
	    	  logger.error(exception.getMessage(),exception);
	      String error = ""; 
	      if (exception instanceof BadCredentialsException) { 
	    	 logger.info("Bad creds");
	         error = "Invalid password!"; 
	      } else if (exception instanceof LockedException) { 
	    	 logger.info("Locked");
	         error = exception.getMessage(); 
	      } else { 
	    	 logger.info("Default");
	         error = "Invalid username"; 
	      } 
	      logger.info("returning");
	      return error;
	   }

	public ResponseDto memberLogin(String customerId) {
		ResponseDto response = new ResponseDto();
		try {
			// parameter <customer> can be either customerId or customer Name
			if(customerId.matches(RegularExpressions.CUSTOMER_ID_FORMAT)) {
				CustomerDetails customerDetails = customerRepository.findById(customerId).orElse(null);
				if(customerDetails==null) {
					logger.info("customer did not enter an existing customer id");
					response.setStatusCode(StatusResponse.FAILURE_STATUS_CODE);
					response.setMessage(StatusResponse.CUSTOMER_DOES_NOT_EXIST);
					return response;
				}
				//success path
				CustomerLoginDto customerLoginDto = mapper.map(customerDetails, CustomerLoginDto.class);
				response.setStatusCode(StatusResponse.SUCCESS_STATUS_CODE);
				customerLoginDto.setEncodedMobile(encodeMobile(customerDetails.getCustomerMobile()));
				response.setResult(customerLoginDto);
				return response;
				
			} else {
				logger.info("customer did not enter valid customer id format");
				response.setStatusCode(StatusResponse.FAILURE_STATUS_CODE);
				response.setMessage(StatusResponse.CUSTOMTER_ID_INVALID_FORMAT);
				return response;
			}
			
		} catch (Exception e) {
			logger.info("caught an exception at memberLogin");
			logger.error(e.getMessage(),e);
			response.setStatusCode(StatusResponse.SERVER_ERROR);
			return response;
		}
	}
	
	@Override
	public ResponseDto customerIdVerification(String customerId, String mobile) {
		ResponseDto response = new ResponseDto();
		try {
			CustomerDetails customerDetails = customerRepository.findById(customerId).orElse(null);
			if(mobile.equalsIgnoreCase(customerDetails.getCustomerMobile())) {
				logger.info(String.format("Mobile matched as : %s", mobile));
				response.setStatusCode(StatusResponse.SUCCESS_STATUS_CODE);
				return response;
			} else {
				logger.info(String.format("Mobile did not match : %s", mobile));
				logger.info(String.format("and in repo : %s", customerDetails.getCustomerMobile()));
				response.setStatusCode(StatusResponse.FAILURE_STATUS_CODE);
				return response;
			}
		} catch (Exception e) {
			logger.info("caught an exception at cusIdVer");
			logger.error(e.getMessage(),e);
			response.setStatusCode(StatusResponse.SERVER_ERROR);
			return response;
		}
	}
	
	
	@Override
    public ResponseDto addUser(String username, String password, String email) {
		ResponseDto response = new ResponseDto();
		logger.info("In register, addUser, authServImpl");
		if(invalidUsernameFormat(username)) {
			logger.info(String.format("name is of invalid format %s",username));
        	response.setStatusCode(StatusResponse.FAILURE_STATUS_CODE);
        	response.setMessage(StatusResponse.NAME_INVALID_FORMAT);
        	return response;
		} else if (userRepository.findUserByUsername(username).isPresent()) {
        	logger.info("username already exists",username);
            //map to /register and 
        	response.setStatusCode(StatusResponse.FAILURE_STATUS_CODE);
        	response.setMessage(StatusResponse.USERNAME_ALREADY_EXISTS);
        	return response;
        	//throw new IllegalArgumentException("User with username already exists: " + username);
            
        } else if(invalidEmailFormat(email)) {
        	logger.info(String.format("email is of invalid format %s",email));
        	response.setStatusCode(StatusResponse.FAILURE_STATUS_CODE);
        	response.setMessage(StatusResponse.EMAIL_INVALID_FORMAT);
        	return response;
        } else if(userRepository.findUserByEmail(email).isPresent()) {
        	logger.info("email already exists",email);
        	response.setStatusCode(StatusResponse.FAILURE_STATUS_CODE);
        	response.setMessage(StatusResponse.EMAIL_ALREADY_EXISTS);
        	return response;
        }
        
        

        // Create the new user
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setAccountNonLocked(true);
        user.setCreatedOn(DateAndTime.generateDateAndTime());
        userDetailsManager.createUser(user);
        logger.info("manager created user");
        userRepository.save(user);
        logger.info("db saved user");
        
        UserDto userResponse = mapper.map(user, UserDto.class);
        response.setStatusCode(StatusResponse.SUCCESS_STATUS_CODE);
        response.setResult(userResponse);
        
        return response;
    }
	
	
	
	//POSTMAN
	
	@Override
	public ResponseDto loginApi(HttpServletRequest request, HttpSession session) {
	    ResponseDto response = new ResponseDto();

	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    logger.info("Name is");
	    logger.info(authentication.getName());
	    if (authentication != null && authentication.isAuthenticated()) {
	        // Successful authentication
	        // You can perform additional logic or return a success response here.
	        response.setStatusCode(StatusResponse.FAILURE_STATUS_CODE);
	        response.setMessage("Please Login to Continue. Use /login");
	    } else {
	        // Authentication failed
	        // You can handle different types of authentication errors here.
	        response.setStatusCode(StatusResponse.FAILURE_STATUS_CODE);
	        response.setMessage("Invalid username or password!");
	    }

	    return response;
	}

	@Override
	public ResponseDto forgotPass(String userName) {
		//first check if user exists
		// then ask to complete
		ResponseDto response = new ResponseDto();
		User user = userRepository.findUserByUsername(userName).orElse(null);
		try {
			if(user==null) {
				response.setStatusCode(StatusResponse.FAILURE_STATUS_CODE);
				logger.info("In forgot, no such user exists in db");
				return response;
			}
			else {
				response.setStatusCode(StatusResponse.SUCCESS_STATUS_CODE);
				UserDto userDto = new UserDto();
				userDto.setUsername(user.getUsername());
				userDto.setEncodedEmail(encodeEmail(user.getEmail()));
				userDto.setEmail(user.getEmail());
				logger.info("encoded email successfully");
				response.setResult(userDto);
				return response;				
			}
		} catch(Exception e) {
			logger.error(e.getMessage(),e);
			response.setStatusCode(StatusResponse.SERVER_ERROR);
			return response;
		}
	}
	
	@Override
	public ResponseDto verifyEmail(UserDto userDto, String email) {
		ResponseDto response = new ResponseDto();
		try {
			if(userDto.getEmail().equalsIgnoreCase(email)) {
				response.setStatusCode(StatusResponse.SUCCESS_STATUS_CODE);
				return response;
			}
			else {
				response.setStatusCode(StatusResponse.FAILURE_STATUS_CODE);
				return response;
			}
		} catch(Exception e){
			logger.error(e.getMessage(),e);
			response.setStatusCode(StatusResponse.SERVER_ERROR);
			return response;
		}
	}

	@Override
	public ResponseDto resetPass(UserDto userDto, String newPass, String confirmPass) {
		ResponseDto response = new ResponseDto();
		try {
			if(!newPass.equalsIgnoreCase(confirmPass)) {
				response.setStatusCode(StatusResponse.FAILURE_STATUS_CODE);
				return response;
			}
			else {
				//atp we can succesfully reset the password using password encoder
				logger.info(String.format("Changing password of user now, user name : %s", userDto.getUsername()));
				User user = userRepository.findUserByUsername(userDto.getUsername()).orElse(null);
				if(user==null) {
					logger.info(String.format("user is null %d", 404));
				}
				else {
					logger.info(String.format("user is not null, proceeding to encode %d", 200));
				}
				user.setPassword(passwordEncoder.encode(newPass));
				user.setUpdatedOn(DateAndTime.generateDateAndTime());
//				userDetailsManager.createUser(user);
//		        logger.info("manager created user");
		        userRepository.save(user);
		        logger.info("db saved user");
		        
		        UserDto userResponse = mapper.map(user, UserDto.class);
		        response.setStatusCode(StatusResponse.SUCCESS_STATUS_CODE);
		        response.setResult(userResponse);
		        
		        return response;
			}
		} catch(Exception e) {
			logger.info("At error, resetPass method, caught exception");
			logger.error(e.getMessage(),e);
			response.setStatusCode(StatusResponse.SERVER_ERROR);
			return response;
		}
	}
	
	public static String encodeEmail(String email) { //method assumes that email has @ and .com (proper email regex), atleast 4 letters before @
		//sarthak24champ@gmail.com --> sar***********@gmail.com
		String encoded = "";
		String parts[] = email.split("@");
		String name = parts[0].substring(0,3);
		String domain = "@"+parts[1];
		int charsRestricted = email.length()-4-parts[1].length();
		
		encoded+=name+"*".repeat(charsRestricted)+domain;
		return encoded;
	}
	
	public static String encodeMobile(String mobile) { //method assumes that email has @ and .com (proper email regex), atleast 4 letters before @
		//1234567890 --> ******7890
		String lastFourDigits = mobile.substring(6);
		String encoded = "*".repeat(6)+lastFourDigits;
		return encoded;
	}
	
	public boolean invalidEmailFormat(String email) {
		if(email.matches(RegularExpressions.EMAIL_FORMAT)) {
			return false;
		}
		return true;
	}
	
	public boolean invalidUsernameFormat(String username) {
		if(username.matches(RegularExpressions.USER_NAME_FORMAT)) {
			return false;
		}
		return true;
	}

} //end class