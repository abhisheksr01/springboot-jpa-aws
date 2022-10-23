package com.sampleapp.helloworld.mapper;

import com.sampleapp.helloworld.controller.UserDetailsDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.time.ZoneId;

import static com.sampleapp.helloworld.mapper.UserDetailsToUserDetailsDTOMapper.MAPPER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

class UserDetailsToUserDetailsDTOMapperTest {
    LocalDate currentLocalData = LocalDate.now(ZoneId.of("UTC"));

    @ParameterizedTest
    @ValueSource(strings = {"Abhishek", "ABHISHEK", " abhishek "})
    void mapUserInputsToUserDetailsDTO_shouldMapDetails(String actualUserName) {
        String expectedUserName = "abhishek";
        UserDetailsDTO expectedUserDetailsDTO = new UserDetailsDTO(expectedUserName,
                currentLocalData);

        UserDetailsDTO actualUserDetailsDTO = MAPPER.mapUserInputsToUserDetailsDTO(actualUserName,
                currentLocalData);

        assertEquals(expectedUserDetailsDTO, actualUserDetailsDTO);
    }

    @Test
    void mapUserInputsToUserDetailsDTO_whenUserNameIsNull_returnsNull() {
        UserDetailsDTO actualUserDetailsDTO = MAPPER.mapUserInputsToUserDetailsDTO(null, null);

        assertNull(actualUserDetailsDTO);
    }

    @Test
    void convertNameLowerCaseAndTrim_whenUpperCaseUntrimmedIsPassed() {
        UserDetailsDTO mockUserDetailsDTO = mock(UserDetailsDTO.class);
        when(mockUserDetailsDTO.getUserName()).thenReturn(" Abhishek ");

        MAPPER.convertNameLowerCaseAndTrim(mockUserDetailsDTO);

        verify(mockUserDetailsDTO, times(1)).setUserName("abhishek");
    }
}