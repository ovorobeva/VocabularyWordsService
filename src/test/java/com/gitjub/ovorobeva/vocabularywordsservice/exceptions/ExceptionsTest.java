package com.gitjub.ovorobeva.vocabularywordsservice.exceptions;

import com.gitjub.ovorobeva.vocabularywordsservice.translates.TranslateClient;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class ExceptionsTest extends TranslateClient {
    @Test
    void throwTooManyRequestsExceptionTest() {
        assertThatExceptionOfType(TooManyRequestsException.class).isThrownBy(() -> isSuccess(429));
    }
    @Test
    void throwLimitExceededExceptionTest() {
        assertThatExceptionOfType(LimitExceededException.class).isThrownBy(() -> isSuccess(456))
                .withMessage("Limit is exceeded, check your account https://www.deepl.com/ru/pro-account/usage");
    }
    @Test
    void throwAuthTranslateExceptionTest() {
        assertThatExceptionOfType(AuthTranslateException.class).isThrownBy(() -> isSuccess(403))
                .withMessage("Api key is compromised and needs to be checked in the account details https://www.deepl.com/ru/pro-account/usage");
    }
    @Test
    void throwGettingTranslateExceptionTest() {
        assertThatExceptionOfType(GettingTranslateException.class).isThrownBy(() -> isSuccess(101));
    }
}