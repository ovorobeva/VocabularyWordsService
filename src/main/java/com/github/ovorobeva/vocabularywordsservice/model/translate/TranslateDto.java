package com.github.ovorobeva.vocabularywordsservice.model.translate;

import lombok.Data;

import java.util.List;

@Data
public class TranslateDto {

    private List<Translation> translations = null;
 //   private Map<String, Object> additionalProperties = new HashMap<>();

    @Data
    public static class Translation {

        private String text;
   //     private Map<String, Object> additionalProperties = new HashMap<>();
    }
}