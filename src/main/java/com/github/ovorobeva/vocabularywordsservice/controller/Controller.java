package com.github.ovorobeva.vocabularywordsservice.controller;

import com.github.ovorobeva.vocabularywordsservice.model.generated.GeneratedWordsDto;
import com.github.ovorobeva.vocabularywordsservice.service.impl.WordsFetchingServiceInternalImpl;
import jakarta.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@Slf4j
@RestController
@RequiredArgsConstructor
public class Controller {

    private final WordsFetchingServiceInternalImpl wordsFetchingServiceInternalImpl;

    @GetMapping("/getwords/{count}")
    public ResponseEntity<Set<GeneratedWordsDto>> getWords(@PathVariable int count) {
        if (count == 0) throw new BadRequestException("Enter word count");
        log.info(count + " words were requested");//todo to get rid of this check
        return ResponseEntity.ok().body(wordsFetchingServiceInternalImpl.getRandomWords(count));
    }
}
