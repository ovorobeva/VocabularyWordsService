package com.gitjub.ovorobeva.vocabularywordsservice.exceptions;

public class LimitExceededException extends Exception{
    public LimitExceededException(String message) {
        super(message);
    }
}
