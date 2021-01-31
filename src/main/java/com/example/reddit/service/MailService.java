package com.example.reddit.service;

import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import com.example.reddit.exceptions.SpringRedditException;
import com.example.reddit.model.NotificationEmail;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j   //->for log.info
public class MailService {
    
	private final JavaMailSender mailSender;
	private final MailContentBuilder mailContentBuilder;
	void sendMail(NotificationEmail notificationEmail) {
		 MimeMessagePreparator messagePreparator = mimeMessage -> {
	            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
	            messageHelper.setFrom("springreddit@email.com");
	            messageHelper.setTo(notificationEmail.getRecipient());
	            messageHelper.setSubject(notificationEmail.getSubject());
	            messageHelper.setText(notificationEmail.getBody());
	        };
	        
	        try {
	            mailSender.send(messagePreparator);
	            log.info("Activation email sent!!");
	        } catch (MailException e) {
	            log.error("Exception occurred when sending mail", e);
	            throw new SpringRedditException("Exception occurred when sending mail to " + notificationEmail.getRecipient());
	        }
	}
}