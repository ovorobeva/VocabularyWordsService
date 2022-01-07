package com.github.ovorobeva.vocabularywordsservice;

import com.github.ovorobeva.vocabularywordsservice.controller.Controller;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DirtiesContext
class VocabularyWordsServiceApplicationTests {

    @Autowired
    private Controller controller;

    @Test
    void contextLoads() {
        assertThat(controller).isNotNull();
    }
    //todo: to replace logger with log4j
    //todo: to make readme
    //todo: to make a documentation
    //todo: to start tests from docker
    //todo: to add languages into android project itself
    //todo: to make mocks for clients
    //todo: to handle wrong requests in controller

}
