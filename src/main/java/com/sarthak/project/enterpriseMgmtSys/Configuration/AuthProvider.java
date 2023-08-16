package com.sarthak.project.enterpriseMgmtSys.Configuration;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.security.authentication.AuthenticationProvider; 
import org.springframework.security.authentication.BadCredentialsException; 
import org.springframework.security.authentication.LockedException; 
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; 
import org.springframework.security.core.Authentication; 
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder; 
import org.springframework.stereotype.Component;

import com.sarthak.project.enterpriseMgmtSys.entity.Attempts; 
import com.sarthak.project.enterpriseMgmtSys.entity.User; 
import com.sarthak.project.enterpriseMgmtSys.repository.AttemptsRepository;
import com.sarthak.project.enterpriseMgmtSys.repository.UserRepository;
import com.sarthak.project.enterpriseMgmtSys.service.SecurityUserDetailsService;

@Component
public class AuthProvider implements AuthenticationProvider {
	private static final Logger logger = LoggerFactory.getLogger(AuthProvider.class);

    private static final int ATTEMPTS_LIMIT = 3;

    @Autowired
    private SecurityUserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AttemptsRepository attemptsRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        logger.info("obtained username at auth provider ", username);
        User user = userRepository.findUserByUsername(username).orElse(null);
        
        
        //UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (user==null) {
        	logger.info("user not present-");
            throw new UsernameNotFoundException("no such user");
        } else if(!user.getAccountNonLocked()) {
        	logger.info("this user is blocked");
        	throw new LockedException("User is locked. Contact support to unfreeze your account");
        }else if (passwordEncoder.matches(password, user.getPassword())) {
            // Passwords match, reset login attempts
        	logger.info("matched password");
            resetLoginAttempts(username);
            logger.info("resetLoginAttempts");
            return new UsernamePasswordAuthenticationToken(user, password, user.getAuthorities());
        }else {
            // Passwords don't match, handle failed attempts
        	logger.info("pass dont match, called failed attempts");
            processFailedAttempts(username, user);
            throw new BadCredentialsException("Invalid username and password!");
        }
    }

    private void resetLoginAttempts(String username) {
        Optional<Attempts> userAttempts = attemptsRepository.findAttemptsByUsername(username);
        if (userAttempts.isPresent()) {
            Attempts attempts = userAttempts.get();
            attempts.setAttempts(0);
            attemptsRepository.save(attempts);
        }
    }

    private void processFailedAttempts(String username, UserDetails userDetails) {
    	logger.info("in failed attempts method");
        Attempts userAttempts = attemptsRepository.findAttemptsByUsername(username).orElse(null);
        if (userAttempts==null) {
        	logger.info("user attempts is null");
            Attempts attempts = new Attempts();
            attempts.setUsername(username);
            attempts.setAttempts(1);
            attemptsRepository.save(attempts);
            logger.info("attempt succ saved in attempts repo");
        } else {
            userAttempts.setAttempts(userAttempts.getAttempts() + 1);
            logger.info("incremented attempts in attempt repo");
            attemptsRepository.save(userAttempts);
            logger.info("saved the increment");

            if (userAttempts.getAttempts() + 1 > ATTEMPTS_LIMIT) { //no fourth attempt
            	logger.info("greater than limit");
                User user = userRepository.findUserByUsername(username).orElse(null);
                if (user != null) {
                    user.setAccountNonLocked(false);
                    logger.info("locked user");
                    userRepository.save(user);
                    logger.info("saved locked user in user repo");
                }
                throw new LockedException("Too many invalid attempts. Account is locked!!");
            }
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}