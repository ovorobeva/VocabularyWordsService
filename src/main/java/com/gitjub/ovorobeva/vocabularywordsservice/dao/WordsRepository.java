package com.gitjub.ovorobeva.vocabularywordsservice.dao;

import com.gitjub.ovorobeva.vocabularywordsservice.model.generated.GeneratedWords;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WordsRepository extends JpaRepository<GeneratedWords, Integer> {
    Optional<GeneratedWords> findByCode(int code);

    @Query("SELECT code FROM GeneratedWords ORDER BY code")
    int[] getCodes();
}
