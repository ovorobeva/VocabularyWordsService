package com.gitjub.ovorobeva.vocabularywordsservice.service;

import com.gitjub.ovorobeva.vocabularywordsservice.dao.WordsRepository;
import com.gitjub.ovorobeva.vocabularywordsservice.model.generated.GeneratedWords;
import com.gitjub.ovorobeva.vocabularywordsservice.translates.Translation;
import com.gitjub.ovorobeva.vocabularywordsservice.wordsprocessing.WordsProcessing;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
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

    private int wordsCount;

    Set<GeneratedWords> generatedWordsSet = new HashSet<>(wordsCount);

    public Set<GeneratedWords> getWords(){
        if (generatedWordsSet.size() >= wordsCount) generatedWordsSet.clear();
        Random random = new Random();
        for (byte i = 0; i < wordsCount; i++) {
            int id = random.nextInt((int) (wordsRepository.count() - 1));
            if (wordsRepository.findById(id).isEmpty())
                throw new EntityNotFoundException("The blog with ID = " + id + " doesn't exist");
            GeneratedWords generatedWords = wordsRepository.getById(id);
            generatedWordsSet.add(generatedWords);
        }
        if (generatedWordsSet.size() < wordsCount){
            wordsCount = wordsCount - generatedWordsSet.size();
            getWords();
        }
        return generatedWordsSet;
    }

    public void fillWordsUp() {
        wordsProcessing.setWordsCount(wordsCount);
        wordsRepository.saveAll(translation.getTranslates(wordsProcessing));
    }

}
