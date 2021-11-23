package com.gitjub.ovorobeva.vocabularywordsservice.model.generated;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.test.annotation.DirtiesContext;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Table(name = "Generated_words", uniqueConstraints = @UniqueConstraint(columnNames={"English", "Code"}))
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class GeneratedWordsDto {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column (name = "Code")
    @NotNull(message = "Code cannot be null")
    private int code;

    @Column (name = "English")
    @NotNull(message = "English translation cannot be null")
    private String en;

    @Column (name = "Russian")
    private String ru;

    @Column (name = "French")
    private String fr;

    public GeneratedWordsDto(String en, int code) {
        this.en = en.toLowerCase();
        this.code = code;
    }

    @Override
    public String toString() {
        return "GeneratedWords{" +
                "id=" + id +
                ", en='" + en + '\'' +
                ", ru='" + ru + '\'' +
                ", fr='" + fr + '\'' +
                ", code=" + code +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GeneratedWordsDto)) return false;
        GeneratedWordsDto that = (GeneratedWordsDto) o;
        return getEn().equals(that.getEn()) && getRu().equals(that.getRu());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEn(), getRu());
    }
}

