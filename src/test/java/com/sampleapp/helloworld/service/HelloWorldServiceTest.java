package com.sampleapp.helloworld.service;

import com.sampleapp.helloworld.controller.Response;
import com.sampleapp.helloworld.controller.UserDetailsDTO;
import com.sampleapp.helloworld.repository.HelloWorldRepository;
import com.sampleapp.helloworld.repository.dao.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDate;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class HelloWorldServiceTest {
    private HelloWorldRepository mockHelloWorldRepository;
    private HelloWorldService helloWorldService;
    private LocalDate oneDayOldLocalDate = LocalDate.now(ZoneId.of("UTC")).minusDays(1);

    @BeforeEach
    void setUp() {
        mockHelloWorldRepository = mock(HelloWorldRepository.class);
        helloWorldService = new HelloWorldService(mockHelloWorldRepository);
    }

    @Test
    void updateUserDetailsInDB_shouldInvokeRepositorySaveMethodOnce_whenUserExist() {
        String expectedUserName = "expectedUserName";
        UserDetailsDTO userDetailsDTO = new UserDetailsDTO(expectedUserName, oneDayOldLocalDate);
        User expectedUser = new User(expectedUserName, oneDayOldLocalDate);
        User mockExistingUser = new User(expectedUserName, oneDayOldLocalDate);
        when(mockHelloWorldRepository.findByNameIgnoreCase(expectedUserName)).thenReturn(mockExistingUser);

        helloWorldService.updateUserDetails(userDetailsDTO);

        verify(mockHelloWorldRepository, times(1)).save(expectedUser);
    }

    @Test
    void updateUserDetailsInDB_shouldInvokeRepositorySaveMethodOnce_whenUserDoNotExist() {
        UserDetailsDTO userDetailsDTO = new UserDetailsDTO("abhishek", oneDayOldLocalDate);
        User expectedUser = new User("abhishek", oneDayOldLocalDate);

        helloWorldService.updateUserDetails(userDetailsDTO);

        verify(mockHelloWorldRepository, times(1)).save(expectedUser);
    }

    @Test
    void getBirthdayMessage_shouldThrowExceptionIfUserDoNotExist() {
        String userName = "abhishek";
        String expectedErrorMessage = "404 No such user exists";
        String actualErrorMessage = null;
        when(mockHelloWorldRepository.findByNameIgnoreCase(userName)).thenReturn(null);

        try {
            helloWorldService.getBirthdayMessage(userName);
        } catch (HttpClientErrorException exception) {
            actualErrorMessage = exception.getMessage();
        }

        assertEquals(expectedErrorMessage, actualErrorMessage);
    }

    @Test
    void getBirthdayMessage_shouldReturnHappyBirthdayMessage_whenUsersBirthdayIsToday() {
        String expectedUserName = "abhishek";
        String expectedMessage = "Hello, " + expectedUserName + "! Happy Birthday!";
        LocalDate oneYearOldDate = LocalDate.now(ZoneId.of("UTC")).minusYears(1);
        User mockUser = new User(expectedUserName, oneYearOldDate);
        when(mockHelloWorldRepository.findByNameIgnoreCase(expectedUserName)).thenReturn(mockUser);

        Response actualResponse = helloWorldService.getBirthdayMessage(expectedUserName);

        assertEquals(expectedMessage, actualResponse.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = {20, 44})
    void getBirthdayMessage_shouldReturnBirthdayMessageWithDays_whenUserBirthdayIsInFuture(int expectedBirthdayInDays) {
        String expectedUserName = "abhishek";
        String expectedMessage = "Hello, " + expectedUserName + "! Your birthday is in " + expectedBirthdayInDays + " day(s)";
        LocalDate dateOfBirth = LocalDate.now(ZoneId.of("UTC")).minusYears(1).plusDays(expectedBirthdayInDays);
        User mockUser = new User(expectedUserName, dateOfBirth);
        when(mockHelloWorldRepository.findByNameIgnoreCase(expectedUserName)).thenReturn(mockUser);

        Response actualResponse = helloWorldService.getBirthdayMessage(expectedUserName);

        assertEquals(expectedMessage, actualResponse.getMessage());
    }

    @ParameterizedTest
    @CsvSource({
            "20, 345",
            "44, 321"
    })
    void getBirthdayMessage_shouldReturnBirthdayMessageWithDays_whenUsersBirthdayIsInPast(int daysInPast, int expectedBirthdayInDays) {
        String expectedUserName = "abhishek";
        String expectedMessage = "Hello, " + expectedUserName + "! Your birthday is in " + expectedBirthdayInDays + " day(s)";
        LocalDate dateOfBirth = LocalDate.now(ZoneId.of("UTC")).minusYears(1).minusDays(daysInPast);
        User mockUser = new User(expectedUserName, dateOfBirth);
        when(mockHelloWorldRepository.findByNameIgnoreCase(expectedUserName)).thenReturn(mockUser);

        Response actualResponse = helloWorldService.getBirthdayMessage(expectedUserName);

        assertEquals(expectedMessage, actualResponse.getMessage());
    }
}
