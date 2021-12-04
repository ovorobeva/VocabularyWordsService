package com.gitjub.ovorobeva.vocabularywordsservice.service;

import com.gitjub.ovorobeva.vocabularywordsservice.dao.WordsRepository;
import com.gitjub.ovorobeva.vocabularywordsservice.model.generated.GeneratedWordsDto;
import com.gitjub.ovorobeva.vocabularywordsservice.translates.Language;
import com.gitjub.ovorobeva.vocabularywordsservice.translates.TranslateFactory;
import com.gitjub.ovorobeva.vocabularywordsservice.wordsprocessing.WordsHandler;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Service
@Data
public class WordsSavingService {

    private final Object WAITER = new Object();
    @Autowired
    TranslateFactory factory;
    @Autowired
    WordsHandler wordsHandler;
    @Autowired
    WordsRepository wordsRepository;

    private int wordsCount = 0;

    public synchronized void fillWordsUp(int wordsCount) {
        this.wordsCount = wordsCount;
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
            wordList.forEach(generatedWords -> wordsRepository.save(generatedWords));
            wordsRepository.flush();
        } else
            saveMissingWords(wordsCount, recordsCount, max, codes);
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
        wordList.forEach(generatedWords -> wordsRepository.save(generatedWords));
        wordsRepository.flush();
    }

    @PostConstruct
    private void defaultFillUp() {
        if (wordsCount == 0) {
            if (wordsRepository.count() == 0) {
                wordsCount = Integer.parseInt(System.getenv().get("DEFAULT_WORD_COUNT"));
                fillWordsUp(wordsCount);
            }
        }
    }

    @PostConstruct
    private void fillMissingTranslates(){
        List<GeneratedWordsDto> frenchMissingList = wordsRepository.getGeneratedWordsDtoByFrIsNull();
        new Thread(() ->
            frenchMissingList.forEach(generatedWordsDto -> {
                factory.getTranslateClient(Language.FR).translateWord(generatedWordsDto);
                wordsRepository.save(generatedWordsDto);
            })
        ).start();
        }
}
