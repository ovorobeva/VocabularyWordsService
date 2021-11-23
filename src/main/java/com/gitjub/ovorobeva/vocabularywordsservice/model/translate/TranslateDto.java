package com.gitjub.ovorobeva.vocabularywordsservice.model.translate;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class TranslateDto {

    private List<Translation> translations = null;
    private Map<String, Object> additionalProperties = new HashMap<>();

    @Data
    public static class Translation {

        private String text;
        private Map<String, Object> additionalProperties = new HashMap<>();
    }
}