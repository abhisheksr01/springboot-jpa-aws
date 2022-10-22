package com.sampleapp.helloworld.mapper;

import com.sampleapp.helloworld.controller.UserDetailsDTO;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.time.ZoneId;

import static com.sampleapp.helloworld.mapper.UserDetailsToUserDetailsDTOMapper.MAPPER;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UserDetailsToUserDetailsDTOMapperTest {
    @ParameterizedTest
    @ValueSource(strings = {"Abhishek", "ABHISHEK", " abhishek "})
    void mapUserInputsToUserDetailsDTO_shouldMapDetails(String userName) {
        UserDetailsDTO expectedUserDetailsDTO = new UserDetailsDTO("abhishek",
                LocalDate.now(ZoneId.of("UTC")));

        UserDetailsDTO actualUserDetailsDTO = MAPPER.mapUserInputsToUserDetailsDTO(userName,
                LocalDate.now(ZoneId.of("UTC")));

        assertEquals(expectedUserDetailsDTO, actualUserDetailsDTO);

    }
}