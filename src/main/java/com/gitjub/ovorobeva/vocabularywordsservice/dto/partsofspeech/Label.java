package com.gitjub.ovorobeva.vocabularywordsservice.dto.partsofspeech;

import java.util.HashMap;
import java.util.Map;

public class Label {

    private String text;
    private String type;
    private Map<String, Object> additionalProperties = new HashMap<>();

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}



