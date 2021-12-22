package com.github.ovorobeva.vocabularywordsservice.wordsprocessing;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
class WordsClientTest {
    @Autowired
    WordsClient wordsClient;


    @Test
    void getRandomWordsTest() throws InterruptedException {
        final Random random = new Random();
        int count = random.nextInt(10) + 1;
        assertThat(wordsClient.getRandomWords(count)).hasSize(count);
    }
}