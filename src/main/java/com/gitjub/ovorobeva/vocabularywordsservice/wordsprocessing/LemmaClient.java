package com.gitjub.ovorobeva.vocabularywordsservice.wordsprocessing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gitjub.ovorobeva.vocabularywordsservice.exceptions.TooManyRequestsException;
import com.gitjub.ovorobeva.vocabularywordsservice.model.lemmas.LemmaDto;
import com.google.gson.Gson;
import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

@Service
@Data
public class LemmaClient {
    public static final String SELDOM_WORD = "Word is not in use";

    @SneakyThrows
    public String getLemma(String word) {

        final String BASE_URL = "https://try.expert.ai/analysis/standard/en/disambiguation";

        WordsClient.logger.log(Level.INFO, "setLemma: Searching for the lemma for the word " + word);

        URI uri = new DefaultUriBuilderFactory(BASE_URL).builder()
                .build();

        HttpClient client = HttpClient.newBuilder()
                .build();

        Map<String, String> variables = new HashMap<>();
        variables.put("text", word);

        String requestBody = new ObjectMapper()
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(variables);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        Gson converter = new Gson();

        try {
            CompletableFuture<HttpResponse<String>> response =
                    client.sendAsync(request, HttpResponse.BodyHandlers.ofString());

            if (response.get().statusCode() >= 200 && response.get().statusCode() < 300) {
                LemmaDto.Token message = converter.fromJson(response.get().body(), LemmaDto.class).getData().getTokens().get(0);
                if (message.getSyncon() == -1)
                    return SELDOM_WORD;

                WordsClient.logger.log(Level.INFO, "execute. URL is: " + response.get().uri() + "\n lemma response is " + message.getLemma());
                return message.getLemma();
            } else if (response.get().statusCode() == 429) {
                throw new TooManyRequestsException();
            } else {
                WordsClient.logger.log(Level.SEVERE, "There is an error during request by link " + response.get().uri()
                        + " . Error code is: " + response.get().statusCode()
                        + ". Error is: " + response.get().body());
                return word;
            }
        } catch (IllegalStateException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
            WordsClient.logger.log(Level.SEVERE, "There is an error during request by link " + request.uri() + e.getMessage());
            return word;
        } catch (TooManyRequestsException e) {
            try {
                Thread.sleep(15000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return getLemma(word);
        }
    }

}
