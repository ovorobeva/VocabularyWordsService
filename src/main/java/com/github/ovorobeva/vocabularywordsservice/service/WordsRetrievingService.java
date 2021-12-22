package com.github.ovorobeva.vocabularywordsservice.service;

import com.github.ovorobeva.vocabularywordsservice.dao.WordsRepository;
import com.github.ovorobeva.vocabularywordsservice.model.generated.GeneratedWordsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.Set;

@Service
public class WordsRetrievingService {
    @Autowired
    WordsRepository wordsRepository;
    private final Random random = new Random();

    public void getRandomWords(int wordsCount, Set<GeneratedWordsDto> wordsToReturn) {
        for (byte i = 0; i < wordsCount; i++) {
            int id = random.nextInt((int) (wordsRepository.count() - 2)) + 1;
            wordsToReturn.add(getWord(id));
        }
        if (wordsToReturn.size() < wordsCount) {
            wordsCount = wordsCount - wordsToReturn.size();
            getRandomWords(wordsCount, wordsToReturn);
        }

    }

    private GeneratedWordsDto getWord(int id) {
        //todo: to place logger
        System.out.println("getting word with id = " + id);
        int size = (int) wordsRepository.count();
        if (wordsRepository.findByCode(id).isEmpty()) {
            id = random.nextInt(size);
            getWord(id);
        } else
            return wordsRepository.findByCode(id).get();
        return null;
    }
}
