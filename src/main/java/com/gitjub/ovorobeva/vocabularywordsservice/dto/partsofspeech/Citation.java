package com.gitjub.ovorobeva.vocabularywordsservice.dto.partsofspeech;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;
@Data
public class Citation {

    private Object source;
    private Object cite;
    private Map<Object, Object> additionalProperties = new HashMap<>();

}
