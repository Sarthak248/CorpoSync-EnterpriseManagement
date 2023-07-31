package com.sarthak.project.enterpriseMgmtSys.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.security.core.userdetails.UserDetails; 
import org.springframework.security.core.userdetails.UserDetailsService; 
import org.springframework.security.core.userdetails.UsernameNotFoundException; 
import org.springframework.security.provisioning.UserDetailsManager; 
import org.springframework.stereotype.Service; 
import com.sarthak.project.enterpriseMgmtSys.entity.User; 
import com.sarthak.project.enterpriseMgmtSys.repository.UserRepository; 

@Service
public class SecurityUserDetailsService implements UserDetailsService { 
private static final Logger logger = LoggerFactory.getLogger(SecurityUserDetailsService.class);

   @Autowired 
   private UserRepository userRepository; 
   
   @Override 
   public UserDetails loadUserByUsername(String username) 
   throws UsernameNotFoundException { 
	  logger.info("load by username from secUserDetServ is called");
      User user = userRepository.findUserByUsername(username) 
         .orElseThrow(() -> new UsernameNotFoundException("User not present")); 
         return user; 
   } 
   public void createUser(UserDetails user) { 
	   logger.info("createUser from secUserDetServ is called");
      userRepository.save((User) user); 
   } 
}