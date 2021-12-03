package com.gitjub.ovorobeva.vocabularywordsservice.model.lemmas;

import lombok.Data;

import java.util.List;


@Data
public class LemmaDto {

    private Data data;

    @lombok.Data
    public static class Data {
        private List<Token> tokens = null;

    }

    @lombok.Data
    public static class Token {
        private int syncon;
        private String lemma;
    }

}


