package com.gitjub.ovorobeva.vocabularywordsservice.dao;
import com.gitjub.ovorobeva.vocabularywordsservice.model.generated.GeneratedWords;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WordsRepository extends JpaRepository<GeneratedWords, Integer> {
}
