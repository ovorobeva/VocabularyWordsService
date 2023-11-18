package com.github.ovorobeva.vocabularywordsservice.clients;

import com.github.ovorobeva.vocabularywordsservice.model.translate.TranslateDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "translation", url = "https://api-free.deepl.com/v2/")
public interface TranslateClient {
    @RequestMapping(
            value = {"/translate"},
            method = {RequestMethod.GET}
    )
    ResponseEntity<TranslateDto> getTranslate(@RequestParam("text") String wordToTranslate,
                                              @RequestParam("auth_key") String apiKey,
                                              @RequestParam("source_lang") String sourceLanguage,
                                              @RequestParam("target_lang") String targetLanguage);
}
