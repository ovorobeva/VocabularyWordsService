package com.gitjub.ovorobeva.vocabularywordsservice.wordsprocessing;

import com.gitjub.ovorobeva.vocabularywordsservice.model.generated.GeneratedWordsDto;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
@Service
@Data
public class WordsProcessing {
    @Autowired
    WordsClient wordsClient;

    private static Boolean isPartOfSpeechCorrect(String word, WordsClient wordsClient) throws InterruptedException {
        WordsClient.logger.log(Level.INFO, "isPartOfSpeechCorrect: the word " + word + " is being checked");
        boolean isCorrect = true;
        List<String> partsOfSpeech = wordsClient.getPartsOfSpeech(word);

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


    public void getWords(List<GeneratedWordsDto> generatedWordsList, int wordsCount, int lastCode) throws InterruptedException {
        List<String> words = wordsClient.getRandomWords(wordsCount);

        Iterator<String> iterator = words.iterator();
        int removedCounter = 0;

        WordsClient.logger.log(Level.INFO, "getWords: Starting removing non-matching words from the list \n" + words);

        while (iterator.hasNext()) {
            String word = iterator.next();
            Pattern pattern = Pattern.compile("[^a-zA-Z[-]]");
            Matcher matcher = pattern.matcher(word);

            if (matcher.find()) {
                removedCounter++;
                WordsClient.logger.log(Level.INFO, "getWords: Removing the word " + word + " because of containing symbol " + matcher.toMatchResult() + ". The count of deleted words is " + removedCounter);
                continue;
            }

            if (!isPartOfSpeechCorrect(word, wordsClient)) {
                removedCounter++;
                WordsClient.logger.log(Level.INFO, "getWords: Removing the word " + word + " because of the wrong part of speech. The count of deleted words is " + removedCounter);
                continue;
            }
            generatedWordsList.add(new GeneratedWordsDto(word, lastCode));
            lastCode++;
        }
        if (removedCounter > 0) {
            wordsCount = removedCounter;
            getWords(generatedWordsList, wordsCount, lastCode);
        }
    }
}

