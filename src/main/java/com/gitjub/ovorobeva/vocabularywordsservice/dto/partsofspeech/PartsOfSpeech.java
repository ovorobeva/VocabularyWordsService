package com.gitjub.ovorobeva.vocabularywordsservice.dto.partsofspeech;

import lombok.Data;
import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class PartsOfSpeech {

    private String id;
    @Nullable
    private String partOfSpeech;
    private String attributionText;
    private String sourceDictionary;
    private String text;
    private String sequence;
    private Integer score;
    private List<Label> labels = null;
    private List<Citation> citations = null;
    private String word;
    private List<Object> relatedWords = null;
    private List<ExampleUse> exampleUses = null;
    private List<Object> textProns = null;
    private List<Object> notes = null;
    private String attributionUrl;
    private String wordnikUrl;
    private Map<String, Object> additionalProperties = new HashMap<>();

    @Override
    public String toString() {
        return partOfSpeech + ", ";
    }
}