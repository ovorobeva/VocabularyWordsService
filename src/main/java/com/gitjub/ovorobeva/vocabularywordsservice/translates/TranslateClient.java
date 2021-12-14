package com.gitjub.ovorobeva.vocabularywordsservice.translates;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gitjub.ovorobeva.vocabularywordsservice.exceptions.AuthTranslateException;
import com.gitjub.ovorobeva.vocabularywordsservice.exceptions.GettingTranslateException;
import com.gitjub.ovorobeva.vocabularywordsservice.exceptions.LimitExceededException;
import com.gitjub.ovorobeva.vocabularywordsservice.model.generated.GeneratedWordsDto;
import com.google.gson.Gson;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.Locale;
import java.util.logging.Logger;

@Service
public interface TranslateClient {

    String TAG = "Custom logs";
    Logger logger = Logger.getLogger(TAG);
    String BASE_URL = "https://api-free.deepl.com/v2/";
    String API_KEY = System.getenv("TRANSLATION_API_KEY");

    ObjectMapper objectMapper = new ObjectMapper();

    UriBuilder uriBuilder = new DefaultUriBuilderFactory(BASE_URL).builder().pathSegment("translate")
            .queryParam("auth_key", API_KEY)
            .queryParam("source_lang", Language.EN.toString().toLowerCase(Locale.ROOT));
    HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
            .header("Content-Type", "application/json");
    HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .build();
    Gson converter = new Gson();

    void translateWord(GeneratedWordsDto word)  throws GettingTranslateException,
            LimitExceededException,
            InterruptedException,
            IOException,
            AuthTranslateException;
}