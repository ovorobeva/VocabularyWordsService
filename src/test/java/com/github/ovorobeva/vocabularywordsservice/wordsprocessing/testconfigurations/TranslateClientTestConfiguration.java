package com.github.ovorobeva.vocabularywordsservice.wordsprocessing.testconfigurations;

import com.github.ovorobeva.vocabularywordsservice.exceptions.AuthTranslateException;
import com.github.ovorobeva.vocabularywordsservice.exceptions.GettingTranslateException;
import com.github.ovorobeva.vocabularywordsservice.exceptions.LimitExceededException;
import com.github.ovorobeva.vocabularywordsservice.model.generated.GeneratedWordsDto;
import com.github.ovorobeva.vocabularywordsservice.translates.TranslateClient;

import java.io.IOException;


public class TranslateClientTestConfiguration extends TranslateClient {

    @Override
    public void translateWord(GeneratedWordsDto word) throws GettingTranslateException, LimitExceededException, InterruptedException, IOException, AuthTranslateException {
        word.setCz(word.getEn() + "Cz");
        word.setFr(word.getEn() + "Fr");
        word.setRu(word.getEn() + "Ru");
    }
}