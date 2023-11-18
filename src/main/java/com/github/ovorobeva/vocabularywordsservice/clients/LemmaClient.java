package com.github.ovorobeva.vocabularywordsservice.clients;

import com.github.ovorobeva.vocabularywordsservice.model.lemmas.LemmaDto;
import com.github.ovorobeva.vocabularywordsservice.model.lemmas.LemmaRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "lemma", url = "https://try.expert.ai/analysis/standard/en")
public interface LemmaClient {
    @RequestMapping(
            value = {"/disambiguation"},
            method = {RequestMethod.POST}
    )
    ResponseEntity<LemmaDto> getLemma(@RequestBody LemmaRequest request);
}
