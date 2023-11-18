package com.github.ovorobeva.vocabularywordsservice.service.impl;

import com.github.ovorobeva.vocabularywordsservice.clients.LemmaClient;
import com.github.ovorobeva.vocabularywordsservice.clients.ProfanityCheckerClient;
import com.github.ovorobeva.vocabularywordsservice.clients.WordsClient;
import com.github.ovorobeva.vocabularywordsservice.enums.CorrectPartsOfSpeech;
import com.github.ovorobeva.vocabularywordsservice.enums.ExcludedPartsOfSpeech;
import com.github.ovorobeva.vocabularywordsservice.enums.IncludedPartsOfSpeech;
import com.github.ovorobeva.vocabularywordsservice.model.generated.GeneratedWordsDto;
import com.github.ovorobeva.vocabularywordsservice.model.lemmas.LemmaDto;
import com.github.ovorobeva.vocabularywordsservice.model.lemmas.LemmaRequest;
import com.github.ovorobeva.vocabularywordsservice.model.words.RandomWordsDto;
import com.github.ovorobeva.vocabularywordsservice.properties.WordsProperties;
import com.github.ovorobeva.vocabularywordsservice.service.PartsOfSpeechService;
import com.github.ovorobeva.vocabularywordsservice.service.TranslateService;
import com.github.ovorobeva.vocabularywordsservice.service.WordsFetchingServiceExternal;
import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.github.ovorobeva.vocabularywordsservice.consrtants.Constants.*;

