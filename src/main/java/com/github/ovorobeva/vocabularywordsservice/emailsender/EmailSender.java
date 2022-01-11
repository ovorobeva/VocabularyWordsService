package com.github.ovorobeva.vocabularywordsservice.emailsender;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

@Component
public class EmailSender {
    @Value("${spring.mail.username}")
    public String TO;

    @Autowired
    private JavaMailSenderImpl emailSender;

    public void sendSimpleMessage(String subject, String text) throws MailSendException {
        SimpleMailMessage message = new SimpleMailMessage();
        String FROM = "noreply@vocabularywordsservice.com";
        message.setFrom(FROM);
        message.setTo(TO);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }
}
