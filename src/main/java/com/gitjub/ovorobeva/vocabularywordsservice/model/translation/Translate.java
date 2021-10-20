package com.gitjub.ovorobeva.vocabularywordsservice.model.translation;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class Translate {

    private List<Translation> translations = null;
    private Map<String, Object> additionalProperties = new HashMap<>();

}