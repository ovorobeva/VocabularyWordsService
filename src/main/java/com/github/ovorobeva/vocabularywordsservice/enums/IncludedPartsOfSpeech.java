package com.github.ovorobeva.vocabularywordsservice.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum IncludedPartsOfSpeech {
    NOUN ("noun"),
    ADJECTIVE ("adjective"),
    VERB ("verb"),
    IDIOM ("idiom"),
    PAST_PARTICIPLE ("past-participle");

    private final String value;

}
