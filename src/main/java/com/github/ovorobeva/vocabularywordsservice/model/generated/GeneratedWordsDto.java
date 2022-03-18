package com.github.ovorobeva.vocabularywordsservice.model.generated;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.UniqueElements;
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
    @UniqueElements
    @NotNull(message = "Code cannot be null")
    private int code;

    @Column (name = "English")
    @NotNull(message = "English translation cannot be null")
    private String en;

    @Column (name = "Russian")
    @NotNull(message = "Russian translation cannot be null")
    private String ru;

    @Column (name = "French")
    @NotNull(message = "French translation cannot be null")
    private String fr;

    @Column (name = "Czech")
    @NotNull(message = "Czech translation cannot be null")
    private String cz;

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
                ", cz='" + cz + '\'' +
                ", code=" + code +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GeneratedWordsDto)) return false;
        GeneratedWordsDto that = (GeneratedWordsDto) o;
        return getCode() == that.getCode() && getEn().equals(that.getEn()) && Objects.equals(getRu(), that.getRu()) && Objects.equals(getFr(), that.getFr()) && Objects.equals(getCz(), that.getCz());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCode(), getEn(), getRu(), getFr(), getCz());
    }
}

