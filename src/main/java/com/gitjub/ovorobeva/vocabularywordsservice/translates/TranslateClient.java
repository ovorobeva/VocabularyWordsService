package com.gitjub.ovorobeva.vocabularywordsservice.translates;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gitjub.ovorobeva.vocabularywordsservice.dto.generated.GeneratedWords;
import com.gitjub.ovorobeva.vocabularywordsservice.dto.translation.Translate;
import com.gitjub.ovorobeva.vocabularywordsservice.exceptions.TooManyRequestsException;
import com.google.gson.Gson;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class TranslateClient {
    private static final String TAG = "Custom logs";

    public static Logger logger = Logger.getLogger(TAG);
    private final String BASE_URL = "https://cloud.yandex.ru/api/translate/";


    public String getTranslate(GeneratedWords word) throws InterruptedException {

        Map<String, String> apiVariables = new HashMap<>();
        apiVariables.put("sourceLanguageCode", "en");
        apiVariables.put("targetLanguageCode", "ru");
        apiVariables.put("texts", word.getEn());

        ObjectMapper objectMapper = new ObjectMapper();

        URI uri = new DefaultUriBuilderFactory(BASE_URL).builder().pathSegment("translate")
                .build();

        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)

                .build();
        String requestBody = null;
        try {
            requestBody = objectMapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(apiVariables);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        Gson converter = new Gson();


        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            TranslateClient.logger.log(Level.INFO, "execute: URL is: " + response.uri());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                assert response.body() != null;

                word.setRu(converter.fromJson(response.body(), Translate.class).getTranslations().get(0).getText());
                TranslateClient.logger.log(Level.INFO, "execute: Translate for the word " + word.getEn() + " is: " + word.getRu());
            } else if (response.statusCode() == 429) {
                throw new TooManyRequestsException();
            } else if (response.statusCode() == 405) {
                return getTranslate(word);
            } else {
                TranslateClient.logger.log(Level.SEVERE, "There is an error during request by link " + response.uri() +
                        " . Error code is: " + response.statusCode() +
                        " Error is: " + response.body());
                word.setRu("Translation is not found");
            }
        } catch (IOException e) {
            TranslateClient.logger.log(Level.SEVERE, "Something went wrong. Error is: " + e.getMessage());
            word.setRu("Translation is not found");
            e.printStackTrace();
        } catch (TooManyRequestsException e) {
            Thread.sleep(10000);
            e.printStackTrace();
            return getTranslate(word);
        }

        return word.getRu();
    }

}