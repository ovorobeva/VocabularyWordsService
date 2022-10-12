package com.github.ovorobeva.vocabularywordsservice.service;

import com.github.ovorobeva.vocabularywordsservice.dao.WordsRepository;
import com.github.ovorobeva.vocabularywordsservice.model.generated.GeneratedWordsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class WordsRetrievingService {

    private final WordsRepository wordsRepository;
    private final Random random = new Random();
    private int defWordCount = 0;

    public void fetchRandomWordsFromRepository(int wordsCount, Set<GeneratedWordsDto> wordsToReturn) {
        if (defWordCount == 0) defWordCount = wordsCount;
        while (wordsToReturn.size() < defWordCount) {
            wordsCount = defWordCount - wordsToReturn.size();
            getRandomWords(wordsCount, wordsToReturn);
        }
        defWordCount = 0;
    }

    private void getRandomWords(int wordsCount, Set<GeneratedWordsDto> wordsToReturn) {
        for (byte i = 0; i < wordsCount; i++) {
            int id = random.nextInt((int) (wordsRepository.count() - 2)) + 1;
            wordsToReturn.add(getWord(id));
        }
    }

    private GeneratedWordsDto getWord(int id) {
        log.debug("getting word with id = " + id);
        int size = (int) wordsRepository.count();
        if (wordsRepository.findByCode(id).isEmpty()) {
            id = random.nextInt(size);
            return getWord(id);
        } else{
            log.debug("Returning the word " + wordsRepository.findByCode(id).get());
            return wordsRepository.findByCode(id).get();}
    }
}
