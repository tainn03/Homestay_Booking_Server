package com.homestay.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class EmailService {
    JavaMailSender mailSender;

    public CompletableFuture<Void> sendEmail(String to, String email) {
        return CompletableFuture.runAsync(() -> {
            try {
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
                helper.setText(email, true);
                helper.setTo(to);
                helper.setSubject("Confirm your email");
                helper.setFrom("homestay.huta@gmail.com");
                mailSender.send(mimeMessage);
            } catch (MessagingException e) {
                log.error("failed to send email", e);
                throw new IllegalStateException("failed to send email");
            }
        });
    }
}
