package com.github.ovorobeva.vocabularywordsservice.wordsprocessing;

import com.github.ovorobeva.vocabularywordsservice.clients.LemmaClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LemmaClientTest {

    @Autowired
    private LemmaClient lemmaClient;

    @Test
    void getLemmaTest() {
        assertThat(lemmaClient.getLemma("words")).isEqualTo("word");
    }

    @Test
    void getLemmaOfSeldomWordTest() {
        assertThat(lemmaClient.getLemma("seldomwords")).isEqualTo(LemmaClient.SELDOM_WORD);
    }
}