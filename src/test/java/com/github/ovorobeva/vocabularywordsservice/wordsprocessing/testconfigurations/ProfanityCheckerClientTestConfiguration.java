package com.github.ovorobeva.vocabularywordsservice.wordsprocessing.testconfigurations;

import com.github.ovorobeva.vocabularywordsservice.clients.ProfanityCheckerClient;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("test")
@Configuration
class ProfanityCheckerClientTestConfiguration {

    @Bean
    @Primary
    public ProfanityCheckerClient profanityCheckerClient() {
        return Mockito.mock(ProfanityCheckerClient.class);
    }
}