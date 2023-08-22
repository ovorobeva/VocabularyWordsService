package com.github.ovorobeva.vocabularywordsservice.model.partsofspeech;

import lombok.Data;
import lombok.ToString;

import java.util.LinkedList;
import java.util.List;

@Data
@ToString(of = "meanings")
public class PartsOfSpeechDto {

    private String word;
    private List<Meaning> meanings = new LinkedList<>();

    @Data
    public static class Meaning {
        private String partOfSpeech;

        @Override
        public String toString() {
            return partOfSpeech + ", ";
        }
    }
}