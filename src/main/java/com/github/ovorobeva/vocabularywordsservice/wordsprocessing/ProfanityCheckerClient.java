package com.github.ovorobeva.vocabularywordsservice.wordsprocessing;

import com.github.ovorobeva.vocabularywordsservice.exceptions.TooManyRequestsException;
import lombok.Data;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

@Service
@Data
public class ProfanityCheckerClient {
    public boolean isProfanity(String word) {

        final String BASE_URL = "https://www.purgomalum.com/service/containsprofanity";

        WordsClient.logger.log(Level.INFO, "isProfanity: Start checking the word " + word);

        URI uri = new DefaultUriBuilderFactory(BASE_URL).builder()
                .queryParam("text", word)
                .build();

        HttpClient client = HttpClient.newBuilder()
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        try {
            CompletableFuture<HttpResponse<String>> response =
                    client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            WordsClient.logger.log(Level.INFO, "execute. URL is: " + response.get().uri() + "\n is profanity = " + response.get().body());

            if (response.get().statusCode() >= 200 && response.get().statusCode() < 300) {
                return response.get().body().equals("true");
            } else if (response.get().statusCode() == 429) {
                throw new TooManyRequestsException();
            } else if (response.get().statusCode() == 404) {
                return false;
            } else if (response.get().statusCode() == 405) {
                return isProfanity(word);
            } else {
                WordsClient.logger.log(Level.SEVERE, "There is an error during request by link " + response.get().uri() + " . Error code is: " + response.get().statusCode());
                return false;
            }
        } catch (IllegalStateException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
            WordsClient.logger.log(Level.SEVERE, "There is an error during request by link " + request.uri() + e.getMessage());
            return false;
        } catch (TooManyRequestsException e) {
            try {
                Thread.sleep(15000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return isProfanity(word);
        }
    }

}
