package com.github.ovorobeva.vocabularywordsservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class VocabularyWordsServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(VocabularyWordsServiceApplication.class, args);
    }

}
