package com.gitjub.ovorobeva.vocabularywordsservice.dto.words;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class WordsMessage {

    private Integer id;
    private String word;
    private Map<String, Object> additionalProperties = new HashMap<>();

    @Override
    public String toString() {
        return word + ", ";
    }
}

