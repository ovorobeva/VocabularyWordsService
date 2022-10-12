package com.github.ovorobeva.vocabularywordsservice.translates;

import com.github.ovorobeva.vocabularywordsservice.exceptions.AuthTranslateException;
import com.github.ovorobeva.vocabularywordsservice.exceptions.GettingTranslateException;
import com.github.ovorobeva.vocabularywordsservice.exceptions.LimitExceededException;
import com.github.ovorobeva.vocabularywordsservice.exceptions.TranslationNotFoundException;
import com.github.ovorobeva.vocabularywordsservice.model.generated.GeneratedWordsDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TranslateClientTest {

    @Autowired
    private TranslateClient translateClient;

    @Test
    void translateWordTestRu() throws AuthTranslateException, GettingTranslateException, LimitExceededException, IOException, InterruptedException, TranslationNotFoundException {
        GeneratedWordsDto word = new GeneratedWordsDto("word", 0);
        assertThat(word.getRu()).isNull();
        assertThat(word.getFr()).isNull();
        assertThat(word.getCz()).isNull();
        translateClient.translateWord(word, Language.RU);
        translateClient.translateWord(word, Language.FR);
        translateClient.translateWord(word, Language.CZ);
        assertThat(word.getRu()).isEqualTo("слово");
        assertThat(word.getCz()).isEqualTo("slovo");
        assertThat(word.getFr()).isEqualTo("mot");
    }
}