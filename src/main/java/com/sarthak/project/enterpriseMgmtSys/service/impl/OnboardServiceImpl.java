package com.sarthak.project.enterpriseMgmtSys.service.impl;

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

import com.sarthak.project.enterpriseMgmtSys.GenericClass.ResponseDto;
import com.sarthak.project.enterpriseMgmtSys.GenericClass.StatusResponse;
import com.sarthak.project.enterpriseMgmtSys.entity.User;
import com.sarthak.project.enterpriseMgmtSys.payload.UserDto;
import com.sarthak.project.enterpriseMgmtSys.repository.UserRepository;
import com.sarthak.project.enterpriseMgmtSys.service.OnboardService;

import javax.servlet.http.HttpServletRequest; 
import javax.servlet.http.HttpSession;

@Service
public class OnboardServiceImpl implements OnboardService {
	private static final Logger logger = LoggerFactory.getLogger(OnboardServiceImpl.class);
	@Autowired
    private UserDetailsManager userDetailsManager;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private UserRepository userRepository;
	
    @Autowired
	public ModelMapper mapper;
	
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

	@Override
    public ResponseDto addUser(String username, String password) {
		ResponseDto response = new ResponseDto();
        // Check if the user already exists in the system
        if (userRepository.findUserByUsername(username).isPresent()) {
        	logger.info("username already exists",username);
            //map to /register and 
        	response.setStatusCode(StatusResponse.FAILURE_STATUS_CODE);
        	return response;
        	//throw new IllegalArgumentException("User with username already exists: " + username);
            
        }

        // Create the new user
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setAccountNonLocked(true);
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

} //end class