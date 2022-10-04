package com.github.ovorobeva.vocabularywordsservice.clients;

import com.github.ovorobeva.vocabularywordsservice.clients.apidocs.PartsOfSpeechApi;
import com.github.ovorobeva.vocabularywordsservice.exceptions.TooManyRequestsException;
import com.github.ovorobeva.vocabularywordsservice.model.partsofspeech.PartsOfSpeechDto;
import feign.codec.DecodeException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
@FeignClient(name = "parts-of-speech", decode404 = true, url = "https://api.dictionaryapi.dev/api/v2/entries/")
public interface PartsOfSpeechClient extends PartsOfSpeechApi {

    default List<String> getPartsOfSpeech(String word) throws InterruptedException {
        try {
            ResponseEntity<List<PartsOfSpeechDto>> response = findPartsOfSpeech(word);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List<PartsOfSpeechDto.Meaning> meanings = response.getBody().get(0).getMeanings();
                if (meanings.isEmpty()) return null;
                return getPartsOfSpeechFromMeanings(meanings);
            } else if (response.getStatusCode().equals(HttpStatus.TOO_MANY_REQUESTS)
                    || response.getStatusCode().equals(HttpStatus.SERVICE_UNAVAILABLE)
                    || response.getStatusCode().equals(HttpStatus.METHOD_NOT_ALLOWED)) {
                throw new TooManyRequestsException();
            } else
                return null;
        } catch (IllegalStateException | DecodeException e) {
            e.printStackTrace();
            return null;
        } catch (TooManyRequestsException e) {
            Thread.sleep(15000);
            e.printStackTrace();
            return getPartsOfSpeech(word);
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
