package com.github.ovorobeva.vocabularywordsservice.service;

import com.github.ovorobeva.vocabularywordsservice.dao.WordsRepository;
import com.github.ovorobeva.vocabularywordsservice.model.generated.GeneratedWordsDto;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
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
    void fillWordsUpTest() {
        assertThat(wordsRepository.count()).isCloseTo(defaultWordCount, Percentage.withPercentage(10.0));

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
    synchronized void fillMissingTranslatesTest() {
        int randomCode = random.nextInt((int) (wordsRepository.count())) + 1;
        GeneratedWordsDto word = wordsRepository.getByCode(randomCode);
        String currentTranslationFr = word.getFr();
        String currentTranslationCz = word.getCz();
        word.setFr(null);
        word.setCz(null);
        wordsRepository.saveAndFlush(word);

        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.execute(() -> wordsSavingService.fillMissingTranslates());
        executor.shutdown();
        while (!executor.isTerminated()) {
        }
        System.out.println(wordsRepository.getByCode(randomCode) + "\n french before = " + currentTranslationFr + " \n czech before = " + currentTranslationCz);
        assertThat(wordsRepository.getByCode(randomCode).getFr()).isEqualTo(currentTranslationFr);
        assertThat(wordsRepository.getByCode(randomCode).getCz()).isEqualTo(currentTranslationCz);
    }

    @AfterEach
    void afterEach() {
        wordsRepository.deleteAll();
    }
}