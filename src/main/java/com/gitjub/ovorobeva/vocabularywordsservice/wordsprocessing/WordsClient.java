package com.gitjub.ovorobeva.vocabularywordsservice.wordsprocessing;

import com.gitjub.ovorobeva.vocabularywordsservice.dto.partsofspeech.PartsOfSpeech;
import com.gitjub.ovorobeva.vocabularywordsservice.dto.words.WordsMessage;
import com.gitjub.ovorobeva.vocabularywordsservice.exceptions.TooManyRequestsException;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.DefaultUriBuilderFactory;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WordsClient {

    private static final Map<String, String> getenv = System.getenv();
    private static final Object OBJECT = new Object();
    private static final String TAG = "Custom logs";
    public static Logger logger = Logger.getLogger(TAG);
    private static WordsClient wordsClient;
    private final String BASE_URL = "https://api.wordnik.com/v4/words.json/";
    private final String WORDS_API_KEY = getenv.get("WORDS_API_KEY");
    private final WordsApi wordsApi;

    private WordsClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        wordsApi = retrofit.create(WordsApi.class);
    }

    public static WordsClient getWordsClient() {
        if (wordsClient != null)
            return wordsClient;

        synchronized (OBJECT) {
            if (wordsClient == null)
                wordsClient = new WordsClient();
            return wordsClient;
        }
    }

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

        URI uri = new DefaultUriBuilderFactory(BASE_URL).builder().pathSegment("randomWords")
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
                JSONArray jsonArray = new JSONArray(response.body());
                for (Object object : jsonArray) {
                    JSONObject jsonObject = (JSONObject) object;
                    returnedWords++;
                    words.add(converter.fromJson(String.valueOf(jsonObject), WordsMessage.class).getWord());
                    WordsClient.logger.log(Level.INFO, "execute. URL is: " + response.uri());
                    WordsClient.logger.log(Level.INFO, "execute. Response to process is: " + words);
                }
            } else if (response.statusCode() == 429) {
                throw new TooManyRequestsException();
            } else
                words.add("There is an error during request by link " + request.uri() + " . Error code is: " + response.statusCode());

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
        List<String> partsOfSpeech = new LinkedList<>();

        WordsClient.logger.log(Level.INFO, "getPartsOfSpeech: Start getting parts of speech for the word " + word);
        final String LIMIT = "500";
        MultiValueMap<String, String> apiVariables = new LinkedMultiValueMap<>();
        apiVariables.put("includeRelated", Collections.singletonList("false"));
        apiVariables.put("useCanonical", Collections.singletonList("false"));
        apiVariables.put("includeTags", Collections.singletonList("false"));
        apiVariables.put("api_key", Collections.singletonList(WORDS_API_KEY));
        apiVariables.put("limit", Collections.singletonList(LIMIT));

        URI uri = new DefaultUriBuilderFactory(BASE_URL).builder().pathSegment(word).pathSegment("definitions")
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
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                JSONArray jsonArray = new JSONArray(response.body());
                for (Object object : jsonArray) {
                    JSONObject jsonObject = (JSONObject) object;
                    partsOfSpeech.add(converter.fromJson(String.valueOf(jsonObject), PartsOfSpeech.class).getPartOfSpeech());
                }
                WordsClient.logger.log(Level.INFO, "execute. URL is: " + response.uri());
                WordsClient.logger.log(Level.INFO, "execute. Response to process is: " + partsOfSpeech);
            } else if (response.statusCode() == 429) {
                throw new TooManyRequestsException();
            } else if (response.statusCode() == 404) {
                partsOfSpeech = null;
            } else
                WordsClient.logger.log(Level.SEVERE, "There is an error during request by link " + response.uri() + " . Error code is: " + response.statusCode());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TooManyRequestsException e) {
            Thread.sleep(10000);
            e.printStackTrace();
            return getPartsOfSpeech(word);
        }





        /*
        Call<List<PartsOfSpeech>> partsOfSpeechRequest = wordsApi.sendRequest(word, apiVariables.get("includeRelated"),
                apiVariables.get("useCanonical"), apiVariables.get("includeTags"), LIMIT, WORDS_API_KEY);

        try {
            Response<List<PartsOfSpeech>> response = partsOfSpeechRequest.execute();
            if (response.isSuccessful()) {
                List<PartsOfSpeech> responseBody = response.body();
                if (responseBody == null) return null;
                for (PartsOfSpeech responseItem : responseBody) {
                    if (responseItem.getPartOfSpeech() != null && !responseItem.getPartOfSpeech().isEmpty()) {
                        partsOfSpeech.add(responseItem.getPartOfSpeech().toLowerCase());
                        WordsClient.logger.log(Level.INFO, "execute. URL is: " + response.raw().request().url());
                        WordsClient.logger.log(Level.INFO, "execute. Response to process is: " + responseBody);
                    }
                }
            } else if (response.code() == 429) {
                throw new TooManyRequestsException();
            } else if (response.code() == 404) {
                partsOfSpeech = null;
            } else
                WordsClient.logger.log(Level.SEVERE, "There is an error during request by link " + response.raw().request().url() + " . Error code is: " + response.code());
        } catch (IOException e) {
            WordsClient.logger.log(Level.SEVERE, "Something went wrong during request by link " + partsOfSpeechRequest.request().url() + "\nError is: " + e.getMessage());
            e.printStackTrace();
        } catch (TooManyRequestsException e) {
            Thread.sleep(10000);
            e.printStackTrace();
            return getPartsOfSpeech(word);
        }*/
        return partsOfSpeech;
    }

}