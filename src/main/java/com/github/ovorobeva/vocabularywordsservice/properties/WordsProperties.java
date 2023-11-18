package com.github.ovorobeva.vocabularywordsservice.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Data
@Validated
@Configuration
@ConfigurationProperties(prefix = "words")
public class WordsProperties {

    @NotNull
    Integer defaultWordCount;

    @NotNull
    String wordsApiKey;

    @NotNull
    String translationApiKey;
}
