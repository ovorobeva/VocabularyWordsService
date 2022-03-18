package com.github.ovorobeva.vocabularywordsservice.emailsender;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@Slf4j
public class EmailSender {
    @Value("${spring.mail.username}")
    public String TO;

    @Autowired
    private JavaMailSenderImpl emailSender;

    public void sendSimpleMessage(String subject, String text) throws MailSendException {
        SimpleMailMessage message = new SimpleMailMessage();
        final String FROM = "noreply@vocabularywordsservice.com";
        message.setFrom(FROM);
        message.setTo(TO);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
        log.info( "Email has been sent to " + TO);
        log.debug("Message subject is: " + message.getSubject() +
                " Message text is: " + message.getText() +
                " Message from: " + message.getFrom() +
                " Message to: " + Arrays.toString(message.getTo()) +
                " Has been sent");

    }
}
