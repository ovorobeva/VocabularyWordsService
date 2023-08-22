package com.github.ovorobeva.vocabularywordsservice.wordsprocessing;

import com.github.ovorobeva.vocabularywordsservice.clients.PartsOfSpeechClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PartsOfSpeechClientTest {
    @Autowired
    PartsOfSpeechClient partsOfSpeechClient;
/*
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
        assertThat(partsOfSpeechClient.getPartsOfSpeech("beautiful")).contains("adjective");
    }

    @Test
    void getPartsOfSpeechTestNull() throws InterruptedException {
        assertThat(partsOfSpeechClient.getPartsOfSpeech("non existing word")).isNull();
    }*/
}
