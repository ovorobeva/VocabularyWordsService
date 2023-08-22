package com.github.ovorobeva.vocabularywordsservice.clients;

import com.github.ovorobeva.vocabularywordsservice.model.partsofspeech.PartsOfSpeechDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(name = "parts-of-speech", decode404 = true, url = "https://api.dictionaryapi.dev/api/v2/entries/")
public interface PartsOfSpeechClient {
    @RequestMapping(
            value = {"/en/{word}"},
            method = {RequestMethod.GET}
    )
    ResponseEntity<List<PartsOfSpeechDto>> findPartsOfSpeech(@PathVariable("word") String word);
}
