package com.sampleapp.helloworld.service;

import com.sampleapp.helloworld.controller.UserDetailsDTO;
import com.sampleapp.helloworld.repository.HelloWorldRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.ZoneId;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

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
    void updateUserDetailsInDB_shouldBeInvokedOnce_withSameParams() {
        UserDetailsDTO userDetailsDTO = new UserDetailsDTO();
        userDetailsDTO.setUserName("abhishek");
        userDetailsDTO.setDateOfBirth(oneDayOldLocalDate);

        helloWorldService.updateUserDetails(userDetailsDTO);

        Mockito.verify(mockHelloWorldRepository, times(1)).updateUserDetailsInDB(userDetailsDTO);
    }
}