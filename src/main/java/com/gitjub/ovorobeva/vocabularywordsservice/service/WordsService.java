package com.gitjub.ovorobeva.vocabularywordsservice.service;

import com.gitjub.ovorobeva.vocabularywordsservice.dao.WordsRepository;
import com.gitjub.ovorobeva.vocabularywordsservice.model.generated.GeneratedWords;
import com.gitjub.ovorobeva.vocabularywordsservice.translates.Translation;
import com.gitjub.ovorobeva.vocabularywordsservice.wordsprocessing.WordsProcessing;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@Service
@Data
public class WordsService {

    @Autowired
    Translation translation;
    @Autowired
    WordsProcessing wordsProcessing;
    @Autowired
    WordsRepository wordsRepository;
    Random random = new Random();
    private int wordsCount = 0;
    Set<GeneratedWords> generatedWordsSet = new HashSet<>(wordsCount);

    public Set<GeneratedWords> getWords() {
        if (generatedWordsSet.size() >= wordsCount) generatedWordsSet.clear();
        for (byte i = 0; i < wordsCount; i++) {
            int id = random.nextInt((int) (wordsRepository.count() - 1));
            generatedWordsSet.add(getWord(id));
        }
        if (generatedWordsSet.size() < wordsCount) {
            wordsCount = wordsCount - generatedWordsSet.size();
            getWords();
        }
        return generatedWordsSet;
    }

    public GeneratedWords getWord(int id) {
        System.out.println("getting word with id = " + id);
        int size = (int) wordsRepository.count();
        if (wordsRepository.findById(id).isEmpty()) {
            id = random.nextInt(50);
            getWord(id);
        } else
            return wordsRepository.getById(id);
        return null;
    }

    @PostConstruct
    public void fillWordsUp() {
        System.out.println("COUNT" + wordsCount);
        if (wordsCount == 0) {
            if (wordsRepository.count() == 0)
                wordsCount = 20;
            else return;
        }
        wordsProcessing.setWordsCount(wordsCount);
        wordsRepository.saveAll(translation.getTranslates(wordsProcessing));
    }

}
