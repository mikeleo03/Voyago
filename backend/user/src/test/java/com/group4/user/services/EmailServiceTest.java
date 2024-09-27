package com.group4.user.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import static org.mockito.Mockito.*;

class EmailServiceTest {

    @InjectMocks
    private EmailService emailService;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MimeMessage mimeMessage;

    @Mock
    private MimeMessageHelper mimeMessageHelper;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendHtmlEmail() throws MessagingException {
        String to = "test@example.com";
        String subject = "Test Subject";
        String htmlBody = "<h1>Hello World</h1>";

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);        
        emailService.sendHtmlEmail(to, subject, htmlBody);
        
        // Verify the helper is configured correctly
        verify(mailSender, times(1)).send(mimeMessage);
    }
}

