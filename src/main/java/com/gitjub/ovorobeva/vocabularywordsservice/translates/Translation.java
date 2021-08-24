package com.gitjub.ovorobeva.vocabularywordsservice.translates;

import com.gitjub.ovorobeva.vocabularywordsservice.dto.generated.GeneratedWords;
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

    public List<GeneratedWords> getTranslates(WordsProcessing wordsProcessing) {
        List<GeneratedWords> wordList;
  //      TranslateClient translateClient = TranslateClient.getTranslateClient();
        try {
            wordList = new LinkedList<>(wordsProcessing.getWords());
        } catch (InterruptedException e) {
            wordList = new LinkedList<>();
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