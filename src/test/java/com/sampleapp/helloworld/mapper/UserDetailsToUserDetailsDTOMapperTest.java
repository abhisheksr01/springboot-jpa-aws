package com.sampleapp.helloworld.mapper;

import com.sampleapp.helloworld.controller.UserDetailsDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.ZoneId;

import static com.sampleapp.helloworld.mapper.UserDetailsToUserDetailsDTOMapper.MAPPER;

class UserDetailsToUserDetailsDTOMapperTest {
    @Test
    void mapUserInputsToUserDetailsDTO_shouldMapDetails() {
        UserDetailsDTO expectedUserDetailsDTO = new UserDetailsDTO();
        expectedUserDetailsDTO.setUserName("abhishek");
        expectedUserDetailsDTO.setDateOfBirth(LocalDate.now(ZoneId.of("UTC")));

        UserDetailsDTO actualUserDetailsDTO = MAPPER.mapUserInputsToUserDetailsDTO("abhishek",
                LocalDate.now(ZoneId.of("UTC")));

        Assertions.assertEquals(expectedUserDetailsDTO, actualUserDetailsDTO);

    }
}