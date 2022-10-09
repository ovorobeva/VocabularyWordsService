package com.github.ovorobeva.vocabularywordsservice.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CorrectPartsOfSpeech {
    NOUN ("noun"),
    ADJECTIVE ("adjective"),
    VERB ("verb"),
    IDIOM ("idiom"),
    PHRASAL_VERB("phrasal verb"),
    ADVERB_ADJECTIVE("adverb & adjective"),
    TRANSITIVE_INTRANSITIVE_VERB("transitive & intransitive verb"),
    INTRANSITIVE_VERB("transitive verb"),
    TRANSITIVE_VERB("intransitive verb"),
    PAST_PARTICIPLE ("past-participle"),
    ADVERB("adverb");

    private final String value;

}
