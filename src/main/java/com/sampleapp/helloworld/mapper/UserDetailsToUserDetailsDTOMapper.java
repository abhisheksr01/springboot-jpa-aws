package com.sampleapp.helloworld.mapper;

import com.sampleapp.helloworld.controller.UserDetailsDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;

@Mapper
public interface UserDetailsToUserDetailsDTOMapper {

    UserDetailsToUserDetailsDTOMapper MAPPER =
            Mappers.getMapper(UserDetailsToUserDetailsDTOMapper.class);

    @Mapping(source = "userName", target = "userName")
    @Mapping(source = "dateOfBirth", target = "dateOfBirth")
    UserDetailsDTO mapUserInputsToUserDetailsDTO(String userName, LocalDate dateOfBirth);
}
