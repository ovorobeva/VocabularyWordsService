package com.github.ovorobeva.vocabularywordsservice.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "profanity-checker", dismiss404 = true, url = "https://www.purgomalum.com/service/")
public interface ProfanityCheckerClient {
    @RequestMapping(
            value = {"/containsprofanity/"},
            method = {RequestMethod.GET}
    )
    ResponseEntity<String> isProfanity(@RequestParam("text") String word);
}
