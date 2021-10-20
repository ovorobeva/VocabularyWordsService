package com.gitjub.ovorobeva.vocabularywordsservice.model.partsofspeech;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class Meaning {

        private String partOfSpeech;
        private List<Object> definitions = null;
        private Map<Object, Object> additionalProperties = new HashMap<>();

    @Override
    public String toString() {
        return partOfSpeech + ", ";
    }
}
