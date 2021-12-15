package com.gitjub.ovorobeva.vocabularywordsservice.emailsender;

import com.dumbster.smtp.SimpleSmtpServer;
import com.dumbster.smtp.SmtpMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DirtiesContext
class EmailSenderTest {
    @Autowired
    private EmailSender emailSender;

    @Test
    public void testEmail() throws InterruptedException, MessagingException {
        try {
            SimpleSmtpServer server = SimpleSmtpServer.start(3025);
            emailSender.sendSimpleMessage("test subject", "test message", true);
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