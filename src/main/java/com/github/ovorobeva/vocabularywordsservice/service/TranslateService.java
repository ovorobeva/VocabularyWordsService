package com.github.ovorobeva.vocabularywordsservice.service;

import com.github.ovorobeva.vocabularywordsservice.model.generated.GeneratedWordsDto;

public interface TranslateService {

    /**
     * Sets translation in db for the given word
     *
     * @param word to find translation for
     */
    void translateWord(final GeneratedWordsDto word);
}
