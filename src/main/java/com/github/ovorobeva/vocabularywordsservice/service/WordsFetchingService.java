package com.github.ovorobeva.vocabularywordsservice.service;

import com.github.ovorobeva.vocabularywordsservice.model.generated.GeneratedWordsDto;

import java.util.Set;

public interface WordsFetchingService {
    Set<GeneratedWordsDto> getRandomWords(int wordsCount);
}
