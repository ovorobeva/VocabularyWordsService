package com.github.ovorobeva.vocabularywordsservice.controller;

import com.github.ovorobeva.vocabularywordsservice.model.generated.GeneratedWordsDto;
import com.github.ovorobeva.vocabularywordsservice.service.WordsRetrievingService;
import com.github.ovorobeva.vocabularywordsservice.service.WordsSavingService;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityNotFoundException;
import java.util.HashSet;
import java.util.Set;

@RestController
@Log4j2
public class Controller {

    private final Logger logger = LogManager.getLogger();
    @Autowired
    private WordsSavingService wordsSavingService;
    @Value("${default.words.count}")
    int wordCount;
    @Autowired
    private WordsRetrievingService wordsRetrievingService;

    @GetMapping("/getwords/{count}")
    public ResponseEntity<Set<GeneratedWordsDto>> getWords(@PathVariable int count) {
        logger.info(count + " words were requested");
        Set<GeneratedWordsDto> generatedWordList = new HashSet<>(count);
        wordsRetrievingService.getRandomWords(count, generatedWordList);
        logger.debug("Words are retrieved: " + generatedWordList);
        if (generatedWordList.isEmpty()) throw new EntityNotFoundException("Words are not found");
        Thread fillingThread = new Thread(() -> {
            logger.debug("Start filling the database");
            wordsSavingService.fillWordsUp(wordCount);
        });
        fillingThread.start();
        return ResponseEntity.ok().body(generatedWordList);
    }
}
