package com.github.ovorobeva.vocabularywordsservice.emailsender;

import com.dumbster.smtp.SimpleSmtpServer;
import com.dumbster.smtp.SmtpMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class EmailSenderTest {
    @Autowired
    private EmailSender emailSender;

    @Test
    public void testEmail() {
        try {
            SimpleSmtpServer server = SimpleSmtpServer.start(3025);
            emailSender.sendSimpleMessage("test subject", "test message");
            List<SmtpMessage> emails = server.getReceivedEmails();
            server.stop();
            assertThat(emails).hasSize(1);
            SmtpMessage email = emails.get(0);
            assertThat(email.getHeaderValue("Subject")).isEqualTo("test subject");
            assertThat(email.getBody()).isEqualTo("test message");
            assertThat(email.getHeaderValue("To")).isEqualTo(emailSender.TO);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}