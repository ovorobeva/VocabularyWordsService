package com.github.ovorobeva.vocabularywordsservice.wordsprocessing;

import com.github.ovorobeva.vocabularywordsservice.model.generated.GeneratedWordsDto;
import com.github.ovorobeva.vocabularywordsservice.translates.TranslateFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
class WordsHandlerTest {

    private final Random random = new Random();
    @Autowired
    private WordsHandler wordsHandler;
    @Autowired
    private WordsClient wordsClient;
    @Autowired
    private PartsOfSpeechClient partsOfSpeechClient;
    @Autowired
    private TranslateFactory translateFactory;
    @Autowired
    private ProfanityCheckerClient profanityCheckerClient;
    @Autowired
    private LemmaClient lemmaClient;
    private int count;

    @BeforeEach
    void beforeEach() throws InterruptedException {
        final String[] WORDS = {"one",
                "two",
                "three",
                "four",
                "five",
                "six",
                "seven",
                "eight",
                "nine",
                "ten",
                "eleven"};
        count = random.nextInt(10) + 1;
        List<String> mockedList = new ArrayList<>(Arrays.asList(WORDS).subList(0, count));
        Mockito.when(wordsClient.getRandomWords(Mockito.anyInt())).thenReturn(mockedList);

        Mockito.when(lemmaClient.getLemma(Mockito.any())).then(AdditionalAnswers.returnsFirstArg());
        Mockito.when(partsOfSpeechClient.getPartsOfSpeech(Mockito.any())).thenReturn(List.of(new String[]{"noun"}));
        Mockito.when(profanityCheckerClient.isProfanity(Mockito.any())).thenReturn(false);
        Mockito.when(translateFactory.getTranslateClient(Mockito.any())).thenReturn(new TranslateClientTestConfiguration());
    }

    @Test
    void getProcessedWords() throws InterruptedException {


        int lastCode = random.nextInt(10);
        List<GeneratedWordsDto> wordList = new ArrayList<>();
        wordsHandler.getProcessedWords(wordList, count, lastCode);
        assertThat(wordList).hasSize(count);
        assertThat(wordList.get(count - 1).getCode()).isEqualTo(lastCode + count - 1);
        assertThat(wordList.get(random.nextInt(count - 1) + 1).getFr()).isNotNull();
        assertThat(wordList.get(random.nextInt(count - 1) + 1).getRu()).isNotNull();
        assertThat(wordList.get(random.nextInt(count - 1) + 1).getCz()).isNotNull();
        assertThat(wordList.get(random.nextInt(count - 1) + 1).getEn()).isNotNull();
    }
}