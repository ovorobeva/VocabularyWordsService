package com.github.ovorobeva.vocabularywordsservice.service;

import com.github.ovorobeva.vocabularywordsservice.dao.WordsRepository;
import com.github.ovorobeva.vocabularywordsservice.model.generated.GeneratedWordsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Service fetching words from internal database
 * 
 * @author Olga Vorobeva 2020
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class WordsRetrievingService {

    private final WordsRepository wordsRepository;
    private final Random random = new Random();
    private int defWordCount = 0;

    /**
     * Gets random words from an internal database.
     * 
     * @param wordsCount required count of words to return
     * @return set of random words with their translates
     */
    public Set<GeneratedWordsDto> getRandomWords(int wordsCount) {
        Set<GeneratedWordsDto> wordsToReturn = new HashSet<>();
        if (defWordCount == 0) defWordCount = wordsCount;
        while (wordsToReturn.size() < defWordCount) {
            wordsCount = defWordCount - wordsToReturn.size();
            wordsToReturn = getMissingWords(wordsCount);
        }
        defWordCount = 0;
        return wordsToReturn;
    }

    private Set<GeneratedWordsDto> getMissingWords(int wordsCount) {
        Set<GeneratedWordsDto> wordsToReturn = new HashSet<>();
        for (byte i = 0; i < wordsCount; i++) {
            int code = random.nextInt((int) (wordsRepository.count() - 2)) + 1;
            wordsRepository.findByCode(code).ifPresent(wordsToReturn::add);
        }
        return wordsToReturn;
    }
}
