package com.gitjub.ovorobeva.vocabularywordsservice.wordsprocessing;

import com.gitjub.ovorobeva.vocabularywordsservice.dto.partsofspeech.PartsOfSpeech;
import com.gitjub.ovorobeva.vocabularywordsservice.dto.words.WordsMessage;
import com.gitjub.ovorobeva.vocabularywordsservice.exceptions.TooManyRequestsException;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WordsClient {

    private static final Map<String, String> getenv = System.getenv();
    private static final Object OBJECT = new Object();
    private static final String TAG = "Custom logs";
    public static Logger logger = Logger.getLogger(TAG);
    private static WordsClient wordsClient;
    private final String BASE_URL = "https://api.wordnik.com/v4/";
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

        Map<String, String> apiVariables = new HashMap<>();
        apiVariables.put("minCorpusCount", "10000");
        apiVariables.put("maxCorpusCount", "-1");
        apiVariables.put("minDictionaryCount", "1");
        apiVariables.put("maxDictionaryCount", "-1");
        apiVariables.put("minLength", "2");
        apiVariables.put("maxLength", "-1");

        List<String> includePartOfSpeechList = new ArrayList<>();
        includePartOfSpeechList.add("noun,");
        includePartOfSpeechList.add("adjective,");
        includePartOfSpeechList.add("verb,");
        includePartOfSpeechList.add("idiom,");
        includePartOfSpeechList.add("past-participle");

        StringBuilder includePartOfSpeech = new StringBuilder();

        for (String partOfSpeech : includePartOfSpeechList) {
            includePartOfSpeech.append(partOfSpeech);
        }

        List<String> excludePartOfSpeechList = new ArrayList<>();
        excludePartOfSpeechList.add("interjection,");
        excludePartOfSpeechList.add("pronoun,");
        excludePartOfSpeechList.add("preposition,");
        excludePartOfSpeechList.add("abbreviation,");
        excludePartOfSpeechList.add("affix,");
        excludePartOfSpeechList.add("article,");
        excludePartOfSpeechList.add("auxiliary-verb,");
        excludePartOfSpeechList.add("conjunction,");
        excludePartOfSpeechList.add("definite-article,");
        excludePartOfSpeechList.add("family-name,");
        excludePartOfSpeechList.add("given-name,");
        excludePartOfSpeechList.add("imperative,");
        excludePartOfSpeechList.add("proper-noun,");
        excludePartOfSpeechList.add("proper-noun-plural,");
        excludePartOfSpeechList.add("suffix,");
        excludePartOfSpeechList.add("verb-intransitive,");
        excludePartOfSpeechList.add("verb-transitive");


        StringBuilder excludePartOfSpeech = new StringBuilder();

        for (String partOfSpeech : excludePartOfSpeechList) {
            excludePartOfSpeech.append(partOfSpeech);
        }

        Call<List<WordsMessage>> randomWordsRequest = wordsApi.sendRequest(true, includePartOfSpeech.toString(), excludePartOfSpeech.toString(), apiVariables.get("minCorpusCount"),
                apiVariables.get("maxCorpusCount"), apiVariables.get("minDictionaryCount"), apiVariables.get("maxDictionaryCount"),
                apiVariables.get("minLength"), apiVariables.get("maxLength"), String.valueOf(wordsCount), WORDS_API_KEY);


        try {
            Response<List<WordsMessage>> response = randomWordsRequest.execute();
            if (response.isSuccessful()) {
                List<WordsMessage> responseBody = response.body();
                assert responseBody != null;
                for (WordsMessage responseItem : responseBody) {
                    returnedWords++;
                    words.add(responseItem.getWord());
                    WordsClient.logger.log(Level.INFO, "execute. URL is: " + response.raw().request().url());
                    WordsClient.logger.log(Level.INFO, "execute. Response to process is: " + responseBody);
                }
            } else if (response.code() == 429) {
                throw new TooManyRequestsException();
            } else
                words.add("There is an error during request by link " + response.raw().request().url() + " . Error code is: " + response.code());
        } catch (IOException e) {
            words.add("Something went wrong. Error is: " + e.getMessage());
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
        Map<String, Boolean> apiVariables = new HashMap<>();
        apiVariables.put("includeRelated", false);
        apiVariables.put("useCanonical", false);
        apiVariables.put("includeTags", false);


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
        }
        return partsOfSpeech;
    }

}