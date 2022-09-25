package com.github.ovorobeva.vocabularywordsservice.wordsprocessing.apidocs;

import com.github.ovorobeva.vocabularywordsservice.model.lemmas.LemmaDto;
import com.github.ovorobeva.vocabularywordsservice.model.lemmas.LemmaRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public interface LemmaApi {
    @RequestMapping(
            value = {"/disambiguation"},
            method = {RequestMethod.POST}
    )
    ResponseEntity<LemmaDto> getLemma(@RequestBody LemmaRequest request);
}
