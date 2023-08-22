package com.github.ovorobeva.vocabularywordsservice.wordsprocessing;

import com.github.ovorobeva.vocabularywordsservice.clients.WordsClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WordsClientTest {

    @Autowired
    private WordsClient wordsClient;

/*    @Test
    void getRandomWordsTest() throws InterruptedException {
        final Random random = new Random();
        int count = random.nextInt(10) + 1;
        assertThat(wordsClient.getWords(count)).hasSize(count);
    }*/
}