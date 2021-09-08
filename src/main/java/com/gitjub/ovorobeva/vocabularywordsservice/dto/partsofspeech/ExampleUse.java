package com.gitjub.ovorobeva.vocabularywordsservice.dto.partsofspeech;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;
@Data
public class ExampleUse {

    private Object text;
    private Object position;
    private Map<Object, Object> additionalProperties = new HashMap<>();

}
