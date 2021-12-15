package com.gitjub.ovorobeva.vocabularywordsservice.translates;

import com.gitjub.ovorobeva.vocabularywordsservice.exceptions.AuthTranslateException;
import com.gitjub.ovorobeva.vocabularywordsservice.exceptions.GettingTranslateException;
import com.gitjub.ovorobeva.vocabularywordsservice.exceptions.LimitExceededException;
import com.gitjub.ovorobeva.vocabularywordsservice.exceptions.TooManyRequestsException;
import com.gitjub.ovorobeva.vocabularywordsservice.model.generated.GeneratedWordsDto;
import com.gitjub.ovorobeva.vocabularywordsservice.model.translate.TranslateDto;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.logging.Level;

@Service
public class TranslateClientCz extends TranslateClient{

    @Override
    public void translateWord(GeneratedWordsDto word) throws LimitExceededException,
            AuthTranslateException,
            GettingTranslateException,
            InterruptedException,
            IOException {
        final URI URI = uriBuilder.queryParam("target_lang", Language.CS.toString().toLowerCase())
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
                word.setCz(converter.fromJson(response.body(), TranslateDto.class).getTranslations().get(0).getText().toLowerCase());
                TranslateClientRu.logger.log(Level.INFO, "execute: Translate for the word " + word.getEn() + " is: " + word.getCz());
            }
        } catch (TooManyRequestsException e) {
            Thread.sleep(10000);
            e.printStackTrace();
            translateWord(word);
        }

    }
}