package com.github.ovorobeva.vocabularywordsservice.clients;

import com.github.ovorobeva.vocabularywordsservice.clients.apidocs.WordsApi;
import com.github.ovorobeva.vocabularywordsservice.enums.ExcludedPartsOfSpeech;
import com.github.ovorobeva.vocabularywordsservice.enums.IncludedPartsOfSpeech;
import com.github.ovorobeva.vocabularywordsservice.model.words.RandomWordsDto;
import feign.FeignException;
import feign.RetryableException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@FeignClient(name = "random-words", decode404 = true, url = "https://api.wordnik.com/v4/words.json/")
public interface WordsClient extends WordsApi {

    Map<String, String> getenv = System.getenv();

    String MIN_CORPUS_COUNT = "10000";
    String MAX_CORPUS_COUNT = "-1";
    String MIN_DICTIONARY_COUNT = "1";
    String MAX_DICTIONARY_COUNT = "-1";
    String MIN_LENGTH = "2";
    String MAX_LENGTH = "-1";

    default Set<String> getRandomWords(int wordsCount) throws InterruptedException {

        final String WORDS_API_KEY = getenv.get("WORDS_API_KEY");
        int returnedWords = 0;
        Set<String> words = new HashSet<>();

        List<String> includePartOfSpeechList = new ArrayList<>();
        for (IncludedPartsOfSpeech partsOfSpeech : IncludedPartsOfSpeech.values()) {
            includePartOfSpeechList.add(partsOfSpeech.getValue());
        }

        List<String> excludePartOfSpeechList = new ArrayList<>();
        for (ExcludedPartsOfSpeech partsOfSpeech : ExcludedPartsOfSpeech.values()) {
            excludePartOfSpeechList.add(partsOfSpeech.getValue());
        }

        try {
            ResponseEntity<List<RandomWordsDto>> response = getWords(MIN_CORPUS_COUNT,
                    MAX_CORPUS_COUNT,
                    MIN_DICTIONARY_COUNT,
                    MAX_DICTIONARY_COUNT,
                    MIN_LENGTH,
                    MAX_LENGTH,
                    includePartOfSpeechList,
                    excludePartOfSpeechList,
                    wordsCount,
                    WORDS_API_KEY);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                response.getBody().forEach(word -> words.add(word.getWord()));
                returnedWords = words.size();
            } else {
                //todo:to handle some common exception
                return words;
            }
        } catch (RetryableException e) {
            e.printStackTrace();
            if (e.getCause() instanceof FeignException.TooManyRequests) {
                Thread.sleep(10000);
                if ((wordsCount - returnedWords) > 0)
                    return getRandomWords(wordsCount - returnedWords);
            }
        }
        return words;
    }
}