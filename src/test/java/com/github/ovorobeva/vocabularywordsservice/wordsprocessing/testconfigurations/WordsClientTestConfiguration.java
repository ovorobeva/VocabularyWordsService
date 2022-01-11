package com.github.ovorobeva.vocabularywordsservice.wordsprocessing.testconfigurations;

import com.github.ovorobeva.vocabularywordsservice.wordsprocessing.WordsClient;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("test")
@Configuration
class WordsClientTestConfiguration {

    @Bean
    @Primary
    public WordsClient wordsClient() {
        return Mockito.mock(WordsClient.class);
    }
}