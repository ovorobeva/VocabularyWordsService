package com.github.ovorobeva.vocabularywordsservice.service;

import com.github.ovorobeva.vocabularywordsservice.dao.WordsRepository;
import com.github.ovorobeva.vocabularywordsservice.emailsender.EmailSender;
import com.github.ovorobeva.vocabularywordsservice.exceptions.AuthTranslateException;
import com.github.ovorobeva.vocabularywordsservice.exceptions.GettingTranslateException;
import com.github.ovorobeva.vocabularywordsservice.exceptions.LimitExceededException;
import com.github.ovorobeva.vocabularywordsservice.model.generated.GeneratedWordsDto;
import com.github.ovorobeva.vocabularywordsservice.translates.Language;
import com.github.ovorobeva.vocabularywordsservice.translates.TranslateFactory;
import com.github.ovorobeva.vocabularywordsservice.wordsprocessing.WordsHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mail.MailSendException;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@EnableScheduling
@Slf4j
public class WordsSavingService {

    @Value("${default.words.count}")
    int defaultWordCount;

    int notSaved = 0;

    @Autowired
    private TranslateFactory factory;
    @Autowired
    private WordsHandler wordsHandler;
    @Autowired
    private WordsRepository wordsRepository;
    @Autowired
    private EmailSender emailSender;

    public synchronized void fillWordsUp(int wordsCount) {
        int[] codes = wordsRepository.getCodes();
        int recordsCount = codes.length;
        int code;
        int max = 0;
        if (Arrays.stream(codes).max().isPresent()) max = Arrays.stream(codes).max().getAsInt();
        if (recordsCount == 0 || recordsCount == max) {
            code = ++recordsCount;

            List<GeneratedWordsDto> wordList = new LinkedList<>();
            try {
                wordsHandler.getProcessedWords(wordList, wordsCount, code);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            wordList.forEach(generatedWords -> {
                try {
                    wordsRepository.save(generatedWords);
                } catch (DataIntegrityViolationException e){
                    notSaved++;
                    log.error(e.getMessage());
                }
            });
            wordsRepository.flush();
        } else
            saveMissingWords(wordsCount, recordsCount, max, codes);
    }

    @PostConstruct
    public synchronized void fillMissingTranslates() {
        List<GeneratedWordsDto> frenchMissingList = wordsRepository.getGeneratedWordsDtoByFrIsNull();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() ->
                frenchMissingList.forEach(generatedWordsDto -> {
                    try {
                        factory.getTranslateClient(Language.FR).translateWord(generatedWordsDto);
                        wordsRepository.save(generatedWordsDto);
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
                })
        );
    }

    private synchronized void saveMissingWords(int wordsCount,
                                               int recordsCount,
                                               int max,
                                               int[] codes) {
        List<GeneratedWordsDto> wordList = new LinkedList<>();
        try {
            wordsHandler.getProcessedWords(wordList, wordsCount, 0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int start = 0;
        int end = recordsCount - 1;
        int count;
        int limit = Math.min(wordsCount, max - recordsCount);
        List<Integer> missingCodes = new ArrayList<>(limit);
        while (missingCodes.size() < limit) {
            while (end - start > 1) {
                count = (end - start) / 2 + start;
                if (codes[count] == count + 1 + missingCodes.size()) {
                    start = count;
                } else {
                    end = count;
                }
            }
            if (codes[start] == start + 1 + missingCodes.size())
                start++;
            else {
                missingCodes.add(start + 1 + missingCodes.size());
            }
            end = recordsCount - 1;
        }

        for (int i = 0; i < wordList.size(); i++) {
            if (i < missingCodes.size())
                wordList.get(i).setCode(missingCodes.get(i));
            else wordList.get(i).setCode(++max);
        }
        wordList.forEach(generatedWords -> {
            if (wordsRepository.findByCode(generatedWords.getCode()).isEmpty())
                try {
                    wordsRepository.save(generatedWords);
                } catch (DataIntegrityViolationException e) {
                    log.error(e.getMessage());
                    notSaved++;
                }
        });
        wordsRepository.flush();
    }

    @PostConstruct
    @Scheduled(cron = "0 59 17 * * ?", zone = "Europe/Paris")
    private void defaultFillUp() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            log.debug("filling " + defaultWordCount + " more words");
            fillWordsUp(defaultWordCount);
        });
    }

}
