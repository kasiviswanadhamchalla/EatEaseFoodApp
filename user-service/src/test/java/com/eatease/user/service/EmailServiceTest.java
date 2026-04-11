package com.eatease.user.service;

import org.junit.jupiter.api.Test;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class EmailServiceTest {

    @Test
    void sendOtpEmailShouldComposeAndSendMessage() {
        JavaMailSender mailSender = mock(JavaMailSender.class);
        EmailService emailService = new EmailService(mailSender);

        emailService.sendOtpEmail("otp@example.com", "123456");

        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendOtpEmailShouldContainExpectedBody() {
        JavaMailSender mailSender = mock(JavaMailSender.class);
        EmailService emailService = new EmailService(mailSender);

        emailService.sendOtpEmail("otp@example.com", "654321");

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());
        SimpleMailMessage actual = messageCaptor.getValue();
        assertEquals("otp@example.com", actual.getTo()[0]);
        assertEquals("EatEase OTP Verification", actual.getSubject());
        assertEquals("Your OTP is: 654321\nValid for 5 minutes.", actual.getText());
    }
}
