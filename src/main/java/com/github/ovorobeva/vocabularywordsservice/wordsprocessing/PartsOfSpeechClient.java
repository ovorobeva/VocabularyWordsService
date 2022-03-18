package com.github.ovorobeva.vocabularywordsservice.wordsprocessing;

import com.github.ovorobeva.vocabularywordsservice.exceptions.TooManyRequestsException;
import com.github.ovorobeva.vocabularywordsservice.model.partsofspeech.PartsOfSpeechDto;
import com.google.gson.Gson;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@Data
@Slf4j
public class PartsOfSpeechClient {

    public List<String> getPartsOfSpeech(String word) throws InterruptedException {

        final String BASE_URL = "https://api.dictionaryapi.dev/api/v2/entries/en/";

        log.info("getPartsOfSpeech: Start getting parts of speech for the word " + word);
        List<String> partsOfSpeech = new LinkedList<>();


        URI uri = new DefaultUriBuilderFactory(BASE_URL).builder()
                .pathSegment(word)
                .build();

        HttpClient client = HttpClient.newBuilder()
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        Gson converter = new Gson();

        try {
            CompletableFuture<HttpResponse<String>> response =
                    client.sendAsync(request, HttpResponse.BodyHandlers.ofString());

            if (response.get().statusCode() >= 200 && response.get().statusCode() < 300) {
                log.info("execute. URL is: " + response.get().uri());
                JSONArray jsonMessages = new JSONArray(response.get().body());
                List<PartsOfSpeechDto.Meaning> meanings = converter.fromJson(jsonMessages.get(0).toString(), PartsOfSpeechDto.class).getMeanings();
                if (meanings.isEmpty()) return null;
                for (PartsOfSpeechDto.Meaning message : meanings) {
                    String partOfSpeech = message.getPartOfSpeech();
                    if (partOfSpeech != null && !partOfSpeech.isEmpty()) {
                        partsOfSpeech.add(partOfSpeech.toLowerCase());
                    }
                }
                log.debug("execute. Response to process is: " + partsOfSpeech);
            } else if (response.get().statusCode() == 429 || response.get().statusCode() == 503 || response.get().statusCode() == 405) {
                throw new TooManyRequestsException();
            } else if (response.get().statusCode() == 404) {
                return null;
            } else
                log.error("There is an error during request by link " + response.get().uri() + " . Error code is: " + response.get().statusCode());
        } catch (IllegalStateException | ExecutionException e) {
            e.printStackTrace();
            log.error("There is an error during request by link " + request.uri() + e.getMessage());
            return null;
        } catch (TooManyRequestsException e) {
            Thread.sleep(15000);
            e.printStackTrace();
            return getPartsOfSpeech(word);
        }

        return partsOfSpeech;
    }

}
