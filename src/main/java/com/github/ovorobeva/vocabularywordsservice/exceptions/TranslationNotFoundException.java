package com.github.ovorobeva.vocabularywordsservice.exceptions;

public class TranslationNotFoundException extends Exception{

    public TranslationNotFoundException(String word) {
        super(String.format("Translation for the word %s is not found.", word));
    }
}
