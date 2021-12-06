package com.gitjub.ovorobeva.vocabularywordsservice;

import com.gitjub.ovorobeva.vocabularywordsservice.controller.Controller;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class VocabularyWordsServiceApplicationTests {

    @Autowired
    private Controller controller;

    @Test
    void contextLoads() {
        assertThat(controller).isNotNull();
    }

}
