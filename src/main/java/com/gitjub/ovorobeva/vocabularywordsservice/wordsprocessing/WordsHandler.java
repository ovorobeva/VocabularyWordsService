package com.gitjub.ovorobeva.vocabularywordsservice.wordsprocessing;

import com.gitjub.ovorobeva.vocabularywordsservice.model.generated.GeneratedWordsDto;
import com.gitjub.ovorobeva.vocabularywordsservice.translates.Language;
import com.gitjub.ovorobeva.vocabularywordsservice.translates.TranslateClient;
import com.gitjub.ovorobeva.vocabularywordsservice.translates.TranslateFactory;
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
    @Autowired
    TranslateFactory factory;
    @Autowired
    ProfanityCheckerClient profanityCheckerClient;
    @Autowired
    LemmaClient lemmaClient;


    public void getProcessedWords(List<GeneratedWordsDto> generatedWordsList,
                                  int wordsCount,
                                  int lastCode) throws InterruptedException {
        List<String> words = wordsClient.getRandomWords(wordsCount);
        List<String> checkedWords = new ArrayList<>();
        Iterator<String> iterator = words.iterator();

        WordsClient.logger.log(Level.INFO, "getWords: Starting removing non-matching words from the list \n" + words);

        ExecutorService executor = Executors.newFixedThreadPool(wordsCount);
        while (iterator.hasNext()) {
            String word = iterator.next();
            Pattern pattern = Pattern.compile("[^a-zA-Z[-]]");

            executor.execute(() -> {
                Matcher matcher = pattern.matcher(word);
                String lemma;
                if (matcher.find()) {
                    WordsClient.logger.log(Level.INFO, "getWords: Removing the word " + word + " because of containing symbol " + matcher.toMatchResult());
                } else {
                    lemma = lemmaClient.getLemma(word);
                    if (lemma.equals(LemmaClient.SELDOM_WORD)) {
                        WordsClient.logger.log(Level.INFO, "getWords: Removing the word " + lemma + " because of its never using.");
                    } else if (!isPartOfSpeechCorrect(lemma)) {
                        WordsClient.logger.log(Level.INFO, "getWords: Removing the word " + lemma + " because of the wrong part of speech.");
                    } else if (profanityCheckerClient.isProfanity(lemma)) {
                        WordsClient.logger.log(Level.INFO, "getWords: Removing the word " + lemma + " because of profanity.");
                    } else {
                        checkedWords.add(lemma);
                    }
                }
            });
        }

        executor.shutdown();
        while (!executor.isTerminated()) {
        }

        iterator = checkedWords.iterator();
        while (iterator.hasNext()) {
            String addedWord = iterator.next();
            GeneratedWordsDto word = new GeneratedWordsDto(addedWord, lastCode);
            translate(word);
            generatedWordsList.add(word);
            lastCode++;
        }

        if (generatedWordsList.size() < wordsCount) {
            wordsCount = wordsCount - generatedWordsList.size();
            getProcessedWords(generatedWordsList, wordsCount, lastCode);
        }
    }

    private boolean isPartOfSpeechCorrect(String word) {
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
            if (!partOfSpeech.matches("(?i)noun" +
                    "|phrasal verb" +
                    "|adverb & adjective" +
                    "|adjective" +
                    "|transitive & intransitive verb" +
                    "|transitive verb" +
                    "|intransitive verb" +
                    "|verb" +
                    "|adverb" +
                    "|idiom" +
                    "|past-participle") || partOfSpeech.isEmpty()) {
                WordsClient.logger.log(Level.INFO, "isPartOfSpeechCorrect: The word " + word + " is to be removed because of part of speech: " + partOfSpeech);
                isCorrect = false;
                break;
            }
        }
        return isCorrect;
    }

    private void translate(GeneratedWordsDto word){
        TranslateClient translateClientRu = factory.getTranslateClient(Language.RU);
        TranslateClient translateClientFr = factory.getTranslateClient(Language.FR);

        translateClientRu.translateWord(word);
        Thread frenchThread = new Thread(() -> translateClientFr.translateWord(word));
        frenchThread.start();
        try {
            frenchThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
