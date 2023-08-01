package com.sarthak.project.enterpriseMgmtSys.controller;

import java.security.Principal;
import java.util.Map;
import javax.servlet.http.HttpServletRequest; 
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping; 
import org.springframework.web.bind.annotation.RequestParam;

import com.sarthak.project.enterpriseMgmtSys.GenericClass.ResponseDto;
import com.sarthak.project.enterpriseMgmtSys.GenericClass.StatusResponse;
import com.sarthak.project.enterpriseMgmtSys.payload.UserDto;
import com.sarthak.project.enterpriseMgmtSys.service.OnboardService;

@Controller 
public class OnboardController {
   @Autowired private OnboardService onboardService;
   
   private static final Logger logger = LoggerFactory.getLogger(OnboardController.class);
   //Total of 8 methods: 4 in Thymeleaf and Postman each.
   //2 of the 4 are GetMapping that redirect to the other 2 Postmapping
   
   //THYMELEAF   
   @GetMapping("/")
   public String redir(HttpServletRequest request, HttpSession session,
				Model model) {
       Principal principal = request.getUserPrincipal();
       if(principal!=null)
    	   return "index";
	   return "forward:/login";
	    
   } 
   
   @GetMapping("/login")
   public String showLoginPage(HttpServletRequest request, HttpSession session,
		   						Model model) {
	   logger.info("at @Get /login");
	   String acceptHeader = request.getHeader("Accept");
	    if (acceptHeader != null && acceptHeader.contains("text/html")) {
	        // Return the Thymeleaf template for the browser request
	    	logger.info("session setting attribute");
	    	session.setAttribute("error", onboardService.getErrorMessage(request, "SPRING_SECURITY_LAST_EXCEPTION"));
//	    	model.addAttribute("justLoggedIn", true);
	        return "login"; 
	    } else {
	    	logger.info("forwarding to @Post api/login from @Get /login");
	        return "forward:/api/login";
	    }
   } 
   
   @PostMapping("/login") 
   public String login(HttpServletRequest request, HttpSession session,
		   				Model model) { 
	   logger.info("At /login post, passed request and session");
	   if(onboardService.login(request, session, model).equalsIgnoreCase("index")) {
		   logger.info("reached the if to check string");
		   model.addAttribute("justLoggedIn", true);
		   return "index";
	   }
	   return "index";
       
   }
   
   
   @GetMapping("/register") 
   public String showRegister(HttpServletRequest request, HttpSession session) { 
	   String acceptHeader = request.getHeader("Accept");
	    if (acceptHeader != null && acceptHeader.contains("text/html")) {
	    	logger.info("forwarding to thymeleaf @Post register");
	        return "register";
	    } else {
	    	logger.info("forwarding to @Post api/register from @Get /register");
	        return "forward:/api/register";
	    }
	   
	   
	  
   }  
   
   @PostMapping(
      value = "/register", 
      consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, 
      produces = {
    		  MediaType.APPLICATION_ATOM_XML_VALUE, MediaType.APPLICATION_JSON_VALUE
    		  }
	)
   
   public String addUser(@RequestParam Map<String, String> body,
		   				Model model) {
	   logger.info("at /register post, passed map of string, string");
       ResponseDto response = onboardService.addUser(body.get("username"), body.get("password"));
       if(response.getStatusCode().equalsIgnoreCase(StatusResponse.FAILURE_STATUS_CODE)) {
    	   //user already exists
    	   logger.info("Controller now knows that user already exists");
    	   model.addAttribute("alreadyRegistered", true);
    	   return "register";
       }
       UserDto user = (UserDto) response.getResult();
       logger.info(user.getUsername());
       model.addAttribute("username", user.getUsername());
       model.addAttribute("justRegistered", true);
       return "index";
   }

   
   //SUCCESS REDIRECT
   @GetMapping("/api/login/redirect")
   public String loginRedirect(HttpServletRequest request, HttpSession session) {
	   logger.info("Reached redirect at login");
	   String acceptHeader = request.getHeader("Accept");
	    if (acceptHeader != null && acceptHeader.contains("text/html")) {
	        return "forward:/postLogin"; 
	    } else {
	    	logger.info("forwarding to @Post api/login/suc from redirect method");
	        return "/api/login/success";
	    }
   }
   @GetMapping("/api/register/redirect")
   public String registerRedirect(HttpServletRequest request, HttpSession session) {
	   logger.info("Reached redirect at reg");
	   String acceptHeader = request.getHeader("Accept");
	    if (acceptHeader != null && acceptHeader.contains("text/html")) {
	        return "forward:/postLogin"; 
	    } else {
	    	logger.info("forwarding to @Post api/login/suc from redirect method");
	        return "/api/login/success";
	    }
   }
   
   
   //POSTMAN
   @GetMapping("/api/login")
   public ResponseEntity<?> loginApi(HttpServletRequest request, HttpSession session) {
       // response dto contatins status, message and object as user details
	   logger.info("Succesfully redirected");
	   ResponseDto response = onboardService.loginApi(request, session);
       return new ResponseEntity<ResponseDto>(response, HttpStatus.OK);
   }
   
   
   @GetMapping("/api/register")
   public ResponseEntity<?> registerApi(HttpServletRequest request, HttpSession session){
	   ResponseDto response = new ResponseDto();
	   return new ResponseEntity<ResponseDto>(response, HttpStatus.OK);   
	   
   }


}