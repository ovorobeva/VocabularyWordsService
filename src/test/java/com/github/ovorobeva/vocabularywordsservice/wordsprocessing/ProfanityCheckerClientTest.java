package com.github.ovorobeva.vocabularywordsservice.wordsprocessing;

import com.github.ovorobeva.vocabularywordsservice.clients.ProfanityCheckerClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProfanityCheckerClientTest {

    @Autowired
    ProfanityCheckerClient profanityCheckerClient;

    @Test
    void isProfanityTestTrue() {
        assertThat(profanityCheckerClient.isProfanity("shit").getBody()).isEqualTo("true");
    }
    @Test
    void isProfanityTestFalse() {
        assertThat(profanityCheckerClient.isProfanity("word").getBody()).isEqualTo("false");
    }
    @Test
    void isProfanityTestNonExists() {
        assertThat(profanityCheckerClient.isProfanity("non existing word").getBody()).isEqualTo("false");
    }
}