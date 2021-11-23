package com.gitjub.ovorobeva.vocabularywordsservice.controller;

import com.gitjub.ovorobeva.vocabularywordsservice.model.generated.GeneratedWordsDto;
import com.gitjub.ovorobeva.vocabularywordsservice.service.WordsRetrievingService;
import com.gitjub.ovorobeva.vocabularywordsservice.service.WordsSavingService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping("/getwords/{count}")
    public ResponseEntity<Set<GeneratedWordsDto>> getWords(@PathVariable int count) {
        Set<GeneratedWordsDto> generatedWordList = new HashSet<>(count);
        wordsRetrievingService.getRandomWords(count, generatedWordList);
        Thread fillingThread = new Thread(() -> {
            int wordCount = Integer.parseInt(System.getenv().get("DEFAULT_WORD_COUNT"));
            wordsSavingService.fillWordsUp(wordCount);
        });
        fillingThread.start();
        return ResponseEntity.ok().body(generatedWordList);
    }
}
