package com.gitjub.ovorobeva.vocabularywordsservice.service;

import com.gitjub.ovorobeva.vocabularywordsservice.dto.generated.GeneratedWords;
import com.gitjub.ovorobeva.vocabularywordsservice.translates.Translation;
import com.gitjub.ovorobeva.vocabularywordsservice.wordsprocessing.WordsProcessing;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Data
public class WordsService {
    @Autowired
    Translation translation;
    @Autowired
    WordsProcessing wordsProcessing;

    private int wordsCount;

    public List<GeneratedWords> getWordList() {
        wordsProcessing.setWordsCount(wordsCount);
        return translation.getTranslates(wordsProcessing);
    }

}
