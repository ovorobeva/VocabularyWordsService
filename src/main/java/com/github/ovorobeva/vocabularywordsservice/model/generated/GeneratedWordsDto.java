package com.github.ovorobeva.vocabularywordsservice.model.generated;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.test.annotation.DirtiesContext;

import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.UUID;

@Setter
@Getter
@RequiredArgsConstructor
@Entity
@Table(name = "generated_words", uniqueConstraints = @UniqueConstraint(columnNames={"English", "Code"}))
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class GeneratedWordsDto {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column (name = "Code", unique = true)
    @NotNull(message = "Code cannot be null")
    private Integer code;

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

    public GeneratedWordsDto(String en, Integer code) {
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

