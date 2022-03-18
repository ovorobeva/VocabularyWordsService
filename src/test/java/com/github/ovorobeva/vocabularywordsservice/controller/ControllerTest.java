package com.github.ovorobeva.vocabularywordsservice.controller;

import com.github.ovorobeva.vocabularywordsservice.dao.WordsRepository;
import com.github.ovorobeva.vocabularywordsservice.service.WordsSavingService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private WordsRepository wordsRepository;
    @Autowired
    private WordsSavingService wordsSavingService;

    @BeforeEach
    void before() {
        if (wordsRepository.count() < 20)
            wordsSavingService.fillWordsUp(20);
    }

    @Test
    void getWordsTest() {
        Random random = new Random();
        int count = random.nextInt(10) + 1;
        assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/getwords/" + count, List.class)).asList().hasSize(count);
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