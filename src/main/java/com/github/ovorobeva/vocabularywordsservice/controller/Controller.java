package com.github.ovorobeva.vocabularywordsservice.controller;

import com.github.ovorobeva.vocabularywordsservice.model.generated.GeneratedWordsDto;
import com.github.ovorobeva.vocabularywordsservice.service.WordsRetrievingService;
import com.github.ovorobeva.vocabularywordsservice.service.WordsSavingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Set;

@RestController
public class Controller {

    @Autowired
    WordsSavingService wordsSavingService;
    @Autowired
    WordsRetrievingService wordsRetrievingService;
    @Value("${default.words.count}")
    int wordCount;

    @GetMapping("/getwords/{count}")
    public ResponseEntity<Set<GeneratedWordsDto>> getWords(@PathVariable int count) {
        Set<GeneratedWordsDto> generatedWordList = new HashSet<>(count);
        wordsRetrievingService.getRandomWords(count, generatedWordList);
        Thread fillingThread = new Thread(() -> {
            wordsSavingService.fillWordsUp(wordCount);
        });
        fillingThread.start();
        return ResponseEntity.ok().body(generatedWordList);
    }
}
