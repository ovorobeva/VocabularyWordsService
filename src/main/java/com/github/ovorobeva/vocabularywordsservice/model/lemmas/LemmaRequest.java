package com.github.ovorobeva.vocabularywordsservice.model.lemmas;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class LemmaRequest {
    private final String text;
}
