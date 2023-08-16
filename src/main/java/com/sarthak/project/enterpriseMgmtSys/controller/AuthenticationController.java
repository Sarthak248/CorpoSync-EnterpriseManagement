package com.sarthak.project.enterpriseMgmtSys.controller;

import java.security.Principal;
import java.util.Map;
import java.util.Arrays;	
import javax.servlet.http.HttpServletRequest; 
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping; 
import org.springframework.web.bind.annotation.RequestParam;

import com.sarthak.project.enterpriseMgmtSys.GenericClass.ResponseDto;
import com.sarthak.project.enterpriseMgmtSys.GenericClass.StatusResponse;
import com.sarthak.project.enterpriseMgmtSys.payload.UserDto;
import com.sarthak.project.enterpriseMgmtSys.service.AuthenticationService;

@Controller 
public class AuthenticationController {
   @Autowired private AuthenticationService onboardService;
   
   private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);
   
   //THYMELEAF   
   @GetMapping("/")
   public String redirect(HttpServletRequest request, HttpSession session,
				Model model) {
       Principal principal = request.getUserPrincipal();
       if(principal!=null) {
    	   logger.info("In the principal not null stmt");
    	   UserDto user = (UserDto) session.getAttribute("verifiedUser");
    	   logger.info(String.format("verified user from session : %s", user.getUsername()));
    	   model.addAttribute("username", user.getUsername());
		   model.addAttribute("justLoggedIn", true);
    	   return "index";
       }
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
		   Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	        if (authentication != null && authentication.isAuthenticated()) {
	            // Get the username from the authentication object
	            String username = authentication.getName();
	            // Set the username as a session attribute
	            session.setAttribute("verifiedUser", username);
	            logger.info(String.format("authcontr:post:login:verified user name : %s", username));
	        }
	        return "index";
	    }
	    return "index";
	}
   
   
   @GetMapping("/register") 
   public String showRegister(HttpServletRequest request, HttpSession session) {
	   logger.info("I'm at register get map");
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
		   				Model model,
		   				HttpSession session) {
	   logger.info("at /register post, passed map of string, string");
       ResponseDto response = onboardService.addUser(body.get("username"), body.get("password"), body.get("email"));
       if(response.getStatusCode().equalsIgnoreCase(StatusResponse.FAILURE_STATUS_CODE)) {
    	   if (response.getMessage().equalsIgnoreCase(StatusResponse.USERNAME_ALREADY_EXISTS)) {
    		   //user already exists
        	   logger.info("Controller now knows that user already exists");
//        	   model.addAttribute("nameExists", true);
//        	   return "register";
    	   } else if (response.getMessage().equalsIgnoreCase(StatusResponse.EMAIL_INVALID_FORMAT)) {
    		   logger.info("Controller now knows that email is of invalid format");
        	   model.addAttribute("invalidEmailFormat", true);
        	   return "register";    		   
    	   } else if (response.getMessage().equalsIgnoreCase(StatusResponse.EMAIL_ALREADY_EXISTS)) {
    		   logger.info("Controller now knows that email already exists");
        	   model.addAttribute("alreadyRegistered", true);
        	   return "register";    		   
    	   } else if(response.getMessage().equalsIgnoreCase(StatusResponse.NAME_INVALID_FORMAT)) {
    		   logger.info("Controller now knows that user is of invalid format");
        	   model.addAttribute("invalidNameFormat", true);
        	   return "register";    		   
    	   }
    	   
       }
       UserDto user = (UserDto) response.getResult();
       logger.info(user.getUsername());
       model.addAttribute("username", user.getUsername());
       model.addAttribute("justRegistered", true);
       session.setAttribute("verifiedUser", user);
       
       Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
       UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(authentication.getPrincipal(), authentication.getCredentials(), Arrays.asList(new SimpleGrantedAuthority("RESET_PASSWORD")));
       SecurityContextHolder.getContext().setAuthentication(newAuth);

       return "index";
   }

   @GetMapping("/forgot")
   public String showForgotPage(HttpServletRequest request) {
       logger.info("at @Get /forgot");
       String acceptHeader = request.getHeader("Accept");
       if (acceptHeader != null && acceptHeader.contains("text/html")) {
           return "forgot"; // Return the Thymeleaf view name
       } else {
           return "forward:/api/forgot"; // If it's an API request
       }
   }

   
   @PostMapping("/forgot") 
   public String forgotPassword(@RequestParam("username") String username, Model model, HttpSession session) { 
	   logger.info("at /forgot post, passed map of string, string");
       ResponseDto response = onboardService.forgotPass(username);
       if(response.getStatusCode().equalsIgnoreCase(StatusResponse.FAILURE_STATUS_CODE)) {
    	   //user doesn't exist
    	   logger.info("Controller now knows that user doesn exist");
    	   model.addAttribute("noUser", true);
    	   return "forgot";
       }
       UserDto userDto = (UserDto) response.getResult();
       logger.info(userDto.getUsername());
       logger.info(String.format("user's email is: %s", userDto.getEmail()));
       model.addAttribute("username", userDto.getUsername());
       model.addAttribute("encodedEmail", userDto.getEncodedEmail());
       model.addAttribute("proceedToVerify", true);
       session.setAttribute("verifiedUser", userDto);
       
    // After username verification
	   Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	   UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(authentication.getPrincipal(), authentication.getCredentials(), Arrays.asList(new SimpleGrantedAuthority("VERIFY_EMAIL")));
	   SecurityContextHolder.getContext().setAuthentication(newAuth);

       return "forgot";
       
   }
   
   @PostMapping("/verify") 
   public String verifyEmail(@RequestParam("cem") String email, HttpSession session, Model model) { 
	   logger.info("at /forgot post, passed map of string, string");
	   UserDto user = (UserDto) session.getAttribute("verifiedUser");
       ResponseDto response = onboardService.verifyEmail(user, email);
       if(response.getStatusCode().equalsIgnoreCase(StatusResponse.FAILURE_STATUS_CODE)) {
    	   //emails dont match
    	   logger.info("Controller now knows that user doesn exist");
    	   model.addAttribute("noMatch", true);
    	   return "forgot";
       }
    // After email verification
       Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
       UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(authentication.getPrincipal(), authentication.getCredentials(), Arrays.asList(new SimpleGrantedAuthority("RESET_PASSWORD")));
       SecurityContextHolder.getContext().setAuthentication(newAuth);

       session.setAttribute("verifiedUser", user);
       return "reset";       
   }
   
   @GetMapping("/redirectToReset")
   public String showResetPage(HttpServletRequest request) {
	   Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
       UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(authentication.getPrincipal(), authentication.getCredentials(), Arrays.asList(new SimpleGrantedAuthority("RESET_PASSWORD")));
       SecurityContextHolder.getContext().setAuthentication(newAuth);
       return "reset";
   }
   
   @PostMapping("/reset") 
   public String resetPassword(@RequestParam("new") String newPassword, @RequestParam("confirm") String confirmPassword,
		   HttpSession session, Model model) { 
	   logger.info("at /reset post, passed string, string");
	   
	   UserDto user = (UserDto) session.getAttribute("verifiedUser");
	   logger.info(String.format("authController:post:reset:verifiedUser: name : %s", user.getUsername()));
       ResponseDto response = onboardService.resetPass(user, newPassword, confirmPassword);
       if(response.getStatusCode().equalsIgnoreCase(StatusResponse.FAILURE_STATUS_CODE)) {
    	   //passwords dont match
    	   logger.info("pass dont match (controller)");
    	   model.addAttribute("unmatchedPass", true);
    	   return "reset";
       }
       UserDto updatedUser = (UserDto) response.getResult();
       session.setAttribute("verifiedUser", updatedUser);
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