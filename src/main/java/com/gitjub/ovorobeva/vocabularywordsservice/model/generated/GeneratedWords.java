package com.gitjub.ovorobeva.vocabularywordsservice.model.generated;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Getter
@Setter
@RequiredArgsConstructor
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
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        GeneratedWords that = (GeneratedWords) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return 0;
    }
}

