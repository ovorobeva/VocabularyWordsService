package com.github.ovorobeva.vocabularywordsservice.service;

import com.github.ovorobeva.vocabularywordsservice.model.generated.GeneratedWordsDto;

import java.util.Set;

public interface WordsFetchingServiceExternal {

    /**
     * Fetches words from external API, processes them by checking on profanity and part of speech,
     * turning them into infinitive and returnes them with translates.
     *
     * @param wordsCount required number of words to fetch
     * @param lastCode   last existing code in the database
     */
    Set<GeneratedWordsDto> getProcessedWords(Integer wordsCount, Integer lastCode);
}
