package com.gitjub.ovorobeva.vocabularywordsservice.translates;

import lombok.Data;

@Data
public class ApiVariables {
    String sourceLanguageCode;


    String targetLanguageCode;
    String texts;

    public ApiVariables(String sourceLanguageCode, String targetLanguageCode, String word) {
        this.sourceLanguageCode = sourceLanguageCode;
        this.targetLanguageCode = targetLanguageCode;
        this.texts = word;
    }
}
