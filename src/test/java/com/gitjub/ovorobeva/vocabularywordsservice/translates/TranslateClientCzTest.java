package com.gitjub.ovorobeva.vocabularywordsservice.translates;

import com.gitjub.ovorobeva.vocabularywordsservice.model.generated.GeneratedWordsDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
class TranslateClientCzTest {

    @Autowired
    private TranslateFactory translateFactory;

    @Test
    void translateWordTest() {
        GeneratedWordsDto word = new GeneratedWordsDto("word", 0);
        assertThat(word.getCz()).isNull();
        translateFactory.getTranslateClient(Language.CZ).translateWord(word);
        assertThat(word.getCz()).isEqualTo("slovo");
    }
}