/**
 * Service is to process words returned from external client
 * and prepare them for saving into database
 *
 * @author Olga Vorobeva 2020
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class WordsFetchingServiceExternalImpl implements WordsFetchingServiceExternal {

    final String SELDOM_WORD = "Word is not in use";

    private final WordsClient wordsClient;
    private final PartsOfSpeechService partsOfSpeechService;
    private final TranslateService translateServiceImpl;
    private final ProfanityCheckerClient profanityCheckerClient;
    private final LemmaClient lemmaClient;
    private final WordsProperties wordsProperties;

    private final Pattern pattern = Pattern.compile("[^a-zA-Z[-]]");
    private Integer wordsCount = 0;
    private Integer lastCode = 0;

    @Override
    public Set<GeneratedWordsDto> getProcessedWords(Integer wordsCount, Integer lastCode) {
        final Set<GeneratedWordsDto> generatedWordsSet = new HashSet<>();
        if (this.wordsCount == 0) {
            this.wordsCount = wordsCount;
        }
        if (this.lastCode == 0) {
            this.lastCode = lastCode;
        }

        while (generatedWordsSet.size() < this.wordsCount) {
            wordsCount = this.wordsCount - generatedWordsSet.size();
            lastCode = this.lastCode;
            generatedWordsSet.addAll(processWords(wordsCount, new AtomicInteger(lastCode)));
        }

        this.wordsCount = 0;
        this.lastCode = 0;
        return generatedWordsSet;
    }

    private Set<String> getRandomWords(final Integer wordsCount) {
        int returnedWords = 0;
        final Set<String> words = new HashSet<>();

        final List<String> includePartOfSpeechList = new ArrayList<>();
        for (IncludedPartsOfSpeech partsOfSpeech : IncludedPartsOfSpeech.values()) {
            includePartOfSpeechList.add(partsOfSpeech.getValue());
        }

        final List<String> excludePartOfSpeechList = new ArrayList<>();
        for (ExcludedPartsOfSpeech partsOfSpeech : ExcludedPartsOfSpeech.values()) {
            excludePartOfSpeechList.add(partsOfSpeech.getValue());
        }

        try {
            final ResponseEntity<List<RandomWordsDto>> response = wordsClient.getWords(MIN_CORPUS_COUNT,
                    MAX_CORPUS_COUNT,
                    MIN_DICTIONARY_COUNT,
                    MAX_DICTIONARY_COUNT,
                    MIN_LENGTH,
                    MAX_LENGTH,
                    includePartOfSpeechList,
                    excludePartOfSpeechList,
                    wordsCount,
                    wordsProperties.getWordsApiKey()
            );
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                response.getBody().forEach(word -> words.add(word.getWord()));
                returnedWords = words.size();
            } else {
                return words;
            }
        } catch (RetryableException e) {
            e.printStackTrace();
            if (e.getCause() instanceof FeignException.TooManyRequests) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                if ((wordsCount - returnedWords) > 0)
                    return getRandomWords(wordsCount - returnedWords);
            }
        }
        return words;
    }

    private Set<GeneratedWordsDto> processWords(Integer wordsCount,
                                                AtomicInteger lastCode) {
        final Set<String> words = getRandomWords(wordsCount);
        final Set<GeneratedWordsDto> wordsToAdd = new HashSet<>();

        log.debug("getWords: Starting removing non-matching words from the list \n" + words);

        words.parallelStream().forEach(word -> checkWord(word).ifPresent(wordsToAdd::add));

        if (!wordsToAdd.isEmpty()) {
            wordsToAdd.forEach(addedWord -> addedWord.setCode(lastCode.getAndIncrement()));
            this.lastCode = lastCode.get();
        }
        return wordsToAdd;
    }

    private Optional<GeneratedWordsDto> checkWord(final String word) {
        final Matcher matcher = pattern.matcher(word);

        if (matcher.find()) {
            log.info("getWords: Removing the word " + word + " because of containing symbol " + matcher.toMatchResult());
        }

        final String lemma = getLemma(word);

        if (lemma.equals(SELDOM_WORD)) {
            log.info("getWords: Removing the word " + lemma + " because of its never using.");
        } else if (!isPartOfSpeechCorrect(lemma)) {
            log.info("getWords: Removing the word " + lemma + " because of the wrong part of speech.");
        } else if (isProfanity(lemma)) {
            log.info("getWords: Removing the word " + lemma + " because of profanity.");
        } else {
            GeneratedWordsDto generatedWord = new GeneratedWordsDto(lemma, 0);
            translateServiceImpl.translateWord(generatedWord);
            return Optional.of(generatedWord);
        }

        return Optional.empty();
    }

    private boolean isPartOfSpeechCorrect(final String word) {
        log.info("isPartOfSpeechCorrect: the word {} is being checked", word);
        final List<String> partsOfSpeech;
        partsOfSpeech = partsOfSpeechService.getPartsOfSpeech(word);
        log.debug("isPartOfSpeechCorrect: parts of speech for the word " + word + " are: " + partsOfSpeech);
        if (partsOfSpeech == null || partsOfSpeech.isEmpty()) {
            return false;
        }
        return Arrays.stream(CorrectPartsOfSpeech.values())
                .anyMatch(correctPartsOfSpeech -> partsOfSpeech.contains(correctPartsOfSpeech.getValue()));
    }

    private String getLemma(String word) {
        log.info("Getting lemma for the word {}", word);

        final LemmaRequest request = new LemmaRequest(word);
        try {
            final ResponseEntity<LemmaDto> response = lemmaClient.getLemma(request);
            if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
                if (Objects.requireNonNull(response.getBody()).getData().getTokens().get(0).getSyncon() == -1)
                    return SELDOM_WORD;
                return response.getBody().getData().getTokens().get(0).getLemma();
            } else {
                return word;
            }
        } catch (RetryableException e) {
            if (e.getCause() instanceof FeignException.BadGateway)
                try {
                    Thread.sleep(15000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            e.printStackTrace();
            return getLemma(word);
        }
    }

    private boolean isProfanity(final String word) {
        try {
            final ResponseEntity<String> response = profanityCheckerClient.isProfanity(word);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody().equals("true");
            } else if (response.getStatusCode().equals(HttpStatus.METHOD_NOT_ALLOWED)) {
                throw new IllegalStateException();
            } else {
                return false;
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return false;
        }
    }
}

