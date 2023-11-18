package com.github.ovorobeva.vocabularywordsservice.service.impl;

import com.github.ovorobeva.vocabularywordsservice.clients.PartsOfSpeechClient;
import com.github.ovorobeva.vocabularywordsservice.model.partsofspeech.PartsOfSpeechDto;
import com.github.ovorobeva.vocabularywordsservice.service.PartsOfSpeechService;
import feign.codec.DecodeException;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
@NonFinal
@RequiredArgsConstructor
@Slf4j
public class PartsOfSpeechServiceImpl implements PartsOfSpeechService {

    final private PartsOfSpeechClient partsOfSpeechClient;

    public List<String> getPartsOfSpeech(final String word) {
        try {
            final ResponseEntity<List<PartsOfSpeechDto>> response = partsOfSpeechClient.findPartsOfSpeech(word);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List<PartsOfSpeechDto.Meaning> meanings = response.getBody().get(0).getMeanings();
                if (meanings.isEmpty()) return null;
                return getPartsOfSpeechFromMeanings(meanings);
            } else {
                return null;
            }
        } catch (DecodeException e) {
            log.error("Failed to get part of speech for the word {}", word);
            e.printStackTrace();
            return null;
        }
    }

    private List<String> getPartsOfSpeechFromMeanings(List<PartsOfSpeechDto.Meaning> meanings){

        final List<String> partsOfSpeech = new LinkedList<>();
        for (PartsOfSpeechDto.Meaning message : meanings) {
            String partOfSpeech = message.getPartOfSpeech();
            if (partOfSpeech != null && !partOfSpeech.isEmpty()) {
                partsOfSpeech.add(partOfSpeech.toLowerCase());
            }
        }
        return partsOfSpeech;
    }


}
