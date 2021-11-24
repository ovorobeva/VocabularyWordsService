package com.gitjub.ovorobeva.vocabularywordsservice.translates;

import org.springframework.stereotype.Component;

@Component
public class TranslateFactory {


    public TranslateClient getTranslateClient(Language language){
        switch (language) {
            case RU: return new TranslateClientRu();
            case FR: return new TranslateClientFr();
            default: throw new IllegalArgumentException("Wrong language: " + language);
        }
    }
}
