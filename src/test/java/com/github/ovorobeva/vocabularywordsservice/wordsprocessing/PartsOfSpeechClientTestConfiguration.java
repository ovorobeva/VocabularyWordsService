package com.github.ovorobeva.vocabularywordsservice.wordsprocessing;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("test")
@Configuration
class PartsOfSpeechClientTestConfiguration {

    @Bean
    @Primary
    public PartsOfSpeechClient partsOfSpeechClient() {
        return Mockito.mock(PartsOfSpeechClient.class);
    }
}