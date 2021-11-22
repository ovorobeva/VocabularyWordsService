package com.gitjub.ovorobeva.vocabularywordsservice.translates;

import com.gitjub.ovorobeva.vocabularywordsservice.model.generated.GeneratedWords;
import com.gitjub.ovorobeva.vocabularywordsservice.wordsprocessing.WordsClient;
import com.gitjub.ovorobeva.vocabularywordsservice.wordsprocessing.WordsProcessing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

@Service
public class TranslateService {
    @Autowired
    TranslateFactory factory;
    @Autowired
    WordsProcessing wordsProcessing;

    public List<GeneratedWords> getTranslates(WordsProcessing wordsProcessing) {
        List<GeneratedWords> wordList = new LinkedList<>();
        try {
            wordsProcessing.getWords(wordList);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        WordsClient.logger.log(Level.INFO, "Parsing finished. Words are: " + wordList);

        try {
            TranslateClient translateClientRu = factory.getTranslateClient(Language.RU);
            TranslateClient translateClientFr = factory.getTranslateClient(Language.FR);
            for (GeneratedWords word : wordList) {
                WordsClient.logger.log(Level.INFO, "Getting translation for the word: " + word.getEn());
                translateClientRu.translateWord(word);
                translateClientFr.translateWord(word);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return wordList;
    }
}
