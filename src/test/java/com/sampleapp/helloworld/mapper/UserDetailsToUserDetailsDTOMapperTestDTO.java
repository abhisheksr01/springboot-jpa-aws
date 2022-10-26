package com.sampleapp.helloworld.mapper;

import com.sampleapp.helloworld.service.UserDetailsVO;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.ZoneId;

import static com.sampleapp.helloworld.mapper.UserDetailsToUserDetailsDTOMapper.MAPPER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserDetailsToUserDetailsDTOMapperTestDTO {
    LocalDate currentLocalData = LocalDate.now(ZoneId.of("UTC"));

    @Test
    void mapUserInputsToUserDetailsDTO_shouldMapDetails() {
        String userName = "abhishek";
        UserDetailsVO expectedUserDetailsVO = new UserDetailsVO(userName,
                currentLocalData);

        UserDetailsVO actualUserDetailsVO = MAPPER.mapUserInputsToUserDetailsDTO(userName,
                currentLocalData);

        assertEquals(expectedUserDetailsVO, actualUserDetailsVO);
    }

    @Test
    void mapUserInputsToUserDetailsDTO_whenUserNameIsNull_returnsNull() {
        UserDetailsVO actualUserDetailsVO = MAPPER.mapUserInputsToUserDetailsDTO(null, null);

        assertNull(actualUserDetailsVO);
    }
}