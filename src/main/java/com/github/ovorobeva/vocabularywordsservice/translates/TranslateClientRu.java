package com.github.ovorobeva.vocabularywordsservice.translates;

import com.github.ovorobeva.vocabularywordsservice.exceptions.*;
import com.github.ovorobeva.vocabularywordsservice.model.generated.GeneratedWordsDto;
import com.github.ovorobeva.vocabularywordsservice.model.translate.TranslateDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
@Slf4j
public class TranslateClientRu extends TranslateClient {
    @Override
    public void translateWord(GeneratedWordsDto word) throws LimitExceededException,
            AuthTranslateException,
            GettingTranslateException,
            InterruptedException,
            IOException, TranslationNotFoundException {
        final URI URI = uriBuilder.queryParam("target_lang", Language.RU.toString().toLowerCase())
                .queryParam("text", word.getEn())
                .build();

        final HttpRequest request = requestBuilder
                .uri(URI)
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            log.debug("execute: URL is: " + response.uri()
                    + "\nstatus code is: " + response.statusCode()
                    + "response is: " + response.body());
            if (isSuccess(response.statusCode())) {
                if (response.body() == null) {
                    throw new TranslationNotFoundException("Cz translation for the word "
                            + word.getEn()
                            + " is not found.");
                } else {                word.setRu(converter.fromJson(response.body(), TranslateDto.class).getTranslations().get(0).getText().toLowerCase());
                log.info("execute: Translate for the word " + word.getEn() + " is: " + word.getRu());
            }}
        } catch (TooManyRequestsException e) {
            Thread.sleep(10000);
            e.printStackTrace();
            translateWord(word);
        }

    }

}