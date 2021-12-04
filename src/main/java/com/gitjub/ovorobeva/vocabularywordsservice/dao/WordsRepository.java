package com.gitjub.ovorobeva.vocabularywordsservice.dao;

import com.gitjub.ovorobeva.vocabularywordsservice.model.generated.GeneratedWordsDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WordsRepository extends JpaRepository<GeneratedWordsDto, Integer> {
    Optional<GeneratedWordsDto> findByCode(int code);

    @Query("SELECT code FROM GeneratedWordsDto ORDER BY code")
    int[] getCodes();

    List<GeneratedWordsDto> getGeneratedWordsDtoByFrIsNull();

}
