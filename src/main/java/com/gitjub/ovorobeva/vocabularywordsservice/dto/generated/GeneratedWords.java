package com.gitjub.ovorobeva.vocabularywordsservice.dto.generated;

import lombok.Data;

@Data
public class GeneratedWords {

    private Integer id;
    private String en;
    private String ru;

    public GeneratedWords() {
    }

    public GeneratedWords(Integer id, String en) {
        this.id = id;
        this.en = en.toLowerCase();
    }

    @Override
    public String toString() {
        return "GeneratedWords{" +
                "id=" + id +
                ", en='" + en + '\'' +
                ", ru='" + ru + '\'' +
                '}';
    }
}

