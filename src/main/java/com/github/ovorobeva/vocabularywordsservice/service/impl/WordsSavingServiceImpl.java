package com.github.ovorobeva.vocabularywordsservice.service.impl;

import com.github.ovorobeva.vocabularywordsservice.model.generated.GeneratedWordsDto;
import com.github.ovorobeva.vocabularywordsservice.properties.WordsProperties;
import com.github.ovorobeva.vocabularywordsservice.repositories.WordsRepository;
import com.github.ovorobeva.vocabularywordsservice.service.TranslateService;
import com.github.ovorobeva.vocabularywordsservice.service.WordsFetchingServiceExternal;
import com.github.ovorobeva.vocabularywordsservice.service.WordsSavingService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@EnableScheduling
@Slf4j
@RequiredArgsConstructor
public class WordsSavingServiceImpl implements WordsSavingService {

    private final WordsFetchingServiceExternal wordsFetchingServiceExternalImpl;
    private final WordsRepository wordsRepository;
    private final TranslateService translateService;
    private final WordsProperties wordsProperties;

    int notSaved = 0;

    public synchronized void fillWordsUp(int wordsCount) {
        log.debug("Starting filling database with {} new words.", wordsCount);
        int[] codes = wordsRepository.getCodes();
        int recordsCount = codes.length;
        int code;
        int max = Arrays.stream(codes).max().isPresent() ? Arrays.stream(codes).max().getAsInt() : 0;
        if (recordsCount == 0 || recordsCount == max) {
            code = ++recordsCount;

            Set<GeneratedWordsDto> wordSet;
            wordSet = wordsFetchingServiceExternalImpl.getProcessedWords(wordsCount, code);
            wordSet.forEach(generatedWords -> {
                try {
                    wordsRepository.save(generatedWords);
                } catch (DataIntegrityViolationException e) {
                    notSaved++;
                    log.error(e.getMessage());
                }
            });
            wordsRepository.flush();
        } else {
            saveMissingWords(wordsCount, recordsCount, max, codes);
        }
    }

    @PostConstruct
    public synchronized void fillMissingTranslates() {
        List<GeneratedWordsDto> frenchMissingList = wordsRepository.getGeneratedWordsDtoByFrIsNull();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() ->
                frenchMissingList.parallelStream().forEach(generatedWordsDto -> {
                    translateService.translateWord(generatedWordsDto);
                    wordsRepository.save(generatedWordsDto);
                })
        );
    }

    private void saveMissingWords(int wordsCount,
                                  int recordsCount,
                                  int max,
                                  int[] codes) {
        log.debug(String.format("Start to fill %04d missing words. Last code was %04d. There are "
                + "%04d elements saved", wordsCount, max, recordsCount));
        Set<GeneratedWordsDto> wordList = new HashSet<>();
        wordList = wordsFetchingServiceExternalImpl.getProcessedWords(wordsCount, 0);

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
        Iterator<GeneratedWordsDto> iterator = wordList.iterator();
        int code = 0;
        while (iterator.hasNext()){
            GeneratedWordsDto word = iterator.next();
            if (code < missingCodes.size())
                word.setCode(missingCodes.get(code));
            else word.setCode(++max);
            code++;
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
    @Scheduled(cron = "0 0 04 * * ?", zone = "Europe/Paris")
    private synchronized void defaultFillUp() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            log.debug("filling " + wordsProperties.getDefaultWordCount() + " more words");
            fillWordsUp(wordsProperties.getDefaultWordCount());
        });
    }

}
