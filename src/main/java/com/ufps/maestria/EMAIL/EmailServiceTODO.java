package com.bezkoder.springjwt.EMAIL;

import com.bezkoder.springjwt.security.services.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class EmailServiceTODO implements EmailSender{


    private static final String EMAIL_FROM_MSG = "test@email.com";
    private static final String CONFIRM_YOUR_EMAIL_MSG = "Please confirm your email";
    private static final String FAILED_TO_SEND_EMAIL_MSG = "failed to send email";
    private final static Logger LOGGER = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender mailSender;

    @Override
    @Async
    public void send(String to, String email) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(email, true);
            helper.setTo(to);
            helper.setSubject(CONFIRM_YOUR_EMAIL_MSG);
            helper.setFrom(EMAIL_FROM_MSG);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            LOGGER.error(FAILED_TO_SEND_EMAIL_MSG, e);
            throw new IllegalStateException(FAILED_TO_SEND_EMAIL_MSG);
        }
    }

    public void sendListEmail(@NotEmpty(message = "El correo personal no puede estar vacío") @Email(message = "El formato del correo personal no es válido") String correoPersonal, String s, String mensaje) {

    }
}