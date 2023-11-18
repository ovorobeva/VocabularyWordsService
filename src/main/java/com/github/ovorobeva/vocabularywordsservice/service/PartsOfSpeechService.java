package com.github.ovorobeva.vocabularywordsservice.service;

import java.util.List;

public interface PartsOfSpeechService {

    /**
     * Returns parts of speech for given word
     *
     * @param word to find parts of speech for
     * @return list of applicable parts of speech
     */
    List<String> getPartsOfSpeech(final String word);
}
