package com.gitjub.ovorobeva.vocabularywordsservice.controller;

import com.gitjub.ovorobeva.vocabularywordsservice.model.generated.GeneratedWords;
import com.gitjub.ovorobeva.vocabularywordsservice.service.WordsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class Controller {

    @Autowired
    WordsService wordsService;

    @GetMapping("/getwords/{count}")
    public ResponseEntity<List<GeneratedWords>> getWords(@PathVariable int count) {
        wordsService.setWordsCount(count);
        List<GeneratedWords> generatedWordList = new ArrayList<>(count);
        generatedWordList.addAll(wordsService.getWords());
        new Thread(() -> {
            wordsService.setWordsCount(5);
            wordsService.fillWordsUp();
        }).start();
        return ResponseEntity.ok().body(generatedWordList);
    }
}
