package com.github.ovorobeva.vocabularywordsservice.translates;

import com.github.ovorobeva.vocabularywordsservice.exceptions.AuthTranslateException;
import com.github.ovorobeva.vocabularywordsservice.exceptions.GettingTranslateException;
import com.github.ovorobeva.vocabularywordsservice.exceptions.LimitExceededException;
import com.github.ovorobeva.vocabularywordsservice.exceptions.TooManyRequestsException;
import com.github.ovorobeva.vocabularywordsservice.model.generated.GeneratedWordsDto;
import com.github.ovorobeva.vocabularywordsservice.model.translate.TranslateDto;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.logging.Level;

//todo: to make constants env
@Service
public class TranslateClientRu extends TranslateClient {
    @Override
    public void translateWord(GeneratedWordsDto word) throws LimitExceededException,
            AuthTranslateException,
            GettingTranslateException,
            InterruptedException,
            IOException {
        final URI URI = uriBuilder.queryParam("target_lang", Language.RU.toString().toLowerCase())
                .queryParam("text", word.getEn())
                .build();

        final HttpRequest request = requestBuilder
                .uri(URI)
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            TranslateClientRu.logger.log(Level.INFO, "execute: URL is: " + response.uri()
                    + "\nstatus code is: " + response.statusCode()
                    + "response is: " + response.body());
            if(TranslateClient.isSuccess(response.statusCode())) {
                assert response.body() != null;
                word.setRu(converter.fromJson(response.body(), TranslateDto.class).getTranslations().get(0).getText().toLowerCase());
                TranslateClientRu.logger.log(Level.INFO, "execute: Translate for the word " + word.getEn() + " is: " + word.getRu());
            }
        } catch (TooManyRequestsException e) {
            Thread.sleep(10000);
            e.printStackTrace();
            translateWord(word);
        }

    }

}