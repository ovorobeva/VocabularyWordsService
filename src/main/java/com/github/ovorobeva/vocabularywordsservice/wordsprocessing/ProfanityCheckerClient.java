package com.github.ovorobeva.vocabularywordsservice.wordsprocessing;

import com.github.ovorobeva.vocabularywordsservice.exceptions.TooManyRequestsException;
import com.github.ovorobeva.vocabularywordsservice.wordsprocessing.apidocs.ProfanityCheckerApi;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@FeignClient(name = "profanity-checker", decode404 = true, url = "https://www.purgomalum.com/service/")
public interface ProfanityCheckerClient extends ProfanityCheckerApi {

    default boolean isProfanity(String word) {

        try {
            ResponseEntity<String> response = getLemma(word);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody().equals("true");
            } else if (response.getStatusCode().equals(HttpStatus.TOO_MANY_REQUESTS)
                    || response.getStatusCode().equals(HttpStatus.METHOD_NOT_ALLOWED)) {
                throw new TooManyRequestsException();
            } else {
                return false;
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return false;
        } catch (TooManyRequestsException e) {
            try {
                Thread.sleep(15000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return isProfanity(word);
        }
    }

}
