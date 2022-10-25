package com.sampleapp.helloworld.controller;

import com.sampleapp.helloworld.service.HelloWorldService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDate;
import java.time.ZoneId;

import static com.sampleapp.helloworld.mapper.UserDetailsToUserDetailsDTOMapper.MAPPER;
import static java.util.Objects.isNull;

@Slf4j
@RestController
public class HelloWorldController {

    private HelloWorldService helloWorldService;

    public HelloWorldController(HelloWorldService helloWorldService) {
        this.helloWorldService = helloWorldService;
    }

    @SneakyThrows
    @PutMapping(value = "/{userName}")
    public ResponseEntity updateUserDetails(@PathVariable String userName, @RequestBody UserDetails userDetails) {
        log.info("HelloWorldController:updateUserDetails: validating the request...");
        validateUserName(userName);
        validateDateOfBirth(userDetails);

        UserDetailsDTO userDetailsDTO = MAPPER.mapUserInputsToUserDetailsDTO(userName, userDetails.getDateOfBirth());

        log.debug("HelloWorldController:updateUserDetails: Invoking helloWorldService.updateUserDetails with : {}",
                userDetailsDTO);
        this.helloWorldService.updateUserDetails(userDetailsDTO);

        log.info("HelloWorldController:updateUserDetails: User details saved successfully");
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping(value = "/{userName}")
    public Response getBirthdayMessage(@PathVariable String userName) {
        log.info("HelloWorldController:updateUserDetails: validating the request...");
        validateUserName(userName);
        log.debug("HelloWorldController:updateUserDetails: Invoking helloWorldService.getBirthMessage with : {}",
                userName);
        return this.helloWorldService.getBirthdayMessage(userName.toLowerCase());
    }

    private void validateDateOfBirth(UserDetails userDetails) {
        LocalDate dateOfBirth = userDetails.getDateOfBirth();
        LocalDate currentDate = LocalDate.now(ZoneId.of("UTC"));
        if (isNull(dateOfBirth) || !dateOfBirth.isBefore(currentDate)) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
                    "Date of birth must be a date before the today date");
        }
    }

    private void validateUserName(String userName) {
        if (!userName.matches("[a-zA-Z]+")) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
                    "UserName must contain only letters");
        }
    }
}
