package com.github.ovorobeva.vocabularywordsservice.exceptions;

public class LimitExceededException extends Exception{
    public LimitExceededException(String message) {
        super(message);
    }
}
