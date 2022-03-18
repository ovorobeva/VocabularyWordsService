package com.github.ovorobeva.vocabularywordsservice.translates;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TranslateFactoryTest {

    @Autowired
    private TranslateFactory translateFactory;
    @Test
    void getTranslateClientRu() {
        assertThat(translateFactory.getTranslateClient(Language.RU)).isInstanceOf(TranslateClientRu.class);
    }
    @Test
    void getTranslateClientFr() {
        assertThat(translateFactory.getTranslateClient(Language.FR)).isInstanceOf(TranslateClientFr.class);
    }
    @Test
    void getTranslateClientCz() {
        assertThat(translateFactory.getTranslateClient(Language.CS)).isInstanceOf(TranslateClientCz.class);
    }
    @Test
    void getTranslateClientWrongLanguage() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> translateFactory.getTranslateClient(Language.valueOf("Wrong language")));

    }
}