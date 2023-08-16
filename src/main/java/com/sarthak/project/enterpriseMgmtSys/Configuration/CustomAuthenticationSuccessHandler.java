package com.sarthak.project.enterpriseMgmtSys.Configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.sarthak.project.enterpriseMgmtSys.payload.UserDto;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
	private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationSuccessHandler.class);
	
    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {
        // Get the username from the authentication object
        String username = authentication.getName();
        UserDto userDto = new UserDto();
        userDto.setUsername(username);
        logger.info(String.format("Username in the custAuthSucHand is : %s",userDto.getUsername()));
        // Set the username as a session attribute
        HttpSession session = request.getSession();
        session.setAttribute("verifiedUser", userDto);

        // Redirect to your desired page after successful authentication
        response.sendRedirect("/"); // Change this to your desired page
    }
}
