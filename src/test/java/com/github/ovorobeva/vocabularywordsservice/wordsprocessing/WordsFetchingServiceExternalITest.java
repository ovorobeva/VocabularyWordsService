package com.github.ovorobeva.vocabularywordsservice.wordsprocessing;

import com.github.ovorobeva.vocabularywordsservice.model.generated.GeneratedWordsDto;
import com.github.ovorobeva.vocabularywordsservice.service.WordsFetchingServiceExternal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Random;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WordsFetchingServiceExternalITest {

    @Autowired
    private WordsFetchingServiceExternal wordsFetchingServiceExternal;

    @Test
    void getRandomWordsTest() {
        final Random random = new Random();
        int count = random.nextInt(10) + 1;
        final Set<GeneratedWordsDto> generatedWords = wordsFetchingServiceExternal
                .getProcessedWords(count, random.nextInt(100));
        assertThat(generatedWords).hasSize(count);
    }
}