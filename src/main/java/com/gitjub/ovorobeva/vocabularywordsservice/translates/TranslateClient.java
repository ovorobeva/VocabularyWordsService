package com.gitjub.ovorobeva.vocabularywordsservice.translates;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gitjub.ovorobeva.vocabularywordsservice.model.generated.GeneratedWords;
import com.google.gson.Gson;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.logging.Logger;

@Service
public interface TranslateClient {

    String TAG = "Custom logs";
    Logger logger = Logger.getLogger(TAG);
    String BASE_URL = "https://cloud.yandex.ru/api/translate/";

    ObjectMapper objectMapper = new ObjectMapper();

    URI URI = new DefaultUriBuilderFactory(BASE_URL).builder().pathSegment("translate").build();
    HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
            .uri(URI)
            .header("Content-Type", "application/json");
    HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .build();
    Gson converter = new Gson();

    String getTranslate(GeneratedWords word) throws InterruptedException;
}