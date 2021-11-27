package com.gitjub.ovorobeva.vocabularywordsservice.wordsprocessing;

import com.gitjub.ovorobeva.vocabularywordsservice.model.generated.GeneratedWordsDto;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Data
public class WordsHandler {
    @Autowired
    WordsClient wordsClient;
    @Autowired
    PartsOfSpeechClient partsOfSpeechClient;


    public void getProcessedWords(List<GeneratedWordsDto> generatedWordsList,
                                  int wordsCount,
                                  int lastCode) throws InterruptedException {
        List<String> words = wordsClient.getRandomWords(wordsCount);
        List<String> checkedWords = new ArrayList<>();
        Iterator<String> iterator = words.iterator();

        WordsClient.logger.log(Level.INFO, "getWords: Starting removing non-matching words from the list \n" + words);

        ExecutorService executor = Executors.newFixedThreadPool(words.size());
        while (iterator.hasNext()) {
            String word = iterator.next();
            Pattern pattern = Pattern.compile("[^a-zA-Z[-]]");

            executor.execute(() -> {
                Matcher matcher = pattern.matcher(word);
                if (matcher.find()) {
                    WordsClient.logger.log(Level.INFO, "getWords: Removing the word " + word + " because of containing symbol " + matcher.toMatchResult());
                } else if (!isPartOfSpeechCorrect(word)) {
                    WordsClient.logger.log(Level.INFO, "getWords: Removing the word " + word + " because of the wrong part of speech.");
                } else {
                    checkedWords.add(word);
                }
            });
        }
        System.out.println("added words" + checkedWords);

        executor.shutdown();
        while (!executor.isTerminated()) {
        }

        iterator = checkedWords.iterator();
        while (iterator.hasNext()) {
            String addedWord = iterator.next();
            generatedWordsList.add(new GeneratedWordsDto(addedWord, lastCode));
            lastCode++;
        }

        if (generatedWordsList.size() < words.size()) {
            wordsCount = words.size() - generatedWordsList.size();
            getProcessedWords(generatedWordsList, wordsCount, lastCode);
        }
        System.out.println("GWL " + generatedWordsList);
    }

    private Boolean isPartOfSpeechCorrect(String word) {
        WordsClient.logger.log(Level.INFO, "isPartOfSpeechCorrect: the word " + word + " is being checked");
        boolean isCorrect = true;
        List<String> partsOfSpeech = null;
        try {
            partsOfSpeech = partsOfSpeechClient.getPartsOfSpeech(word);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        WordsClient.logger.log(Level.INFO, "isPartOfSpeechCorrect: parts of speech for the word " + word + " are: " + partsOfSpeech);

        if (partsOfSpeech == null || partsOfSpeech.isEmpty()) {
            return false;
        }

        for (String partOfSpeech : partsOfSpeech) {
            if (!partOfSpeech.matches("(?i)noun|phrasal verb|adverb & adjective|adjective|transitive & intransitive verb|transitive verb|intransitive verb|verb|adverb|idiom|past-participle") || partOfSpeech.isEmpty()) {
                WordsClient.logger.log(Level.INFO, "isPartOfSpeechCorrect: The word " + word + " is to be removed because of part of speech: " + partOfSpeech);
                isCorrect = false;
                break;
            }
        }
        return isCorrect;
    }
}

