package com.gitjub.ovorobeva.vocabularywordsservice.service;

import com.gitjub.ovorobeva.vocabularywordsservice.dao.WordsRepository;
import com.gitjub.ovorobeva.vocabularywordsservice.model.generated.GeneratedWords;
import com.gitjub.ovorobeva.vocabularywordsservice.translates.TranslateService;
import com.gitjub.ovorobeva.vocabularywordsservice.wordsprocessing.WordsProcessing;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Data
public class WordsSavingService {
    @Autowired
    TranslateService translateService;
    @Autowired
    WordsProcessing wordsProcessing;
    @Autowired
    WordsRepository wordsRepository;

    private int wordsCount = 0;

    public synchronized void fillWordsUp(int wordsCount) {
        this.wordsCount = wordsCount;
        if (wordsCount == 0) {
            if (wordsRepository.count() == 0)
                wordsCount = Integer.parseInt(System.getenv().get("DEFAULT_WORD_COUNT"));
            else return;
        }
        int[] codes = wordsRepository.getCodes();
        int recordsCount = codes.length;
        int code;
        int max = 0;
        if (Arrays.stream(codes).max().isPresent()) max = Arrays.stream(codes).max().getAsInt();
        if (recordsCount == 0 || recordsCount == max) {
            code = ++recordsCount;
            wordsProcessing.setCode(code);
            wordsProcessing.setWordsCount(wordsCount);
            List<GeneratedWords> generatedWordsList = translateService.getTranslates(wordsProcessing);
            generatedWordsList.forEach(generatedWords -> wordsRepository.save(generatedWords));
            wordsRepository.flush();
        } else
            saveMissingWords(wordsCount, recordsCount, max, codes);
    }

    private void saveMissingWords(int wordsCount, int recordsCount, int max, int[] codes) {
        wordsProcessing.setCode(0);
        wordsProcessing.setWordsCount(wordsCount);
        List<GeneratedWords> generatedWordsList = translateService.getTranslates(wordsProcessing);
        int start = 0;
        int end = recordsCount - 1;
        int count;
        int limit = Math.min(wordsCount, max - recordsCount);
        System.out.println("wordscount = " + wordsCount + "\nmax = " + max + "\nrecordscount = " + recordsCount + "\nlimit = " + limit + "\n");
        List<Integer> missingCodes = new ArrayList<>(limit);
        for (int i = 0; missingCodes.size() < limit; i++) {
            while (end - start > 1) {
                count = (end - start) / 2 + start;
                System.out.println("i = " + i + "; start = " + start + "; end = " + end + "; count = " + count
                        + "\ncodes[" + count + "] = " + codes[count] + " compairing to " + (count + 1 + missingCodes.size()));
                if (codes[count] == count + 1 + missingCodes.size()) {
                    start = count;
                } else {
                    end = count;
                }
            }
            System.out.println("end - start  = " + end + " - " + start + " = " + (end - start)
                    + " that is < 1.\nCode["
                    + start + "] = " + codes[start] + " compairing to " + (start + 1 + missingCodes.size()));
            if (codes[start] == start + 1 + missingCodes.size())
                start++;
            else {
                missingCodes.add(start + 1 + missingCodes.size());
            }
            System.out.println(missingCodes);
            end = recordsCount - 1;
            System.out.println("end of the " + i + "th iteration");
            //todo: to add found numbers
        }
        System.out.println(missingCodes);

        for (int i = 0; i < generatedWordsList.size(); i++) {
            if (i < missingCodes.size())
                generatedWordsList.get(i).setCode(missingCodes.get(i));
            else generatedWordsList.get(i).setCode(++max);
        }
        generatedWordsList.forEach(generatedWords -> wordsRepository.save(generatedWords));
        wordsRepository.flush();
    }

    @PostConstruct
    private void defaultFillUp() {
        if (wordsCount == 0) {
            if (wordsRepository.count() == 0) {
                wordsCount = Integer.parseInt(System.getenv().get("DEFAULT_WORD_COUNT"));
                fillWordsUp(wordsCount);
            } else return;
        }
    }
}
