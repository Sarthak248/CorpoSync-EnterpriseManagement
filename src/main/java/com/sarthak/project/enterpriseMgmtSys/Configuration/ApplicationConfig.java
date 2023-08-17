package com.sarthak.project.enterpriseMgmtSys.Configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean; 
import org.springframework.context.annotation.Configuration; 
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter; 
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; 
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration 
public class ApplicationConfig extends WebSecurityConfigurerAdapter { 
private static final Logger logger = LoggerFactory.getLogger(ApplicationConfig.class);
	@Autowired
	private CustomAuthenticationSuccessHandler authenticationSuccessHandler;

   @Bean 
   public PasswordEncoder passwordEncoder() { 
      return new BCryptPasswordEncoder(); 
   } 
   @Override 
   protected void configure(HttpSecurity http) throws Exception {
       logger.info("started appConfig");
       http 
           .csrf().disable() // disabling cross-site request forgery protection
           .authorizeRequests()
               .antMatchers("/register**", "/forgot**", "/member**").permitAll()
               .antMatchers("/verify").hasAuthority("VERIFY_EMAIL")
               .antMatchers("/reset").hasAuthority("RESET_PASSWORD")
               .antMatchers("/mobileverify").hasAuthority("MEMBER_LOGIN")
               .anyRequest().authenticated()
           .and() 
               .formLogin() 
               .loginPage("/login").permitAll() 
               .successHandler(authenticationSuccessHandler) // Set the custom success handler
           .and() 
               .logout() 
               .invalidateHttpSession(true) // helps prevent session fixation attacks.
               .clearAuthentication(true).permitAll(); 
       logger.info("ended appConfig");
   }

}
