package com.gitjub.ovorobeva.vocabularywordsservice.model.generated;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Table(name = "Generated_words", uniqueConstraints = @UniqueConstraint(columnNames={"English"}))
public class GeneratedWords {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;

    @Column (name = "English")
    @NotNull(message = "English translation cannot be null")
    private String en;

    @Column (name = "Russian")
    private String ru;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GeneratedWords)) return false;
        GeneratedWords that = (GeneratedWords) o;
        return getEn().equals(that.getEn()) && getRu().equals(that.getRu());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEn(), getRu());
    }
}

