package com.github.ovorobeva.vocabularywordsservice.wordsprocessing;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
class LemmaClientTest {

    @LocalServerPort
    private int port;

    @Autowired
    private LemmaClient lemmaClient;

    @Test
    void getLemma() {
        assertThat(lemmaClient.getLemma("words")).isEqualTo("word");
    }
}