package com.github.ovorobeva.vocabularywordsservice.wordsprocessing.testconfigurations;

import com.github.ovorobeva.vocabularywordsservice.translates.TranslateFactory;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("test")
@Configuration
class TranslateFactoryTestConfiguration {

    @Bean
    @Primary
    public TranslateFactory translateFactory() {
        return Mockito.mock(TranslateFactory.class);
    }
}