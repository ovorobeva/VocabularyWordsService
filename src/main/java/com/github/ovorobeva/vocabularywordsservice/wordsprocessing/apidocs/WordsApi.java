package com.github.ovorobeva.vocabularywordsservice.wordsprocessing.apidocs;

import com.github.ovorobeva.vocabularywordsservice.model.words.RandomWordsDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface WordsApi {

    @RequestMapping(
            value = {"/randomWords"},
            method = {RequestMethod.GET}
    )
    ResponseEntity<List<RandomWordsDto>> getWords(@RequestParam("minCorpusCount") String minCorpusCount,
                                                  @RequestParam("maxCorpusCount") String maxCorpusCount,
                                                  @RequestParam("minDictionaryCount") String minDictionaryCount,
                                                  @RequestParam("maxDictionaryCount") String maxDictionaryCount,
                                                  @RequestParam("minLength") String minLength,
                                                  @RequestParam("maxLength") String maxLength,
                                                  @RequestParam("includePartOfSpeech[]") List<String> includePartOfSpeech,
                                                  @RequestParam("excludePartOfSpeech[]") List<String> excludePartOfSpeechList,
                                                  @RequestParam("limit") int wordsCount,
                                                  @RequestParam("api_key") String api_key);

}
