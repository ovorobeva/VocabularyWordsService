package com.gitjub.ovorobeva.vocabularywordsservice.dto.words;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class WordsMessage {

    private Integer id;
    private String word;
    private Map<String, Object> additionalProperties = new HashMap<>();

/*    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }*/

    @Override
    public String toString() {
        return word + ", ";
    }
}

