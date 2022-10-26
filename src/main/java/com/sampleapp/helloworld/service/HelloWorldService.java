package com.sampleapp.helloworld.service;

import com.sampleapp.helloworld.controller.ResponseDTO;
import com.sampleapp.helloworld.repository.HelloWorldRepository;
import com.sampleapp.helloworld.repository.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDate;
import java.time.ZoneId;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.YEARS;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;


@Slf4j
@Service
public class HelloWorldService {

    @Autowired
    private HelloWorldRepository helloWorldRepository;

    public HelloWorldService(HelloWorldRepository helloWorldRepository) {
        this.helloWorldRepository = helloWorldRepository;
    }

    public void updateUserDetails(UserDetailsVO userDetailsVO) {
        log.info("HelloWorldService:updateUserDetails: getting userDetails...");
        User user = this.helloWorldRepository.findByNameIgnoreCase(userDetailsVO.getUserName());

        if (nonNull(user)) {
            log.debug("HelloWorldService:updateUserDetails: User: {} exist hence updating user details to: {}",
                    user, userDetailsVO);
            user.setName(userDetailsVO.getUserName());
            user.setDateOfBirth(userDetailsVO.getDateOfBirth());
        } else {
            user = new User(userDetailsVO.getUserName(), userDetailsVO.getDateOfBirth());
            log.debug("HelloWorldService:updateUserDetails: User: {} do not exist hence creating new user", user);
        }

        this.helloWorldRepository.save(user);
        log.debug("HelloWorldService:updateUserDetails: User: {} added/updated successfully", user);
    }

    public ResponseDTO getBirthdayMessage(String userName) {
        User user = this.helloWorldRepository.findByNameIgnoreCase(userName);
        if (isNull(user)) {
            log.debug("HelloWorldService:getBirthdayMessage: No user found with username: {} throwing error.", userName);
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND,
                    "No user found, please check the username");
        }
        String birthDayMessage = this.getMessage(user);
        log.debug("HelloWorldService:getBirthdayMessage: For user: {} returning birthday message: {}",
                user, birthDayMessage);
        return new ResponseDTO(birthDayMessage);
    }

    private String getMessage(User user) {
        final LocalDate dateOfBirth = user.getDateOfBirth();
        final LocalDate currentDate = LocalDate.now(ZoneId.of("UTC"));
        final long age = YEARS.between(dateOfBirth, currentDate);
        LocalDate nextBirthday = dateOfBirth.plusYears(age);
        log.debug("HelloWorldService:getMessage: Retrieving Birthday message for user: {}," +
                " currentDate: {}, age: {} and next birthday: {}", user, currentDate, age, nextBirthday);

        if (nextBirthday.equals(currentDate)) {
            return "Hello, " + user.getName() + "! Happy Birthday!";
        } else if (nextBirthday.isBefore(currentDate)) {
            nextBirthday = dateOfBirth.plusYears(age + 1);
        }

        long daysUntilNextBirthday = DAYS.between(currentDate, nextBirthday);
        log.debug("HelloWorldService:getMessage: For user: {}, the nextBirthday is on: {} and in days until: {}",
                user, nextBirthday, daysUntilNextBirthday);
        int days = Math.toIntExact(daysUntilNextBirthday);
        return "Hello, " + user.getName() + "! Your birthday is in " + days + " day(s)";
    }
}
