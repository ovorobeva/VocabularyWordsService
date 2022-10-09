package com.github.ovorobeva.vocabularywordsservice.service;

import com.github.ovorobeva.vocabularywordsservice.clients.LemmaClient;
import com.github.ovorobeva.vocabularywordsservice.clients.PartsOfSpeechClient;
import com.github.ovorobeva.vocabularywordsservice.clients.ProfanityCheckerClient;
import com.github.ovorobeva.vocabularywordsservice.clients.WordsClient;
import com.github.ovorobeva.vocabularywordsservice.emailsender.EmailSender;
import com.github.ovorobeva.vocabularywordsservice.enums.CorrectPartsOfSpeech;
import com.github.ovorobeva.vocabularywordsservice.exceptions.AuthTranslateException;
import com.github.ovorobeva.vocabularywordsservice.exceptions.GettingTranslateException;
import com.github.ovorobeva.vocabularywordsservice.exceptions.LimitExceededException;
import com.github.ovorobeva.vocabularywordsservice.exceptions.TranslationNotFoundException;
import com.github.ovorobeva.vocabularywordsservice.model.generated.GeneratedWordsDto;
import com.github.ovorobeva.vocabularywordsservice.translates.Language;
import com.github.ovorobeva.vocabularywordsservice.translates.TranslateClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailSendException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class WordsHandler {

    private final WordsClient wordsClient;
    private final PartsOfSpeechClient partsOfSpeechClient;
    private final TranslateClient translateClient;
    private final ProfanityCheckerClient profanityCheckerClient;
    private final LemmaClient lemmaClient;
    private final EmailSender emailSender;

    private int defWordCount = 0;

    public void getProcessedWords(List<GeneratedWordsDto> generatedWordsList,
                                  int wordsCount,
                                  int lastCode) throws InterruptedException {
        if (defWordCount == 0) defWordCount = wordsCount;
        System.out.println(defWordCount);
        List<String> words = wordsClient.getRandomWords(wordsCount);
        List<String> checkedWords = new ArrayList<>();
        List<GeneratedWordsDto> wordsToAdd = new ArrayList<>();
        Iterator<String> iterator = words.iterator();

        log.debug("getWords: Starting removing non-matching words from the list \n" + words);

        List<ExecutorService> executors = new ArrayList<>();
        while (iterator.hasNext()) {
            String word = iterator.next();
            Pattern pattern = Pattern.compile("[^a-zA-Z[-]]");

            ExecutorService executor = Executors.newSingleThreadExecutor();
            executors.add(executor);
            executor.execute(() -> {
                Matcher matcher = pattern.matcher(word);
                String lemma;
                if (matcher.find()) {
                    log.info("getWords: Removing the word " + word + " because of containing symbol " + matcher.toMatchResult());
                } else {
                    lemma = lemmaClient.getLemma(word);
                    if (lemma.equals(LemmaClient.SELDOM_WORD)) {
                        log.info("getWords: Removing the word " + lemma + " because of its never using.");
                    } else if (!isPartOfSpeechCorrect(lemma)) {
                        log.info("getWords: Removing the word " + lemma + " because of the wrong part of speech.");
                    } else if (profanityCheckerClient.isProfanity(lemma)) {
                        log.info("getWords: Removing the word " + lemma + " because of profanity.");
                    } else {
                        checkedWords.add(lemma);
                        GeneratedWordsDto generatedWord = new GeneratedWordsDto(lemma, 0);
                        translateWord(generatedWord);
                        wordsToAdd.add(generatedWord);
                    }
                }
            });
        }

        executors.forEach(executor -> {
            executor.shutdown();
            while (!executor.isTerminated()) {
            }
        });

        for (GeneratedWordsDto addedWord : wordsToAdd) {
            addedWord.setCode(lastCode);
            lastCode++;
        }
        generatedWordsList.addAll(wordsToAdd);

        if (generatedWordsList.size() < defWordCount) {
            wordsCount = defWordCount - generatedWordsList.size();
            getProcessedWords(generatedWordsList, wordsCount, lastCode);
        }
        defWordCount = 0;
    }

    private void translateWord(GeneratedWordsDto word) {
        try {
            translate(word);
        } catch (GettingTranslateException | InterruptedException | IOException e) {
            e.printStackTrace();
        } catch (LimitExceededException | AuthTranslateException e) {
            log.error(e.getMessage());
            try {
                emailSender.sendSimpleMessage(e.getMessage(), e.getMessage());
            } catch (MailSendException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    private boolean isPartOfSpeechCorrect(String word) {
        log.info("isPartOfSpeechCorrect: the word " + word + " is being checked");
        try {
            List<String> partsOfSpeech;
            partsOfSpeech = partsOfSpeechClient.getPartsOfSpeech(word);
            log.debug("isPartOfSpeechCorrect: parts of speech for the word " + word + " are: " + partsOfSpeech);
            if (partsOfSpeech == null || partsOfSpeech.isEmpty()) {
                return false;
            }
            return Arrays.stream(CorrectPartsOfSpeech.values()).anyMatch(partsOfSpeech::contains);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void translate(GeneratedWordsDto word) throws AuthTranslateException,
            GettingTranslateException,
            LimitExceededException,
            IOException,
            InterruptedException {
        for (Language language : Language.values()) {
            try {
                translateClient.translateWord(word, language);
            } catch (TranslationNotFoundException e) {
                log.error(e.getMessage());
            }
        }
    }
}

