package com.gitjub.ovorobeva.vocabularywordsservice.translates;

import com.gitjub.ovorobeva.vocabularywordsservice.dto.generated.GeneratedWords;
import com.gitjub.ovorobeva.vocabularywordsservice.dto.translation.Translate;
import com.gitjub.ovorobeva.vocabularywordsservice.exceptions.TooManyRequestsException;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class TranslateClient {
    private static final String TAG = "Custom logs";

    public static Logger logger = Logger.getLogger(TAG);
    private final String BASE_URL = "https://cloud.yandex.ru/api/translate/";

    private final TranslateApi translateApi;

    private TranslateClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        translateApi = retrofit.create(TranslateApi.class);
    }

    public String getTranslate(GeneratedWords word) throws InterruptedException {

        Map<String, String> apiVariables = new HashMap<>();
        apiVariables.put("sourceLanguageCode", "en");
        apiVariables.put("targetLanguageCode", "ru");


        Call<Translate> translateRequest = translateApi.sendRequest(apiVariables.get("sourceLanguageCode"),
                apiVariables.get("targetLanguageCode"), word.getEn());


        try {
            Response<Translate> response = translateRequest.execute();
            TranslateClient.logger.log(Level.INFO, "execute: URL is: " + translateRequest.request().url());
            if (response.isSuccessful()) {
                assert response.body() != null;
                word.setRu(response.body().getTranslations().get(0).getText());
                TranslateClient.logger.log(Level.INFO, "execute: Translate for the word " + word.getEn() + " is: " + word.getRu());
            } else if (response.code() == 429) {
                throw new TooManyRequestsException();
            } else {
                TranslateClient.logger.log(Level.SEVERE, "There is an error during request by link " + response.raw().request().url() + " . Error code is: " + response.code());
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