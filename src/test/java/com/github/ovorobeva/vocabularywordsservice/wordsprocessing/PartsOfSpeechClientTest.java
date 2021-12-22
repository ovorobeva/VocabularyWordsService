package com.github.ovorobeva.vocabularywordsservice.wordsprocessing;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
class PartsOfSpeechClientTest {
    @Autowired
    PartsOfSpeechClient partsOfSpeechClient;

    @Test
    void getPartsOfSpeechTestNoun() throws InterruptedException {
        assertThat(partsOfSpeechClient.getPartsOfSpeech("word")).contains("noun");
    }

    @Test
    void getPartsOfSpeechTestVerb() throws InterruptedException {
        assertThat(partsOfSpeechClient.getPartsOfSpeech("read")).contains("verb");
    }

    @Test
    void getPartsOfSpeechTestAdjective() throws InterruptedException {
        assertThat(partsOfSpeechClient.getPartsOfSpeech("smart")).contains("adjective");
    }

    @Test
    void getPartsOfSpeechTestNull() throws InterruptedException {
        assertThat(partsOfSpeechClient.getPartsOfSpeech("non existing word")).isNull();
    }
}
