package com.gitjub.ovorobeva.vocabularywordsservice.controller;

import com.gitjub.ovorobeva.vocabularywordsservice.model.generated.GeneratedWords;
import com.gitjub.ovorobeva.vocabularywordsservice.service.WordsService;
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
    WordsService wordsService;

    @GetMapping("/getwords/{count}")
    public ResponseEntity<Set<GeneratedWords>> getWords(@PathVariable int count) {
        wordsService.setWordsCount(count);
        Set<GeneratedWords> generatedWordList = new HashSet<>(count);
        wordsService.getRandomWords(generatedWordList);
        Thread fillingThread = new Thread(() -> {
            int wordCount = Integer.parseInt(System.getenv().get("DEFAULT_WORD_COUNT"));
            wordsService.setWordsCount(wordCount);
            wordsService.fillWordsUp();
        });
        fillingThread.start();
        return ResponseEntity.ok().body(generatedWordList);
    }
}
