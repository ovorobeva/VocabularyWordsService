package com.github.ovorobeva.vocabularywordsservice.service;

public interface WordsSavingService {

    /**
     * Adds new words to the db
     *
     * @param wordsCount count of words to add
     */
    void fillWordsUp(int wordsCount);

    /**
     * Checks if the new language is added and adds translations for it for old existing words
     */
    void fillMissingTranslates();
    //todo: to make it easier. get translate for only one word if it is missing in db and put it to db too
}
