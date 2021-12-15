package com.gitjub.ovorobeva.vocabularywordsservice.emailsender;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

@Component
public class EmailSender {
    public final String TO = "passant.dlm@gmail.com";
    private final String FROM = "noreply@vocabularywordsservice.com";
    @Autowired
    private JavaMailSenderImpl emailSender;

    public void sendSimpleMessage(String subject, String text, boolean isTest) throws MailSendException {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(FROM);
        message.setTo(TO);
        message.setSubject(subject);
        message.setText(text);
        if (isTest){
            emailSender.setPort(3025);
            emailSender.setHost("localhost");
        }
        emailSender.send(message);
    }

    public void sendSimpleMessage(String subject, String text) throws MailSendException {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(FROM);
        message.setTo(TO);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }
}
