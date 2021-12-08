package com.gitjub.ovorobeva.vocabularywordsservice.translates;

import com.gitjub.ovorobeva.vocabularywordsservice.model.generated.GeneratedWordsDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
class TranslateClientRuTest {

    @Autowired
    private TranslateFactory translateFactory;

    @Test
    void translateWordTest() {
        GeneratedWordsDto word = new GeneratedWordsDto("word", 0);
        assertThat(word.getRu()).isNull();
        translateFactory.getTranslateClient(Language.RU).translateWord(word);
        assertThat(word.getRu()).isEqualTo("слово");
    }
}