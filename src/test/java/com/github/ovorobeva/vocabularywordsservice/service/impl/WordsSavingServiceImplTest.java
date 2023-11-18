package com.github.ovorobeva.vocabularywordsservice.service.impl;

import com.github.ovorobeva.vocabularywordsservice.model.generated.GeneratedWordsDto;
import com.github.ovorobeva.vocabularywordsservice.properties.WordsProperties;
import com.github.ovorobeva.vocabularywordsservice.repositories.WordsRepository;
import com.github.ovorobeva.vocabularywordsservice.service.WordsSavingService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WordsSavingServiceImplTest {

    private final Random random = new Random();

    @Autowired
    private WordsRepository wordsRepository;

    @Autowired
    private WordsSavingService wordsSavingService;

    @Autowired
    private WordsProperties wordsProperties;

    @BeforeEach
    void before() {
        if (wordsRepository.count() < wordsProperties.getDefaultWordCount())
            wordsSavingService.fillWordsUp(wordsProperties.getDefaultWordCount());
    }


    @Test
    void saveMissingWordsTest() {
        int randomCode = random.nextInt((int) (wordsRepository.count())) + 1;
        wordsRepository.deleteByCode(randomCode);
        assertThat(wordsRepository.getByCode(randomCode)).isNull();
        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.execute(() -> wordsSavingService.fillWordsUp(random.nextInt(5) + 1));
        executor.shutdown();
        while (!executor.isTerminated()) {
        }
        assertThat(wordsRepository.getByCode(randomCode).getCode()).isEqualTo(randomCode);
    }

    @Test
    synchronized void fillMissingTranslatesTest() throws InterruptedException {
        int randomCode = random.nextInt((int) (wordsRepository.count())) + 1;
        GeneratedWordsDto word = wordsRepository.getByCode(randomCode);
        String currentTranslationFr = word.getFr();
        word.setFr(null);
        wordsRepository.saveAndFlush(word);

        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.execute(() -> wordsSavingService.fillMissingTranslates());
        executor.shutdown();
        Thread.sleep(10000);
        assertThat(wordsRepository.getByCode(randomCode).getFr()).isEqualTo(currentTranslationFr);
    }

    @AfterEach
    void afterEach() {
        wordsRepository.deleteAll();
    }
}