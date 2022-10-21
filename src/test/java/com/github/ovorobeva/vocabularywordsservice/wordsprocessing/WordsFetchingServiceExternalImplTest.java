package com.github.ovorobeva.vocabularywordsservice.wordsprocessing;

import com.github.ovorobeva.vocabularywordsservice.clients.LemmaClient;
import com.github.ovorobeva.vocabularywordsservice.clients.PartsOfSpeechClient;
import com.github.ovorobeva.vocabularywordsservice.clients.ProfanityCheckerClient;
import com.github.ovorobeva.vocabularywordsservice.clients.WordsClient;
import com.github.ovorobeva.vocabularywordsservice.exceptions.AuthTranslateException;
import com.github.ovorobeva.vocabularywordsservice.exceptions.GettingTranslateException;
import com.github.ovorobeva.vocabularywordsservice.exceptions.LimitExceededException;
import com.github.ovorobeva.vocabularywordsservice.exceptions.TranslationNotFoundException;
import com.github.ovorobeva.vocabularywordsservice.model.generated.GeneratedWordsDto;
import com.github.ovorobeva.vocabularywordsservice.service.WordsFetchingServiceExternalImpl;
import com.github.ovorobeva.vocabularywordsservice.translates.TranslateClient;
import com.github.ovorobeva.vocabularywordsservice.translates.testconf.TestClientMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class WordsFetchingServiceExternalImplTest {

    private final Random random = new Random();
    @InjectMocks
    private WordsFetchingServiceExternalImpl wordsFetchingServiceExternalImpl;
    @Mock
    private WordsClient wordsClient;
    @Mock
    private PartsOfSpeechClient partsOfSpeechClient;
    @Mock
    private TranslateClient translateClient;

    private TestClientMock testClientMock = new TestClientMock();
    @Mock
    private ProfanityCheckerClient profanityCheckerClient;
    @Mock
    private LemmaClient lemmaClient;

    private int count;

    @BeforeEach
    void beforeEach() throws InterruptedException, TranslationNotFoundException, AuthTranslateException, GettingTranslateException, LimitExceededException {
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
        Set<String> mockedList = new HashSet<>(Arrays.asList(WORDS).subList(0, count));
        System.out.println(mockedList);
        Mockito.when(wordsClient.getRandomWords(Mockito.anyInt())).thenReturn(mockedList);
        Mockito.when(lemmaClient.getLemma(Mockito.anyString())).then(AdditionalAnswers.returnsFirstArg());
        Mockito.when(partsOfSpeechClient.getPartsOfSpeech(Mockito.anyString())).thenReturn(List.of(new String[]{"noun"}));
        Mockito.when(profanityCheckerClient.isProfanity(Mockito.any())).thenReturn(false);
        Mockito.doAnswer(invocationOnMock -> {
            testClientMock.translateWord(
                    invocationOnMock.getArgument(0),
                    invocationOnMock.getArgument(1)
            );
            return null;
        }).when(translateClient).translateWord(Mockito.any(), Mockito.any());
    }

    @Test
    void getProcessedWordsTest() throws InterruptedException {
        int lastCode = random.nextInt(10);
        Set<GeneratedWordsDto> wordList = new HashSet<>();
        wordList.addAll(wordsFetchingServiceExternalImpl.getProcessedWords(count, lastCode));
        assertEquals(count, wordList.size());
        assertTrue(wordList.stream().anyMatch(wordsDto -> wordsDto.getCode() == lastCode + count - 1));
        /*assertThat(wordList.get(count - 1).getCode()).isEqualTo(lastCode + count - 1);
        assertThat(wordList.get(random.nextInt(count - 1) + 1).getFr()).containsSequence("Fr");
        assertThat(wordList.get(random.nextInt(count - 1) + 1).getRu()).containsSequence("Ru");
        assertThat(wordList.get(random.nextInt(count - 1) + 1).getCz()).containsSequence("Cz");
        assertThat(wordList.get(random.nextInt(count - 1) + 1).getEn()).isNotNull();*/
    }
}