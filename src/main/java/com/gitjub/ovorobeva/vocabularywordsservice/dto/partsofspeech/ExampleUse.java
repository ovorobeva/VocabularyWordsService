package com.gitjub.ovorobeva.vocabularywordsservice.dto.partsofspeech;

import java.util.HashMap;
import java.util.Map;

public class ExampleUse {

    private String text;
    private Integer position;
    private Map<String, Object> additionalProperties = new HashMap<>();

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
