package com.github.ovorobeva.vocabularywordsservice.service.impl;

import com.github.ovorobeva.vocabularywordsservice.clients.LemmaClient;
import com.github.ovorobeva.vocabularywordsservice.clients.ProfanityCheckerClient;
import com.github.ovorobeva.vocabularywordsservice.clients.WordsClient;
import com.github.ovorobeva.vocabularywordsservice.model.generated.GeneratedWordsDto;
import com.github.ovorobeva.vocabularywordsservice.model.lemmas.LemmaDto;
import com.github.ovorobeva.vocabularywordsservice.model.lemmas.LemmaRequest;
import com.github.ovorobeva.vocabularywordsservice.model.words.RandomWordsDto;
import com.github.ovorobeva.vocabularywordsservice.properties.WordsProperties;
import com.github.ovorobeva.vocabularywordsservice.service.PartsOfSpeechService;
import com.github.ovorobeva.vocabularywordsservice.service.TranslateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

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
    @Mock
    private WordsProperties wordsProperties;

    @InjectMocks
    private WordsFetchingServiceExternalImpl wordsFetchingServiceExternalImpl;

    private Integer count;

    @BeforeEach
    void beforeEach() {

        count = random.nextInt(10) + 2;

        final List<RandomWordsDto> mockedList = createList();
        System.out.println("mockedList" + mockedList);
        when(wordsProperties.getWordsApiKey()).thenReturn("mockKey");
        when(wordsClient.getWords(anyString(), anyString(), anyString(), anyString(),
                        anyString(), anyString(), any(), any(), any(), anyString()))
                .thenReturn(ResponseEntity.ok(mockedList));//todo: sometimes returns weird 
        when(lemmaClient.getLemma(any()))
                .thenAnswer(invocationOnMock -> ResponseEntity.ok(createLemma(invocationOnMock.getArgument(0))));
        Mockito.when(partsOfSpeechService.getPartsOfSpeech(Mockito.anyString())).thenReturn(List.of(new String[]{"noun"}));
        Mockito.when(profanityCheckerClient.isProfanity(any())).thenReturn(ResponseEntity.ok().body("false"));
        Mockito.doAnswer(invocationOnMock -> {
            translateWord(invocationOnMock.getArgument(0));
            return null;
        }).when(translateService).translateWord(any());
    }

    @Test
    void getProcessedWordsTest() {
        int lastCode = random.nextInt(10);
        System.out.println("lastCode = " + lastCode + " count = " + count);
        Set<GeneratedWordsDto> wordList = new HashSet<>();
        wordList.addAll(wordsFetchingServiceExternalImpl.getProcessedWords(count, lastCode));
        System.out.println("wordList" + wordList);
        assertEquals(count, wordList.size());
        assertTrue(wordList.stream().anyMatch(wordsDto -> wordsDto.getCode() == lastCode + count - 1));

        wordList.forEach(wordsDto -> {
            assertFalse(wordsDto.getEn().isEmpty());
            assertTrue(wordsDto.getFr().contains("Fr"));
            assertTrue(wordsDto.getRu().contains("Ru"));
            assertTrue(wordsDto.getCz().contains("Cz"));
        });
    }

    private List<RandomWordsDto> createList() {
        return Arrays.asList(WORDS).subList(0, count)
                .stream().map(s -> {
                    RandomWordsDto word = new RandomWordsDto();
                    word.setWord(s);
                    return word;
                }).collect(Collectors.toList());
    }

    private void translateWord(GeneratedWordsDto word) {
        word.setCz(word.getEn() + "Cz");
        word.setFr(word.getEn() + "Fr");
        word.setRu(word.getEn() + "Ru");
    }

    private LemmaDto createLemma(LemmaRequest request) {
        final LemmaDto lemmaDto = new LemmaDto();
        final LemmaDto.Data data = new LemmaDto.Data();
        data.setTokens(List.of(new LemmaDto.Token()));
        data.getTokens().get(0).setLemma(request.getText());
        lemmaDto.setData(data);

        return lemmaDto;
    }
}