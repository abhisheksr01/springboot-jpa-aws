package com.sampleapp.helloworld.controller;

import com.sampleapp.helloworld.service.HelloWorldService;
import com.sampleapp.helloworld.service.UserDetailsVO;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class HelloWorldControllerTest {

    private HelloWorldController helloWorldController;
    private HelloWorldService mockHelloWorldService;
    private UserDetailsDTO userDetails;
    private LocalDate currentLocalDate = LocalDate.now(ZoneId.of("UTC"));
    private LocalDate oneDayOldLocalDate = currentLocalDate.minusDays(1);

    @SneakyThrows
    @BeforeEach
    void setUp() {
        mockHelloWorldService = mock(HelloWorldService.class);
        helloWorldController = new HelloWorldController(mockHelloWorldService);
        userDetails = new UserDetailsDTO();
    }

    @Test
    void updateUserDetails_shouldReturn204NoContent() throws ParseException {
        userDetails.setDateOfBirth(oneDayOldLocalDate);

        ResponseEntity actualResponse = helloWorldController.updateUserDetails("AbHishek", userDetails);

        assertEquals(HttpStatus.NO_CONTENT, actualResponse.getStatusCode());
    }

    @Test
    void updateUserDetails_shouldTrimUserName() {
        UserDetailsVO expectedUserDetailsDTODTO = new UserDetailsVO("AbhisHek", oneDayOldLocalDate);
        userDetails.setDateOfBirth(oneDayOldLocalDate);

        helloWorldController.updateUserDetails(" AbhisHek", userDetails);

        verify(mockHelloWorldService, times(1)).updateUserDetails(expectedUserDetailsDTODTO);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            " 12345abhishek", "@bhishek", "1234", "select * from table"
    })
    void updateUserDetails_shouldThrowBadRequestException_whenNonAlphabeticUserNameIsPassed(String actualUserName) {
        String expectedErrorMessage = "400 UserName must contain only letters";
        String actualErrorMessage = null;
        userDetails.setDateOfBirth(oneDayOldLocalDate);

        try {
            helloWorldController.updateUserDetails(actualUserName, userDetails);
        } catch (HttpClientErrorException exception) {
            actualErrorMessage = exception.getMessage();
        }

        assertEquals(expectedErrorMessage, actualErrorMessage);
    }

    @Test
    void updateUserDetails_shouldThrowBadRequestException_whenCurrentDateIsPassed() {
        String expectedErrorMessage = "400 Date of birth must be a date before the today date";
        String actualErrorMessage = null;
        userDetails.setDateOfBirth(currentLocalDate);

        try {
            helloWorldController.updateUserDetails("abhishek", userDetails);
        } catch (HttpClientErrorException exception) {
            actualErrorMessage = exception.getMessage();
        }

        assertEquals(expectedErrorMessage, actualErrorMessage);
    }

    @Test
    void updateUserDetails_shouldThrowBadRequestException_whenFutureDateIsPassed() {
        String expectedErrorMessage = "400 Date of birth must be a date before the today date";
        String actualErrorMessage = null;
        userDetails.setDateOfBirth(currentLocalDate.plusDays(1));

        try {
            helloWorldController.updateUserDetails("abhishek", userDetails);
        } catch (HttpClientErrorException exception) {
            actualErrorMessage = exception.getMessage();
        }

        assertEquals(expectedErrorMessage, actualErrorMessage);
    }

    @Test
    void updateUserDetails_shouldInvokeService_whenValidInputDataIsPassed() {
        UserDetailsVO userDetailsVO = new UserDetailsVO("aBhishek", oneDayOldLocalDate);
        userDetails.setDateOfBirth(oneDayOldLocalDate);

        helloWorldController.updateUserDetails("aBhishek", userDetails);

        verify(mockHelloWorldService, times(1)).updateUserDetails(userDetailsVO);
    }

    @ParameterizedTest
    @ValueSource(strings = {" Abhishek", " Abhishek "})
    void getBirthdayMessage_shouldReturnBirthDayMessage(String actualUserName) {
        String expectedMessage = "Hello,Abhishek! Happy Birthday";
        ResponseDTO expectedResponse = new ResponseDTO(expectedMessage);
        when(mockHelloWorldService.getBirthdayMessage("Abhishek")).thenReturn(expectedResponse);

        ResponseEntity<ResponseDTO> actualResponse = helloWorldController.getBirthdayMessage(actualUserName);

        assertEquals(expectedMessage, actualResponse.getBody().getMessage());
    }
}