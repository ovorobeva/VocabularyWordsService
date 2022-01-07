package com.github.ovorobeva.vocabularywordsservice.wordsprocessing;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("test")
@Configuration
class LemmaClientTestConfiguration {

    @Bean
    @Primary
    public LemmaClient lemmaClient() {
        return Mockito.mock(LemmaClient.class);
    }
}