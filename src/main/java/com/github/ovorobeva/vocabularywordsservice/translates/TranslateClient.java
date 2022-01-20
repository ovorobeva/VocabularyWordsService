package com.github.ovorobeva.vocabularywordsservice.translates;

import com.github.ovorobeva.vocabularywordsservice.exceptions.AuthTranslateException;
import com.github.ovorobeva.vocabularywordsservice.exceptions.GettingTranslateException;
import com.github.ovorobeva.vocabularywordsservice.exceptions.LimitExceededException;
import com.github.ovorobeva.vocabularywordsservice.exceptions.TooManyRequestsException;
import com.github.ovorobeva.vocabularywordsservice.model.generated.GeneratedWordsDto;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.Locale;

@Service
public abstract class TranslateClient {

    protected final Logger logger = LogManager.getLogger();
    protected final HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
            .header("Content-Type", "application/json");
    protected final HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .build();
    protected final Gson converter = new Gson();
    private final String BASE_URL = "https://api-free.deepl.com/v2/";
    private final String API_KEY = System.getenv("TRANSLATION_API_KEY");
    protected final UriBuilder uriBuilder = new DefaultUriBuilderFactory(BASE_URL).builder().pathSegment("translate")
            .queryParam("auth_key", API_KEY)
            .queryParam("source_lang", Language.EN.toString().toLowerCase(Locale.ROOT));

    protected static boolean isSuccess(int statusCode) throws TooManyRequestsException, LimitExceededException, AuthTranslateException, GettingTranslateException {
        if (statusCode >= 200 && statusCode < 300) {
            return true;
        } else if (statusCode == 429 || statusCode == 529) {
            throw new TooManyRequestsException();
        } else if (statusCode == 456) {
            throw new LimitExceededException("Limit is exceeded, check your account https://www.deepl.com/ru/pro-account/usage");
        } else if (statusCode == 403) {
            throw new AuthTranslateException("Api key is compromised and needs to be checked in the account details https://www.deepl.com/ru/pro-account/usage");
        } else
            throw new GettingTranslateException();
    }

    public void translateWord(GeneratedWordsDto word) throws GettingTranslateException,
            LimitExceededException,
            InterruptedException,
            IOException,
            AuthTranslateException {}
}