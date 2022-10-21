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

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service is to process words returned from external client
 * and prepare them for saving into database
 *
 * @author Olga Vorobeva 2020
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class WordsFetchingServiceExternalImpl{

    private final WordsClient wordsClient;
    private final PartsOfSpeechClient partsOfSpeechClient;
    private final TranslateClient translateClient;
    private final ProfanityCheckerClient profanityCheckerClient;
    private final LemmaClient lemmaClient;
    private final EmailSender emailSender;

    private final Pattern pattern = Pattern.compile("[^a-zA-Z[-]]");
    private int wordsCount = 0;
    private int lastCode = 0;

    /**
     * Fetches words from external API, processes them by checking on profanity and part of speech,
     * turning them into infinitive and returnes them with translates.
     *
     * @param wordsCount required number of words to fetch
     * @param lastCode last existing code in the database
     * @throws InterruptedException when getting random words from the external client
     */
    public Set<GeneratedWordsDto> getProcessedWords(int wordsCount, int lastCode)
            throws InterruptedException {
        final Set<GeneratedWordsDto> generatedWordsSet = new HashSet<>();
        if (this.wordsCount == 0) {
            this.wordsCount = wordsCount;
        }
        if (this.lastCode == 0) {
            this.lastCode = lastCode;
        }

        while (generatedWordsSet.size() < this.wordsCount){
            wordsCount = this.wordsCount - generatedWordsSet.size();
            lastCode = this.lastCode;
            generatedWordsSet.addAll(processWords(wordsCount, lastCode));
        }
        this.wordsCount = 0;
        this.lastCode = 0;
        return generatedWordsSet;
    }

    private Set<GeneratedWordsDto> processWords(int wordsCount,
                                                int lastCode) throws InterruptedException {
        final Set<GeneratedWordsDto> generatedWordsList = new HashSet<>();
        Set<String> words = wordsClient.getRandomWords(wordsCount);
        List<GeneratedWordsDto> wordsToAdd = new ArrayList<>();

        log.debug("getWords: Starting removing non-matching words from the list \n" + words);

        words.parallelStream().forEach(word -> checkWord(word).ifPresent(wordsToAdd::add));

        if (!wordsToAdd.isEmpty()) {
            for (GeneratedWordsDto addedWord : wordsToAdd) {
                addedWord.setCode(lastCode);
                lastCode++;
            }
            generatedWordsList.addAll(wordsToAdd);
            this.lastCode = lastCode;
        }
        return generatedWordsList;
    }

    private Optional<GeneratedWordsDto> checkWord(final String word){
            final Matcher matcher = pattern.matcher(word);
            final String lemma;
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
                    GeneratedWordsDto generatedWord = new GeneratedWordsDto(lemma, 0);
                    translateWord(generatedWord);
                    return Optional.of(generatedWord);
                }
            }
            return Optional.empty();
    }

    private void translateWord(final GeneratedWordsDto word) {
        try {
            for (Language language : Language.values()) {
                    translateClient.translateWord(word, language);
            }
        } catch (GettingTranslateException | InterruptedException e) {
            e.printStackTrace();
        } catch (LimitExceededException | AuthTranslateException | TranslationNotFoundException e ) {
            log.error(e.getMessage());
            try {
                emailSender.sendSimpleMessage(e.getMessage(), e.getMessage());
            } catch (MailSendException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    private boolean isPartOfSpeechCorrect(final String word) {
        log.info("isPartOfSpeechCorrect: the word " + word + " is being checked");
        try {
            List<String> partsOfSpeech;
            partsOfSpeech = partsOfSpeechClient.getPartsOfSpeech(word);
            log.debug("isPartOfSpeechCorrect: parts of speech for the word " + word + " are: " + partsOfSpeech);
            if (partsOfSpeech == null || partsOfSpeech.isEmpty()) {
                return false;
            }
            return Arrays.stream(CorrectPartsOfSpeech.values())
                    .anyMatch(correctPartsOfSpeech -> partsOfSpeech.contains(correctPartsOfSpeech.getValue()));
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

}
