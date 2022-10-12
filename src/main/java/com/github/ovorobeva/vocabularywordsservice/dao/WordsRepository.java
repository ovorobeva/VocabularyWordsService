package com.github.ovorobeva.vocabularywordsservice.dao;

import com.github.ovorobeva.vocabularywordsservice.model.generated.GeneratedWordsDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * Repository to work with {@link GeneratedWordsDto}
 */
@Repository
public interface WordsRepository extends JpaRepository<GeneratedWordsDto, Integer> {
    Optional<GeneratedWordsDto> findByCode(int code);

    @Query("SELECT code FROM GeneratedWordsDto ORDER BY code")
    int[] getCodes();

    List<GeneratedWordsDto> getGeneratedWordsDtoByFrIsNull();
    List<GeneratedWordsDto> getGeneratedWordsDtoByCzIsNull();

    @Transactional
    void deleteByCode(int code);
    GeneratedWordsDto getByCode(int code);
}
