package com.gitjub.ovorobeva.vocabularywordsservice.dto.partsofspeech;

import java.util.HashMap;
import java.util.Map;

public class Citation {

    private String source;
    private String cite;
    private Map<String, Object> additionalProperties = new HashMap<>();

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getCite() {
        return cite;
    }

    public void setCite(String cite) {
        this.cite = cite;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
