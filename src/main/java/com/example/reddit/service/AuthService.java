package com.example.reddit.service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.reddit.dto.RegisterRequest;
import com.example.reddit.exceptions.SpringRedditException;
import com.example.reddit.model.NotificationEmail;
import com.example.reddit.model.User;
import com.example.reddit.model.VerificationToken;
import com.example.reddit.repository.UserRepository;
import com.example.reddit.repository.VerificationTokenRepository;

@Service
public class AuthService {
    
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository  userRepository; 
	
	@Autowired 
	private VerificationTokenRepository verificationTokenRepository;
	
	@Autowired
	private MailService mailService;
	
	@Transactional
	public void signup(RegisterRequest registerRequest) {
		User user = new User();
		user.setUsername(registerRequest.getUsername());
		user.setEmail(registerRequest.getEmail());
		user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
		user.setCreated(Instant.now());
		user.setEnabled(false);
		
		userRepository.save(user);
		
		String token = generateVerificationToken(user);
		
		mailService.sendMail(new NotificationEmail("Please Activate your Account",
                user.getEmail(), "Thank you for signing up to Spring Reddit, " +
                "please click on the below url to activate your account : " +
                "http://localhost:8080/api/auth/accountVerification/" + token));
	}

	private String generateVerificationToken(User user) {
		// TODO Auto-generated method stub
		String token = UUID.randomUUID().toString();
		VerificationToken verificationToken = new VerificationToken();
		verificationToken.setToken(token);
		verificationToken.setUser(user);
		verificationTokenRepository.save(verificationToken);
		
		return token;
	}

	public void verifyAccount(String token) {
		// TODO Auto-generated method stub
		Optional<VerificationToken> verificationToken = verificationTokenRepository.findByToken(token);
		verificationToken.orElseThrow(()-> new SpringRedditException("Invalid Token"));
		fetchUserAndEnable(verificationToken.get());
		
		
	}

	@Transactional
	private void fetchUserAndEnable(VerificationToken verificationToken) {
		// TODO Auto-generated method stub
		String username = verificationToken.getUser().getUsername();
		User user = userRepository.findByUsername(username).orElseThrow(()-> new SpringRedditException("user not found with name- "+username));
		user.setEnabled(true);
		userRepository.save(user);
	}
}
