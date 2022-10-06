package com.github.ovorobeva.vocabularywordsservice.clients;

import com.github.ovorobeva.vocabularywordsservice.clients.apidocs.LemmaApi;
import com.github.ovorobeva.vocabularywordsservice.model.lemmas.LemmaDto;
import com.github.ovorobeva.vocabularywordsservice.model.lemmas.LemmaRequest;
import feign.RetryableException;
import lombok.SneakyThrows;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@FeignClient(name = "lemma", decode404 = true, url = "https://try.expert.ai/analysis/standard/en")
public interface LemmaClient extends LemmaApi {

    String SELDOM_WORD = "Word is not in use";

    @SneakyThrows
    default String getLemma(String word) {

        LemmaRequest request = new LemmaRequest(word);
        ResponseEntity<LemmaDto> response = getLemma(request);

        try {
            if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
                if (response.getBody().getData().getTokens().get(0).getSyncon() == -1)
                    return SELDOM_WORD;
                return response.getBody().getData().getTokens().get(0).getLemma();
            } else {
                return word;
            }
        } catch (RetryableException e) {
            try {
                Thread.sleep(15000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return getLemma(word);
        }
    }

}
