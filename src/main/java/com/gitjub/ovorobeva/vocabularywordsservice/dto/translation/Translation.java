package com.gitjub.ovorobeva.vocabularywordsservice.dto.translation;

import java.util.HashMap;
import java.util.Map;

public class Translation {

    private String text;
    private Map<String, Object> additionalProperties = new HashMap<>();

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}

