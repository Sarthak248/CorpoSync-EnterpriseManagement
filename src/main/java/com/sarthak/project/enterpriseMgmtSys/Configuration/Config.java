package com.sarthak.project.enterpriseMgmtSys.Configuration;

import org.springframework.http.MediaType;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


//import org.springframework.security.crypto.password;
@Configuration
public class Config implements WebMvcConfigurer {
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.favorParameter(false)
	        .defaultContentType(MediaType.APPLICATION_JSON)
	        .mediaType("json", MediaType.APPLICATION_JSON)
	        .mediaType("text", MediaType.TEXT_PLAIN);
    }
    
    @Bean
    public UserDetailsManager userDetailsManager() {
        return new InMemoryUserDetailsManager();
    }
    
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}