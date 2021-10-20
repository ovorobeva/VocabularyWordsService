package com.gitjub.ovorobeva.vocabularywordsservice.translates;

import com.gitjub.ovorobeva.vocabularywordsservice.model.generated.GeneratedWords;
import com.gitjub.ovorobeva.vocabularywordsservice.wordsprocessing.WordsClient;
import com.gitjub.ovorobeva.vocabularywordsservice.wordsprocessing.WordsProcessing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

@Component
public class Translation {
    @Autowired
    TranslateClient translateClient;
    @Autowired
    WordsProcessing wordsProcessing;

    public List<GeneratedWords> getTranslates(WordsProcessing wordsProcessing) {
        List<GeneratedWords> wordList = new LinkedList<>();
        try {
            wordsProcessing.getWords(wordList, 0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        WordsClient.logger.log(Level.INFO, "Parsing finished. Words are: " + wordList);

        try {
            for (GeneratedWords word : wordList) {
                WordsClient.logger.log(Level.INFO, "Getting translation for the word: " + word.getEn());
                translateClient.getTranslate(word);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return wordList;
    }
}
