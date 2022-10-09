package com.github.ovorobeva.vocabularywordsservice.wordsprocessing;

import com.github.ovorobeva.vocabularywordsservice.clients.LemmaClient;
import com.github.ovorobeva.vocabularywordsservice.clients.PartsOfSpeechClient;
import com.github.ovorobeva.vocabularywordsservice.clients.ProfanityCheckerClient;
import com.github.ovorobeva.vocabularywordsservice.clients.WordsClient;
import com.github.ovorobeva.vocabularywordsservice.model.generated.GeneratedWordsDto;
import com.github.ovorobeva.vocabularywordsservice.service.WordsHandler;
import com.github.ovorobeva.vocabularywordsservice.translates.TranslateClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
class WordsHandlerTest {

    private final Random random = new Random();
    @Mock
    private WordsHandler wordsHandler;
    @Mock
    private WordsClient wordsClient;
    @Mock
    private PartsOfSpeechClient partsOfSpeechClient;
    @Mock
    private TranslateClient translateClient;
    @Mock
    private ProfanityCheckerClient profanityCheckerClient;
    @Mock
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
        count = random.nextInt(10) + 2;
        List<String> mockedList = new ArrayList<>(Arrays.asList(WORDS).subList(0, count));
        System.out.println(mockedList);
        Mockito.when(wordsClient.getRandomWords(Mockito.anyInt())).thenReturn(mockedList);

        Mockito.when(lemmaClient.getLemma(Mockito.anyString())).then(AdditionalAnswers.returnsFirstArg());
        Mockito.when(partsOfSpeechClient.getPartsOfSpeech(Mockito.any())).thenReturn(List.of(new String[]{"noun"}));
        Mockito.when(profanityCheckerClient.isProfanity(Mockito.any())).thenReturn(false);
    }

    @Test
    void getProcessedWordsTest() throws InterruptedException {
        int lastCode = random.nextInt(10);
        System.out.println(String.format("Last code is %s", lastCode));
        List<GeneratedWordsDto> wordList = new ArrayList<>();
        wordsHandler.getProcessedWords(wordList, count, lastCode);
        assertThat(wordList).hasSize(count);
        assertThat(wordList.get(count - 1).getCode()).isEqualTo(lastCode + count - 1);
        assertThat(wordList.get(random.nextInt(count - 1) + 1).getFr()).containsSequence("Fr");
        assertThat(wordList.get(random.nextInt(count - 1) + 1).getRu()).containsSequence("Ru");
        assertThat(wordList.get(random.nextInt(count - 1) + 1).getCz()).containsSequence("Cz");
        assertThat(wordList.get(random.nextInt(count - 1) + 1).getEn()).isNotNull();
    }
}