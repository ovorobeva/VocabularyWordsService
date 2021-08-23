package com.gitjub.ovorobeva.vocabularywordsservice.controller;

import com.gitjub.ovorobeva.vocabularywordsservice.dto.generated.GeneratedWords;
import com.gitjub.ovorobeva.vocabularywordsservice.service.WordsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class Controller {

@Autowired
    WordsService wordsService;

    @GetMapping("/getwords/{count}")
    public ResponseEntity<List<GeneratedWords>> createUser(@PathVariable int count) {
        wordsService.setWordsCount(count);
        return ResponseEntity.ok().body(wordsService.getWordList());
    }
}
