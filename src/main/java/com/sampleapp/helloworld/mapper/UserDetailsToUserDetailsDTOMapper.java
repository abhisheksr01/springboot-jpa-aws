package com.sampleapp.helloworld.mapper;

import com.sampleapp.helloworld.controller.UserDetailsDTO;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;

@Mapper
public interface UserDetailsToUserDetailsDTOMapper {

    UserDetailsToUserDetailsDTOMapper MAPPER =
            Mappers.getMapper(UserDetailsToUserDetailsDTOMapper.class);

    @Mapping(source = "userName", target = "userName")
    @Mapping(source = "dateOfBirth", target = "dateOfBirth")
    UserDetailsDTO mapUserInputsToUserDetailsDTO(String userName, LocalDate dateOfBirth);

    @AfterMapping
    default void convertNameLowerCaseAndTrim(@MappingTarget UserDetailsDTO userDetailsDTO) {
        userDetailsDTO.setUserName(userDetailsDTO.getUserName().toLowerCase().trim());
    }
}
