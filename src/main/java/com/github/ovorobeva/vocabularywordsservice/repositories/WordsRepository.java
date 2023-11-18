package com.github.ovorobeva.vocabularywordsservice.repositories;

import com.github.ovorobeva.vocabularywordsservice.model.generated.GeneratedWordsDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository to work with {@link GeneratedWordsDto}
 */
@Repository
public interface WordsRepository extends JpaRepository<GeneratedWordsDto, UUID> {
    Optional<GeneratedWordsDto> findByCode(int code);

    @Query("SELECT code FROM GeneratedWordsDto ORDER BY code")
    int[] getCodes();

    List<GeneratedWordsDto> getGeneratedWordsDtoByFrIsNull();
    List<GeneratedWordsDto> getGeneratedWordsDtoByCzIsNull();

    @Transactional
    void deleteByCode(int code);
    GeneratedWordsDto getByCode(int code);
}
