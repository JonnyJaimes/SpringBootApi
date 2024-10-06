package com.ufps.maestria.security.services;

import com.ufps.maestria.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(User user, String siteURL) {
        String toAddress = user.getEmail();
        String subject = "Please verify your registration";

        String content = "<html><body>" +
                "<h2>Dear " + user.getUsername() + ",</h2>" +
                "<p>Thank you for registering. Please click the link below to verify your registration:</p>" +
                "<p><a href='" + siteURL + "/api/auth/verify?token=" + user.getVerificationToken() +
                "' style='background-color: #4CAF50; color: white; padding: 10px 20px; text-decoration: none;'>VERIFY</a></p>" +
                "<p>Thank you,<br>Your Company Name</p>" +
                "</body></html>";

        try {
            sendHtmlEmail(toAddress, subject, content);
        } catch (MessagingException e) {
            // Log the exception to avoid application crashes
            e.printStackTrace();
        }
    }

    private void sendHtmlEmail(String toAddress, String subject, String content) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(toAddress);
        helper.setSubject(subject);
        helper.setText(content, true);

        mailSender.send(message);
    }
}

