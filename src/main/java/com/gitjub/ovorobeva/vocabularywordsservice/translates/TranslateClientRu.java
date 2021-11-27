package com.gitjub.ovorobeva.vocabularywordsservice.translates;

import com.gitjub.ovorobeva.vocabularywordsservice.exceptions.TooManyRequestsException;
import com.gitjub.ovorobeva.vocabularywordsservice.model.generated.GeneratedWordsDto;
import com.gitjub.ovorobeva.vocabularywordsservice.model.translate.TranslateDto;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

@Service
public class TranslateClientRu implements TranslateClient{

    @SneakyThrows
    @Override
    public void translateWord(GeneratedWordsDto word) {

        Map<String, String> apiVariables = new HashMap<>();
        apiVariables.put("sourceLanguageCode", "en");
        apiVariables.put("targetLanguageCode", "ru");
        apiVariables.put("texts", word.getEn());


        String requestBody = objectMapper
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(apiVariables);

        TranslateClientFr.logger.log(Level.INFO, "translate client: body is: " + requestBody);


        HttpRequest request = requestBuilder
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            TranslateClientRu.logger.log(Level.INFO, "execute: URL is: " + response.uri());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                assert response.body() != null;

                word.setRu(converter.fromJson(response.body(), TranslateDto.class).getTranslations().get(0).getText());
                TranslateClientRu.logger.log(Level.INFO, "execute: Translate for the word " + word.getEn() + " is: " + word.getRu());
            } else if (response.statusCode() == 429) {
                throw new TooManyRequestsException();
            } else if (response.statusCode() == 405) {
                Thread.sleep(10000);
                translateWord(word);
            } else {
                TranslateClientRu.logger.log(Level.SEVERE, "There is an error during request by link " + response.uri() +
                        " . Error code is: " + response.statusCode() +
                        " Error is: " + response.body());
                word.setRu("Translation is not found");
            }
        } catch (IOException | InterruptedException e) {
            TranslateClientRu.logger.log(Level.SEVERE, "Something went wrong. Error is: " + e.getMessage());
            word.setRu("Translation is not found");
            e.printStackTrace();
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