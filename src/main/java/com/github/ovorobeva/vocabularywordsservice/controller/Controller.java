package com.github.ovorobeva.vocabularywordsservice.controller;

import com.github.ovorobeva.vocabularywordsservice.model.generated.GeneratedWordsDto;
import com.github.ovorobeva.vocabularywordsservice.service.WordsRetrievingService;
import jakarta.ws.rs.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@RestController
public class Controller {

    @Value("${default.words.count}")
    int wordCount;
    @Autowired
    private WordsRetrievingService wordsRetrievingService;

    @GetMapping("/getwords/{count}")
    public ResponseEntity<Set<GeneratedWordsDto>> getWords(@PathVariable int count) {
        if (count == 0) throw new BadRequestException("Enter word count");
        log.info(count + " words were requested");
        Set<GeneratedWordsDto> generatedWordList = new HashSet<>(count);
        wordsRetrievingService.getRandomWords(count, generatedWordList);
        log.debug("Words are retrieved: " + generatedWordList);
        return ResponseEntity.ok().body(generatedWordList);
    }
}
