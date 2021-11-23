package com.gitjub.ovorobeva.vocabularywordsservice.service;

import com.gitjub.ovorobeva.vocabularywordsservice.dao.WordsRepository;
import com.gitjub.ovorobeva.vocabularywordsservice.model.generated.GeneratedWordsDto;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.Set;

@Service
@Data
public class WordsRetrievingService {
    @Autowired
    WordsRepository wordsRepository;
    private Random random = new Random();

    public void getRandomWords(int wordsCount, Set<GeneratedWordsDto> wordsToReturn) {
        for (byte i = 0; i < wordsCount; i++) {
            int id = random.nextInt((int) (wordsRepository.count() - 1));
            wordsToReturn.add(getWord(id));
        }
        if (wordsToReturn.size() < wordsCount) {
            wordsCount = wordsCount - wordsToReturn.size();
            getRandomWords(wordsCount, wordsToReturn);
        }

    }

    public GeneratedWordsDto getWord(int id) {
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
