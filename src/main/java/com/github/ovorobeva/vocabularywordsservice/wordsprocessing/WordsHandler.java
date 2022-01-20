package com.github.ovorobeva.vocabularywordsservice.wordsprocessing;

import com.github.ovorobeva.vocabularywordsservice.emailsender.EmailSender;
import com.github.ovorobeva.vocabularywordsservice.exceptions.AuthTranslateException;
import com.github.ovorobeva.vocabularywordsservice.exceptions.GettingTranslateException;
import com.github.ovorobeva.vocabularywordsservice.exceptions.LimitExceededException;
import com.github.ovorobeva.vocabularywordsservice.model.generated.GeneratedWordsDto;
import com.github.ovorobeva.vocabularywordsservice.translates.Language;
import com.github.ovorobeva.vocabularywordsservice.translates.TranslateClient;
import com.github.ovorobeva.vocabularywordsservice.translates.TranslateFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSendException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class WordsHandler {

    protected final Logger logger = LogManager.getLogger();

    @Autowired
    private WordsClient wordsClient;
    @Autowired
    private PartsOfSpeechClient partsOfSpeechClient;
    @Autowired
    private TranslateFactory factory;
    @Autowired
    private ProfanityCheckerClient profanityCheckerClient;
    @Autowired
    private LemmaClient lemmaClient;
    @Autowired
    private EmailSender emailSender;
    private int defWordCount = 0;


    public void getProcessedWords(List<GeneratedWordsDto> generatedWordsList,
                                  int wordsCount,
                                  int lastCode) throws InterruptedException {
        if (defWordCount == 0) defWordCount = wordsCount;
        List<String> words = wordsClient.getRandomWords(wordsCount);
        List<String> checkedWords = new ArrayList<>();
        Iterator<String> iterator = words.iterator();

        logger.debug("getWords: Starting removing non-matching words from the list \n" + words);

        ExecutorService executor = Executors.newFixedThreadPool(wordsCount);
        while (iterator.hasNext()) {
            String word = iterator.next();
            Pattern pattern = Pattern.compile("[^a-zA-Z[-]]");

            executor.execute(() -> {
                Matcher matcher = pattern.matcher(word);
                String lemma;
                if (matcher.find()) {
                    logger.info("getWords: Removing the word " + word + " because of containing symbol " + matcher.toMatchResult());
                } else {
                    lemma = lemmaClient.getLemma(word);
                    if (lemma.equals(LemmaClient.SELDOM_WORD)) {
                        logger.info("getWords: Removing the word " + lemma + " because of its never using.");
                    } else if (!isPartOfSpeechCorrect(lemma)) {
                        logger.info("getWords: Removing the word " + lemma + " because of the wrong part of speech.");
                    } else if (profanityCheckerClient.isProfanity(lemma)) {
                        logger.info("getWords: Removing the word " + lemma + " because of profanity.");
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
            try {
                translate(word);
                generatedWordsList.add(word);
            } catch (GettingTranslateException | InterruptedException | IOException e) {
                e.printStackTrace();
            } catch (LimitExceededException | AuthTranslateException e) {
                logger.error(e.getMessage());
                try {
                    emailSender.sendSimpleMessage(e.getMessage(), e.getMessage());
                } catch (MailSendException ex) {
                    ex.printStackTrace();
                }
                e.printStackTrace();
            }
            lastCode++;
        }

        if (generatedWordsList.size() < defWordCount) {
            wordsCount = defWordCount - generatedWordsList.size();
            getProcessedWords(generatedWordsList, wordsCount, lastCode);
        }
    }

    private boolean isPartOfSpeechCorrect(String word) {
        logger.info("isPartOfSpeechCorrect: the word " + word + " is being checked");
        boolean isCorrect = true;
        List<String> partsOfSpeech = null;
        try {
            partsOfSpeech = partsOfSpeechClient.getPartsOfSpeech(word);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        logger.debug("isPartOfSpeechCorrect: parts of speech for the word " + word + " are: " + partsOfSpeech);

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
                logger.info("isPartOfSpeechCorrect: The word " + word + " is to be removed because of part of speech: " + partOfSpeech);
                isCorrect = false;
                break;
            }
        }
        return isCorrect;
    }

    private void translate(GeneratedWordsDto word) throws AuthTranslateException, GettingTranslateException, LimitExceededException, IOException, InterruptedException {
        TranslateClient translateClientRu = factory.getTranslateClient(Language.RU);
        TranslateClient translateClientFr = factory.getTranslateClient(Language.FR);
        TranslateClient translateClientCz = factory.getTranslateClient(Language.CS);

        translateClientRu.translateWord(word);
        translateClientFr.translateWord(word);
        translateClientCz.translateWord(word);
    }
}

