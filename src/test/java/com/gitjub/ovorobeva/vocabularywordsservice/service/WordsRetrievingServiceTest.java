package com.gitjub.ovorobeva.vocabularywordsservice.service;

import com.gitjub.ovorobeva.vocabularywordsservice.model.generated.GeneratedWordsDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
class WordsRetrievingServiceTest {

    @Autowired
    private WordsRetrievingService wordsRetrievingService;
    @Test
    void getRandomWordsTest() {
        final Random random = new Random();
        final Set<GeneratedWordsDto> wordsToReturn = new HashSet<>();
        int count = random.nextInt(10) + 1;
        wordsRetrievingService.getRandomWords(count, wordsToReturn);
        assertThat(wordsToReturn).size().isEqualTo(count);
    }
}
