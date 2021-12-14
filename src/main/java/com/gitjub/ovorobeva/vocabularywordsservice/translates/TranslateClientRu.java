package com.gitjub.ovorobeva.vocabularywordsservice.translates;

import com.gitjub.ovorobeva.vocabularywordsservice.exceptions.AuthTranslateException;
import com.gitjub.ovorobeva.vocabularywordsservice.exceptions.GettingTranslateException;
import com.gitjub.ovorobeva.vocabularywordsservice.exceptions.LimitExceededException;
import com.gitjub.ovorobeva.vocabularywordsservice.exceptions.TooManyRequestsException;
import com.gitjub.ovorobeva.vocabularywordsservice.model.generated.GeneratedWordsDto;
import com.gitjub.ovorobeva.vocabularywordsservice.model.translate.TranslateDto;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.logging.Level;

@Service
public class TranslateClientRu implements TranslateClient {
    @SneakyThrows
    @Override
    public void translateWord(GeneratedWordsDto word){
        URI URI = uriBuilder.queryParam("target_lang", Language.RU.toString().toLowerCase())
                .queryParam("text", word.getEn())
                .build();

        HttpRequest request = requestBuilder
                .uri(URI)
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            TranslateClientRu.logger.log(Level.INFO, "execute: URL is: " + response.uri());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                assert response.body() != null;
                word.setRu(converter.fromJson(response.body(), TranslateDto.class).getTranslations().get(0).getText());
                TranslateClientRu.logger.log(Level.INFO, "execute: Translate for the word " + word.getEn() + " is: " + word.getRu());
            } else if (response.statusCode() == 429 || response.statusCode() == 529) {
                throw new TooManyRequestsException();
            }  else if (response.statusCode() == 456) {
                throw new LimitExceededException("Limit is exceeded, check your account https://www.deepl.com/ru/pro-account/usage");
            }   else if (response.statusCode() == 403) {
                throw new AuthTranslateException("Api key is compromised and needs to be checked in the account details https://www.deepl.com/ru/pro-account/usage");
            } else {
                TranslateClientRu.logger.log(Level.SEVERE, "There is an error during request by link " + response.uri() +
                        " . Error code is: " + response.statusCode() +
                        " Error is: " + response.body());
                throw new GettingTranslateException();
            }
        } catch (TooManyRequestsException e) {
            try {
                Thread.sleep(10000);
                e.printStackTrace();
                translateWord(word);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }

    }

}