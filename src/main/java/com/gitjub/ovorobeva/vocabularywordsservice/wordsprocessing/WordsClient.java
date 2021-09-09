package com.gitjub.ovorobeva.vocabularywordsservice.wordsprocessing;

import com.gitjub.ovorobeva.vocabularywordsservice.dto.partsofspeech.PartsOfSpeech;
import com.gitjub.ovorobeva.vocabularywordsservice.dto.words.WordsMessage;
import com.gitjub.ovorobeva.vocabularywordsservice.exceptions.TooManyRequestsException;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class WordsClient {

    private static final Map<String, String> getenv = System.getenv();
    private static final String TAG = "Custom logs";
    public static Logger logger = Logger.getLogger(TAG);
    private final String BASE_URL = "https://api.wordnik.com/v4/";
    private final String WORDS_API_KEY = getenv.get("WORDS_API_KEY");

    public WordsClient() {    }

    public List<String> getRandomWords(int wordsCount) throws InterruptedException {

        int returnedWords = 0;
        List<String> words = new LinkedList<>();


        List<String> includePartOfSpeechList = new ArrayList<>();
        includePartOfSpeechList.add("noun");
        includePartOfSpeechList.add("adjective");
        includePartOfSpeechList.add("verb");
        includePartOfSpeechList.add("idiom");
        includePartOfSpeechList.add("past-participle");


        List<String> excludePartOfSpeechList = new ArrayList<>();
        excludePartOfSpeechList.add("interjection");
        excludePartOfSpeechList.add("pronoun");
        excludePartOfSpeechList.add("preposition");
        excludePartOfSpeechList.add("abbreviation");
        excludePartOfSpeechList.add("affix");
        excludePartOfSpeechList.add("article");
        excludePartOfSpeechList.add("auxiliary-verb");
        excludePartOfSpeechList.add("conjunction");
        excludePartOfSpeechList.add("definite-article");
        excludePartOfSpeechList.add("family-name");
        excludePartOfSpeechList.add("given-name");
        excludePartOfSpeechList.add("imperative");
        excludePartOfSpeechList.add("proper-noun");
        excludePartOfSpeechList.add("proper-noun-plural");
        excludePartOfSpeechList.add("suffix");
        excludePartOfSpeechList.add("verb-intransitive,");
        excludePartOfSpeechList.add("verb-transitive");


        MultiValueMap<String, String> apiVariables = new LinkedMultiValueMap<>();
        apiVariables.put("minCorpusCount", Collections.singletonList("10000"));
        apiVariables.put("maxCorpusCount", Collections.singletonList("-1"));
        apiVariables.put("minDictionaryCount", Collections.singletonList("1"));
        apiVariables.put("maxDictionaryCount", Collections.singletonList("-1"));
        apiVariables.put("minLength", Collections.singletonList("2"));
        apiVariables.put("maxLength", Collections.singletonList("-1"));
        apiVariables.put("includePartOfSpeech[]", includePartOfSpeechList);
        apiVariables.put("excludePartOfSpeech[]", excludePartOfSpeechList);

        URI uri = new DefaultUriBuilderFactory(BASE_URL).builder().pathSegment("words.json")
                .pathSegment("randomWords")
                .queryParams(apiVariables)
                .queryParam("limit", wordsCount)
                .queryParam("api_key", WORDS_API_KEY)
                .build();

        HttpClient client = HttpClient.newBuilder()
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        Gson converter = new Gson();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                JSONArray jsonMessages = new JSONArray(response.body());
                for (Object message : jsonMessages) {
                    returnedWords++;
                    words.add(converter.fromJson(message.toString(), WordsMessage.class).getWord());
                }
                WordsClient.logger.log(Level.INFO, "execute. URL is: " + response.uri());
                WordsClient.logger.log(Level.INFO, "execute. Response to process is: " + words);
            } else if (response.statusCode() == 429) {
                throw new TooManyRequestsException();
            } else
                WordsClient.logger.log(Level.INFO, "There is an error during request by link " + request.uri() + " . Error code is: " + response.statusCode());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TooManyRequestsException e) {
            Thread.sleep(10000);
            e.printStackTrace();
            if ((wordsCount - returnedWords) > 0)
                return getRandomWords(wordsCount - returnedWords);
        }
        return words;
    }

    public List<String> getPartsOfSpeech(String word) throws InterruptedException {
        WordsClient.logger.log(Level.INFO, "getPartsOfSpeech: Start getting parts of speech for the word " + word);
        List<String> partsOfSpeech = new LinkedList<>();


        final String LIMIT = "500";
        MultiValueMap<String, String> apiVariables = new LinkedMultiValueMap<>();
        apiVariables.put("includeRelated", Collections.singletonList("false"));
        apiVariables.put("useCanonical", Collections.singletonList("false"));
        apiVariables.put("includeTags", Collections.singletonList("false"));
        apiVariables.put("limit", Collections.singletonList(LIMIT));
        apiVariables.put("api_key", Collections.singletonList(WORDS_API_KEY));

        URI uri = new DefaultUriBuilderFactory(BASE_URL).builder().pathSegment("word.json")
                .pathSegment(word)
                .pathSegment("definitions")
                .queryParams(apiVariables)
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
                WordsClient.logger.log(Level.INFO, "execute. URL is: " + response.get().uri());
                JSONArray jsonMessages = new JSONArray(response.get().body());
                if (jsonMessages.isEmpty()) return null;
                for (Object message : jsonMessages) {
                    String partOfSpeech = converter.fromJson(message.toString(), PartsOfSpeech.class).getPartOfSpeech();
                        if (partOfSpeech != null && !partOfSpeech.isEmpty()) {
                        partsOfSpeech.add(partOfSpeech.toLowerCase());
                    }
                }
                WordsClient.logger.log(Level.INFO, "execute. Response to process is: " + partsOfSpeech);
            } else if (response.get().statusCode() == 429) {
                throw new TooManyRequestsException();
            } else if (response.get().statusCode() == 404) {
                partsOfSpeech = null;
            } else
                WordsClient.logger.log(Level.SEVERE, "There is an error during request by link " + response.get().uri() + " . Error code is: " + response.get().statusCode());
        } catch (IllegalStateException e){
            partsOfSpeech = null;
        }catch (TooManyRequestsException e) {
            Thread.sleep(15000);
            e.printStackTrace();
            return getPartsOfSpeech(word);
        } catch (ExecutionException e) {
            e.printStackTrace();
            partsOfSpeech = null;
            WordsClient.logger.log(Level.SEVERE, "There is an error during request by link " + request.uri() + e.getMessage());
        }

        return partsOfSpeech;
    }

}