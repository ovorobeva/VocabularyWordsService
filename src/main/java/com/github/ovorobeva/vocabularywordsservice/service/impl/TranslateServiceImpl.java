package com.github.ovorobeva.vocabularywordsservice.service.impl;

import com.github.ovorobeva.vocabularywordsservice.clients.TranslateClient;
import com.github.ovorobeva.vocabularywordsservice.emailsender.EmailSender;
import com.github.ovorobeva.vocabularywordsservice.exceptions.AuthTranslateException;
import com.github.ovorobeva.vocabularywordsservice.exceptions.GettingTranslateException;
import com.github.ovorobeva.vocabularywordsservice.exceptions.LimitExceededException;
import com.github.ovorobeva.vocabularywordsservice.exceptions.TranslationNotFoundException;
import com.github.ovorobeva.vocabularywordsservice.model.generated.GeneratedWordsDto;
import com.github.ovorobeva.vocabularywordsservice.model.translate.TranslateDto;
import com.github.ovorobeva.vocabularywordsservice.service.TranslateService;
import com.github.ovorobeva.vocabularywordsservice.translates.Language;
import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSendException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class TranslateServiceImpl implements TranslateService {

    private final TranslateClient translateClient;
    private final EmailSender emailSender;

    @NonFinal
    @Value("${translation.api.key:}") //todo: to create file properties
    private String API_KEY;

    public void translateWord(final GeneratedWordsDto word) {
        try {
            for (Language language : Language.values()) {
                translateWord(word, language);
            }
        } catch (GettingTranslateException | InterruptedException e) {
            e.printStackTrace();
        } catch (LimitExceededException | AuthTranslateException | TranslationNotFoundException e ) {
            log.error(e.getMessage());
            try {
                emailSender.sendSimpleMessage(e.getMessage(), e.getMessage());
            } catch (MailSendException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    private void translateWord(final GeneratedWordsDto word, final Language targetLanguage) throws
            LimitExceededException,
            AuthTranslateException,
            GettingTranslateException,
            InterruptedException,
            TranslationNotFoundException {
        try {
            final ResponseEntity<TranslateDto> response = translateClient.getTranslate(word.getEn(),
                    API_KEY,
                    Language.EN.getValue(),
                    targetLanguage.getValue());
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                setTranslation(word, targetLanguage, response.getBody().getTranslations().get(0).getText());
            } else if (!response.hasBody()) {
                throw new TranslationNotFoundException(word.getEn());
            }
        } catch (RetryableException e) {
            e.printStackTrace();
            if (e.getCause() instanceof FeignException.FeignClientException) {
                throw new LimitExceededException("Limit is exceeded, check your account https://www.deepl.com/ru/pro-account/usage");
            } else if (e.getCause() instanceof FeignException.Forbidden) {
                throw new AuthTranslateException("Api key is compromised and needs to be checked in the account details https://www.deepl.com/ru/pro-account/usage");
            } else if (e.getCause() instanceof FeignException.NotFound) {
                throw new TranslationNotFoundException(word.getEn());
            } else {
                throw new GettingTranslateException();
            }
        }
    }

    private void setTranslation(final GeneratedWordsDto word, final Language targetLanguage, final String translation)
            throws TranslationNotFoundException {
        if (translation.isEmpty()){
            throw new TranslationNotFoundException(word.getEn());
        }
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