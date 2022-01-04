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
    private int defWordCount = 0;

    public void getRandomWords(int wordsCount, Set<GeneratedWordsDto> wordsToReturn) {
        if (defWordCount == 0) defWordCount = wordsCount;
        for (byte i = 0; i < wordsCount; i++) {
            int id = random.nextInt((int) (wordsRepository.count() - 2)) + 1;
            wordsToReturn.add(getWord(id));
        }
        if (wordsToReturn.size() < defWordCount) {
            wordsCount = defWordCount - wordsToReturn.size();
            getRandomWords(wordsCount, wordsToReturn);
        }

    }

    private GeneratedWordsDto getWord(int id) {
        System.out.println("getting word with id = " + id);
        int size = (int) wordsRepository.count();
        if (wordsRepository.findByCode(id).isEmpty()) {
            id = random.nextInt(size);
            return getWord(id);
        } else{
            System.out.println("Returning thw word " + wordsRepository.findByCode(id).get());
            return wordsRepository.findByCode(id).get();}
    }
}
