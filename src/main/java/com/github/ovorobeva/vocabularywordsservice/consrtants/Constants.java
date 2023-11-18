package com.github.ovorobeva.vocabularywordsservice.consrtants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants {

    public static String MIN_CORPUS_COUNT = "10000";
    public static String MAX_CORPUS_COUNT = "-1";
    public static String MIN_DICTIONARY_COUNT = "1";
    public static String MAX_DICTIONARY_COUNT = "-1";
    public static String MIN_LENGTH = "2";
    public static String MAX_LENGTH = "-1";
}
