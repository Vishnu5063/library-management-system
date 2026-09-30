package com.library.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOtpEmail(String recipientEmail, String otp) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(recipientEmail);
        message.setSubject("Library Management System - OTP");
        message.setText(
                "Your Library Management System OTP is: " + otp
                        + "\n\n"
                        + "This OTP is valid for 5 minutes."
                        + "\n"
                        + "Please do not share this OTP with anyone.");

        mailSender.send(message);
    }
}