package com.github.ovorobeva.vocabularywordsservice.wordsprocessing;

import com.github.ovorobeva.vocabularywordsservice.clients.LemmaClient;
import com.github.ovorobeva.vocabularywordsservice.clients.ProfanityCheckerClient;
import com.github.ovorobeva.vocabularywordsservice.clients.WordsClient;
import com.github.ovorobeva.vocabularywordsservice.exceptions.AuthTranslateException;
import com.github.ovorobeva.vocabularywordsservice.exceptions.GettingTranslateException;
import com.github.ovorobeva.vocabularywordsservice.exceptions.LimitExceededException;
import com.github.ovorobeva.vocabularywordsservice.exceptions.TranslationNotFoundException;
import com.github.ovorobeva.vocabularywordsservice.model.generated.GeneratedWordsDto;
import com.github.ovorobeva.vocabularywordsservice.model.words.RandomWordsDto;
import com.github.ovorobeva.vocabularywordsservice.service.PartsOfSpeechService;
import com.github.ovorobeva.vocabularywordsservice.service.TranslateService;
import com.github.ovorobeva.vocabularywordsservice.service.impl.WordsFetchingServiceExternalImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class WordsFetchingServiceExternalImplTest {

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

    private final Random random = new Random();
    @InjectMocks
    private WordsFetchingServiceExternalImpl wordsFetchingServiceExternalImpl;
    @Mock
    private WordsClient wordsClient;
    @Mock
    private PartsOfSpeechService partsOfSpeechService;
    @Mock
    private TranslateService translateService;
    @Mock
    private ProfanityCheckerClient profanityCheckerClient;
    @Mock
    private LemmaClient lemmaClient;

    private int count;

    @BeforeEach
    void beforeEach() throws InterruptedException, TranslationNotFoundException, AuthTranslateException, GettingTranslateException, LimitExceededException {

        count = random.nextInt(10) + 2;
        final List<RandomWordsDto> mockedList = createList();
        System.out.println(mockedList);
        Mockito.when(wordsClient.getWords(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), any(), any(), any(), anyString()))
                .thenReturn(ResponseEntity.of(Optional.of(mockedList)));
        Mockito.when(lemmaClient.getLemma(any())).then(AdditionalAnswers.returnsFirstArg());
        Mockito.when(partsOfSpeechService.getPartsOfSpeech(Mockito.anyString())).thenReturn(List.of(new String[]{"noun"}));
        Mockito.when(profanityCheckerClient.isProfanity(any())).thenReturn(ResponseEntity.of(Optional.of("false")));
        Mockito.doAnswer(invocationOnMock -> {
            translateWord(invocationOnMock.getArgument(0));
            return null;
        }).when(translateService).translateWord(any());
    }

    private List<RandomWordsDto> createList() {
        return Arrays.asList(WORDS).subList(0, count)
                .stream().map(s -> {
                    RandomWordsDto word = new RandomWordsDto();
                    word.setWord(s);
                    return word;
                }).collect(Collectors.toList());
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

    private void translateWord(GeneratedWordsDto word) {
        word.setCz(word.getEn() + "Cz");
        word.setFr(word.getEn() + "Fr");
        word.setRu(word.getEn() + "Ru");
    }
}