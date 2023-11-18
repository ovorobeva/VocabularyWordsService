package com.github.ovorobeva.vocabularywordsservice.service.impl;

import com.github.ovorobeva.vocabularywordsservice.service.PartsOfSpeechService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PartsOfSpeechServiceImplITest {

    @Autowired
    private PartsOfSpeechService partsOfSpeechService;

    @Test
    void getPartsOfSpeechTestNoun() {
        assertThat(partsOfSpeechService.getPartsOfSpeech("word")).contains("noun");
    }

    @Test
    void getPartsOfSpeechTestVerb() {
        assertThat(partsOfSpeechService.getPartsOfSpeech("read")).contains("verb");
    }

    @Test
    void getPartsOfSpeechTestAdjective() {
        assertThat(partsOfSpeechService.getPartsOfSpeech("beautiful")).contains("adjective");
    }

    @Test
    void getPartsOfSpeechTestNull() {
        assertThat(partsOfSpeechService.getPartsOfSpeech("non existing word")).isNull();
    }
}
//todo: not integration test