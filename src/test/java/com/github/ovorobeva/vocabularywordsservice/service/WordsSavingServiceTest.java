package com.github.ovorobeva.vocabularywordsservice.service;

import com.github.ovorobeva.vocabularywordsservice.dao.WordsRepository;
import com.github.ovorobeva.vocabularywordsservice.model.generated.GeneratedWordsDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WordsSavingServiceTest {

    private final Random random = new Random();
    @Autowired
    private WordsRepository wordsRepository;
    @Autowired
    private WordsSavingService wordsSavingService;

    @Value("${default.words.count}")
    int defaultWordCount;

    @BeforeEach
    void before() {
        if (wordsRepository.count() < defaultWordCount)
            wordsSavingService.fillWordsUp(defaultWordCount);
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