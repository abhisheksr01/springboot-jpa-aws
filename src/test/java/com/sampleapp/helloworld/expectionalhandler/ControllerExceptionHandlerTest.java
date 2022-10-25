package com.sampleapp.helloworld.expectionalhandler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ControllerExceptionHandlerTest {
    private ControllerExceptionHandler controllerExceptionHandler;

    @BeforeEach
    void setUp() {
        controllerExceptionHandler = new ControllerExceptionHandler();
    }

    @Test
    void handleHttpClientErrorException_shouldReturn404AndErrorMessage_whenHttpClientErrorExceptionWith404Occurs() {
        HttpClientErrorException httpClientErrorException =
                new HttpClientErrorException(HttpStatus.NOT_FOUND, "No user found, please check the username");

        ResponseEntity responseEntity = controllerExceptionHandler.
                handleHttpClientErrorException(httpClientErrorException);

        assertEquals(404, responseEntity.getStatusCodeValue());
        assertEquals("No user found, please check the username", responseEntity.getBody());
    }

    @Test
    void handleHttpClientErrorException_shouldReturn500AndInternalServerErrorMessage_whenNoOtherScenarioMatches() {
        HttpClientErrorException httpClientErrorException =
                new HttpClientErrorException(HttpStatus.BANDWIDTH_LIMIT_EXCEEDED, "BandWidthLimit Exceeded");

        ResponseEntity responseEntity = controllerExceptionHandler.
                handleHttpClientErrorException(httpClientErrorException);

        assertEquals(500, responseEntity.getStatusCodeValue());
        assertEquals("Internal Server Error", responseEntity.getBody());
    }

    @ParameterizedTest
    @ValueSource(strings = {"UserName must contain only letters", "Date of birth must be a date before the today date"})
    void handleHttpClientErrorException_shouldReturn400AndBadRequestError_whenHttpClientErrorExceptionWith400Occurs(String expectedErrorMessage) {
        HttpClientErrorException httpClientErrorException =
                new HttpClientErrorException(HttpStatus.BAD_REQUEST, expectedErrorMessage);

        ResponseEntity responseEntity = controllerExceptionHandler.
                handleHttpClientErrorException(httpClientErrorException);

        assertEquals(400, responseEntity.getStatusCodeValue());
        assertEquals(expectedErrorMessage, responseEntity.getBody());
    }
}
