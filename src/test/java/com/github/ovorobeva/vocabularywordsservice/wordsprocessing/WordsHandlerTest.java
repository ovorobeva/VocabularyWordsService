package com.github.ovorobeva.vocabularywordsservice.wordsprocessing;

import com.github.ovorobeva.vocabularywordsservice.model.generated.GeneratedWordsDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
class WordsHandlerTest {

    @Autowired
    WordsHandler wordsHandler;
    @Test
    void getProcessedWords() throws InterruptedException {
        final Random random = new Random();
        int count = random.nextInt(10) + 1;
        int lastCode = random.nextInt(10);
        List<GeneratedWordsDto> wordList = new ArrayList<>();
        wordsHandler.getProcessedWords(wordList, count, lastCode);
        assertThat(wordList).hasSize(count);
        assertThat(wordList.get(count - 1).getCode()).isEqualTo(lastCode + count - 1);
        assertThat(wordList.get(random.nextInt(lastCode + count) + lastCode).getFr()).isNotNull();
        assertThat(wordList.get(random.nextInt(lastCode + count) + lastCode).getRu()).isNotNull();
        assertThat(wordList.get(random.nextInt(lastCode + count) + lastCode).getCz()).isNotNull();
        assertThat(wordList.get(random.nextInt(lastCode + count) + lastCode).getEn()).isNotNull();
    }
}