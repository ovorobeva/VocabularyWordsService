package com.github.ovorobeva.vocabularywordsservice.model.partsofspeech;

import lombok.Data;
import lombok.ToString;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Data
@ToString(of = "meanings")
public class
PartsOfSpeechDto {

        private String word;
        private String phonetic;
        private List<Object> phonetics = null;
        private String origin;
        private List<Meaning> meanings = new LinkedList<>();
        private Map<String, Object> additionalProperties = new HashMap<>();

    @Data
    public static class Meaning {

        private String partOfSpeech;
        private List<Object> definitions = null;
        private Map<Object, Object> additionalProperties = new HashMap<>();

        @Override
        public String toString() {
            return partOfSpeech + ", ";
        }
    }
}