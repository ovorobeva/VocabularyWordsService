package com.github.ovorobeva.vocabularywordsservice.translates;

import com.github.ovorobeva.vocabularywordsservice.clients.apidocs.TranslateApi;
import com.github.ovorobeva.vocabularywordsservice.exceptions.AuthTranslateException;
import com.github.ovorobeva.vocabularywordsservice.exceptions.GettingTranslateException;
import com.github.ovorobeva.vocabularywordsservice.exceptions.LimitExceededException;
import com.github.ovorobeva.vocabularywordsservice.exceptions.TranslationNotFoundException;
import com.github.ovorobeva.vocabularywordsservice.model.generated.GeneratedWordsDto;
import com.github.ovorobeva.vocabularywordsservice.model.translate.TranslateDto;
import feign.FeignException;
import feign.RetryableException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@FeignClient(name = "translation", url = "https://api-free.deepl.com/v2/")
public interface TranslateClient extends TranslateApi {

    String API_KEY = System.getenv("TRANSLATION_API_KEY");

    default void translateWord(final GeneratedWordsDto word, final Language targetLanguage) throws
            LimitExceededException,
            AuthTranslateException,
            GettingTranslateException,
            InterruptedException,
            TranslationNotFoundException {

        try {
            ResponseEntity<TranslateDto> response = getTranslate(word.getEn(),
                    API_KEY,
                    Language.EN.getValue(),
                    targetLanguage.getValue());
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                setTranslation(word, targetLanguage, response.getBody().getTranslations().get(0).getText());
            } else if (!response.hasBody()) {
                throw new TranslationNotFoundException(String.format(
                        "Cz translation for the word %s is not found.", word.getEn()));
            }
        } catch (RetryableException e) {
            e.printStackTrace();
            if (e.getCause() instanceof FeignException.TooManyRequests) {
                Thread.sleep(10000);
                translateWord(word, targetLanguage);
            } else if (e.getCause() instanceof FeignException.FeignClientException) {
                throw new LimitExceededException("Limit is exceeded, check your account https://www.deepl.com/ru/pro-account/usage");
            } else if (e.getCause() instanceof FeignException.Forbidden) {
                throw new AuthTranslateException("Api key is compromised and needs to be checked in the account details https://www.deepl.com/ru/pro-account/usage");
            } else if (e.getCause() instanceof FeignException.NotFound) {
                throw new TranslationNotFoundException(String.format(
                        "Cz translation for the word %s is not found.", word.getEn()));
            } else {
                throw new GettingTranslateException();
            }
        }
    }

    private void setTranslation(GeneratedWordsDto word, Language targetLanguage, String translation) {
        switch (targetLanguage) {
            case FR:
                word.setFr(translation);
                break;
            case RU:
                word.setRu(translation);
                break;
            case CZ:
                word.setCz(translation);
                break;
        }
    }
}