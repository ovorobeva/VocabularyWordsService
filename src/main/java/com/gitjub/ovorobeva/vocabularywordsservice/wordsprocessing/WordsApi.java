package com.gitjub.ovorobeva.vocabularywordsservice.wordsprocessing;

import com.gitjub.ovorobeva.vocabularywordsservice.dto.partsofspeech.PartsOfSpeech;
import com.gitjub.ovorobeva.vocabularywordsservice.dto.words.WordsMessage;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;

public interface WordsApi {

    @GET("word.json/{word}/definitions")
    Call<List<PartsOfSpeech>> sendRequest(@Path("word") String word,
                                          @Query("includeRelated") boolean includeRelated,
                                          @Query("useCanonical") boolean useCanonical,
                                          @Query("includeTags") boolean includeTags,
                                          @Query("limit") String limit,
                                          @Query("api_key") String apiKey);


    @GET("words.json/randomWords")
    Call<List<WordsMessage>> sendRequest(@Query("isHasDictionaryDef") boolean isHasDictionaryDef,
                                         @Query("includePartOfSpeech") String includePartOfSpeech,
                                         @Query("excludePartOfSpeech") String excludePartOfSpeech,
                                         //todo: to make constants for beginner, intermediate, advanced
                                         @Query("minCorpusCount") String minCorpusCount,
                                         @Query("maxCorpusCount") String maxCorpusCount,
                                         @Query("minDictionaryCount") String minDictionaryCount,
                                         @Query("maxDictionaryCount") String maxDictionaryCount,
                                         @Query("minLength") String minLength,
                                         @Query("maxLength") String maxLength,
                                         @Query("limit") String limit,
                                         @Query("api_key") String apiKey);
}
