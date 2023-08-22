package com.github.ovorobeva.vocabularywordsservice.controller;

import com.github.ovorobeva.vocabularywordsservice.repositories.WordsRepository;
import com.github.ovorobeva.vocabularywordsservice.service.impl.WordsSavingServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@PropertySource(value = "classpath:application.properties")
@RunWith(SpringJUnit4ClassRunner.class)
class ControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private WordsRepository wordsRepository;
    @Autowired
    private WordsSavingServiceImpl wordsSavingServiceImpl;

    @BeforeEach
    void before() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
        if (wordsRepository.count() < 20)
            wordsSavingServiceImpl.fillWordsUp(20);});
        executor.shutdown();
        while (!executor.isTerminated()) {
        }
    }

    @Test
    void getWordsTest() {
        Random random = new Random();
        int count = random.nextInt(10) + 1;
        System.out.println(count);
        assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/getwords/" + count, List.class))
                .asList()
                .hasSize(count);
    }
    @Test
    void getWordsWrongTest() {
        assertThat(this.restTemplate.getForEntity("http://localhost:" + port + "/getwords/a", String.class).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @AfterEach
    void afterEach() {
        wordsRepository.deleteAll();
    }
}