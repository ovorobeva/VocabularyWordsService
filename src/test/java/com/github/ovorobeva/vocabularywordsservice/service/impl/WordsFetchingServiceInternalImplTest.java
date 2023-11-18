package com.github.ovorobeva.vocabularywordsservice.service.impl;

import com.github.ovorobeva.vocabularywordsservice.model.generated.GeneratedWordsDto;
import com.github.ovorobeva.vocabularywordsservice.repositories.WordsRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Random;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WordsFetchingServiceInternalImplTest {

    @Autowired
    private WordsFetchingServiceInternalImpl wordsFetchingServiceInternalImpl;
    @Autowired
    private WordsRepository wordsRepository;
    @Autowired
    private WordsSavingServiceImpl wordsSavingServiceImpl;

    @BeforeEach
    void before() {
        if (wordsRepository.count() < 20)
            wordsSavingServiceImpl.fillWordsUp(20);
        for (int i = 0; i <= 8; i += 2){
            wordsRepository.deleteByCode(i);
        }
    }

    @Test
    void getRandomWordsTest() {
        final Random random = new Random();
        int count = random.nextInt(10) + 1;
        final Set<GeneratedWordsDto> wordsToReturn = wordsFetchingServiceInternalImpl.getRandomWords(count);
        assertThat(wordsToReturn).size().isEqualTo(count);
    }
    @AfterEach
    void afterEach() {
        wordsRepository.deleteAll();
    }
}
