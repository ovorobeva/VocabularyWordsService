package com.gitjub.ovorobeva.vocabularywordsservice.emailsender;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailSender{
    @Autowired
    private JavaMailSender emailSender;

    public void sendSimpleMessage (String subject, String text) throws MailSendException {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@vocabularywordsservice.com");
        message.setTo("passant.dlm@gmail.com");
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }
}
