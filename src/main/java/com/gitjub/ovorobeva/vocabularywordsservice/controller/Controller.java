package com.gitjub.ovorobeva.vocabularywordsservice.controller;

import com.gitjub.ovorobeva.vocabularywordsservice.dao.WordsRepository;
import com.gitjub.ovorobeva.vocabularywordsservice.model.generated.GeneratedWords;
import com.gitjub.ovorobeva.vocabularywordsservice.service.WordsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityNotFoundException;
import java.util.*;

@RestController
public class Controller {

    @Autowired
    WordsService wordsService;
    @Autowired
    WordsRepository wordsRepository;

    @GetMapping("/getwords/{count}")
    public ResponseEntity<List<GeneratedWords>> getWords(@PathVariable int count) {
        Set<GeneratedWords> generatedWordsSet = new HashSet<>(count);
        Random random = new Random();
        System.out.println("count is: " + count);
        for (byte i = 0; i < count; i++) {
            int id = random.nextInt((int) (wordsRepository.count() - 1));
            if (wordsRepository.findById(id).isEmpty())
                throw new EntityNotFoundException("The blog with ID = " + id + " doesn't exist");
            GeneratedWords generatedWords = wordsRepository.getById(id);
            generatedWordsSet.add(generatedWords);
        }
        new Thread(() -> {
            wordsService.setWordsCount(5);
            wordsService.getWordList();
        }).start();
        List<GeneratedWords> generatedWordList = new ArrayList<>(count);
        generatedWordList.addAll(generatedWordsSet);
        return ResponseEntity.ok().body(generatedWordList);
    }
}
