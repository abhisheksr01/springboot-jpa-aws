package com.sampleapp.helloworld.controller;

import com.sampleapp.helloworld.service.HelloWorldService;
import com.sampleapp.helloworld.service.UserDetailsVO;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.ZoneId;

import static com.sampleapp.helloworld.mapper.UserDetailsToUserDetailsDTOMapper.MAPPER;

@Slf4j
@RestController
public class HelloWorldController {

    private HelloWorldService helloWorldService;

    public HelloWorldController(HelloWorldService helloWorldService) {
        this.helloWorldService = helloWorldService;
    }

    @SneakyThrows
    @PutMapping(value = "/{userName}")
    public ResponseEntity updateUserDetails(@PathVariable String userName, @RequestBody @Valid UserDetailsDTO userDetails) {
        log.info("HelloWorldController:updateUserDetails: validating the request...");
        userName = userName.trim();
        validateUserName(userName);
        validateDateOfBirth(userDetails);

        UserDetailsVO userDetailsVO = MAPPER.mapUserInputsToUserDetailsDTO(userName, userDetails.getDateOfBirth());

        log.debug("HelloWorldController:updateUserDetails: Invoking helloWorldService.updateUserDetails with : {}",
                userDetailsVO);
        this.helloWorldService.updateUserDetails(userDetailsVO);

        log.info("HelloWorldController:updateUserDetails: User details saved successfully");
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping(value = "/{userName}")
    public ResponseEntity<ResponseDTO> getBirthdayMessage(@PathVariable String userName) {
        log.info("HelloWorldController:updateUserDetails: validating the request...");
        userName = userName.trim();
        validateUserName(userName);
        log.debug("HelloWorldController:updateUserDetails: Invoking helloWorldService.getBirthMessage with : {}",
                userName);
        return new ResponseEntity(this.helloWorldService.getBirthdayMessage(userName), HttpStatus.OK);
    }

    private void validateDateOfBirth(UserDetailsDTO userDetails) {
        LocalDate dateOfBirth = userDetails.getDateOfBirth();
        LocalDate currentDate = LocalDate.now(ZoneId.of("UTC"));
        if (!dateOfBirth.isBefore(currentDate)) {
            log.debug("HelloWorldController:updateUserDetails: validation failed dateOfBirth: {} is not before currentDate: {}",
                    dateOfBirth, currentDate);
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
                    "Date of birth must be a date before the today date");
        }
    }

    private void validateUserName(String userName) {
        if (!userName.matches("^[a-zA-Z]+(([',. -][a-zA-Z ])?[a-zA-Z]*)*$")) {
            log.debug("HelloWorldController:updateUserDetails: validation failed userName: {} contains non letter characters",
                    userName);
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
                    "UserName must contain only letters");
        }
    }

}
