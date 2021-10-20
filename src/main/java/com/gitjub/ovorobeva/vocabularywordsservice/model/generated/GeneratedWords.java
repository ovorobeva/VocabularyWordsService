package com.gitjub.ovorobeva.vocabularywordsservice.model.generated;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Table(name = "Generated_words")
public class GeneratedWords {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column (name = "English")
    @NotNull(message = "English translation cannot be null")
    private String en;

    @Column (name = "Russian")
    private String ru;

    public GeneratedWords() {
    }

    public GeneratedWords(String en) {
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

