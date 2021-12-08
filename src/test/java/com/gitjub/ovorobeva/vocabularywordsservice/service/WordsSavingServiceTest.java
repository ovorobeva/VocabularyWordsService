package com.gitjub.ovorobeva.vocabularywordsservice.service;

import com.gitjub.ovorobeva.vocabularywordsservice.dao.WordsRepository;
import com.gitjub.ovorobeva.vocabularywordsservice.model.generated.GeneratedWordsDto;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
class WordsSavingServiceTest {

    @Autowired
    private WordsRepository wordsRepository;
    @Autowired
    private WordsSavingService wordsSavingService;
    private Random random = new Random();

    @Test
    void fillWordsUpTest() {
        int countBefore = (int) wordsRepository.count();
        int countForFill = random.nextInt(10) + 1;
        wordsSavingService.fillWordsUp(countForFill);
        assertThat(wordsRepository.count()).isCloseTo(countBefore + countForFill, Percentage.withPercentage(10.0));
    }

    @Test
    void saveMissingWordsTest() {
        int randomCode = random.nextInt((int) (wordsRepository.count() + 1));
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
    void fillMissingTranslatesTest(){
        int randomCode = random.nextInt((int) (wordsRepository.count() + 1));
        GeneratedWordsDto word = wordsRepository.getByCode(randomCode);
        String currentTranslation = word.getFr();
        word.setFr(null);
        wordsRepository.saveAndFlush(word);

       ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.execute(() -> wordsSavingService.fillMissingTranslates());
        executor.shutdown();
        while (!executor.isTerminated()) {
        }
        assertThat(wordsRepository.getByCode(randomCode).getFr()).isEqualTo(currentTranslation);
    }
}