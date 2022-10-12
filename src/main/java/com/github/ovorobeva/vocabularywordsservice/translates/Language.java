package com.github.ovorobeva.vocabularywordsservice.translates;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Language {
    EN("en"),
    RU("ru"),
    CZ("cs"),
    FR("fr");

    private final String value;

    }
