package com.gitjub.ovorobeva.vocabularywordsservice.dto.partsofspeech;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;
@Data
public class Label {

    private Object text;
    private Object type;
    private Map<Object, Object> additionalProperties = new HashMap<>();

}



