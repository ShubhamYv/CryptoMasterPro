package com.trading.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.trading.constants.MailConstants;
import com.trading.service.EmailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService {

	@Autowired
	private JavaMailSender javaMailSender;

	@Override
	public void sendVerificationOtpEmail(String email, String otp) throws MessagingException {

		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, "utf-8");

		String subject = MailConstants.SUBJECT;
		String text = MailConstants.TEXT + otp;

		messageHelper.setSubject(subject);
		messageHelper.setText(text, true);
		messageHelper.setTo(email);

		try {
			javaMailSender.send(mimeMessage);
		} catch (Exception e) {
			throw new MailSendException(e.getMessage());
		}
	}

}
