package com.sampleapp.helloworld.service;

import com.sampleapp.helloworld.controller.Response;
import com.sampleapp.helloworld.controller.UserDetailsDTO;
import com.sampleapp.helloworld.repository.HelloWorldRepository;
import com.sampleapp.helloworld.repository.dao.User;
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

@Service
public class HelloWorldService {

    @Autowired
    private HelloWorldRepository helloWorldRepository;

    public HelloWorldService(HelloWorldRepository helloWorldRepository) {
        this.helloWorldRepository = helloWorldRepository;
    }

    public void updateUserDetails(UserDetailsDTO userDetailsDTO) {
        User user = this.helloWorldRepository.findByNameIgnoreCase(userDetailsDTO.getUserName());
        if (nonNull(user)) {
            user.setName(userDetailsDTO.getUserName());
            user.setDateOfBirth(userDetailsDTO.getDateOfBirth());
        } else {
            user = new User(userDetailsDTO.getUserName(), userDetailsDTO.getDateOfBirth());
        }
        this.helloWorldRepository.save(user);
    }

    public Response getBirthdayMessage(String userName) {
        User user = this.helloWorldRepository.findByNameIgnoreCase(userName);
        if (isNull(user)) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND,
                    "No such user exists");
        }
        String name = user.getName();
        LocalDate dateOfBirth = user.getDateOfBirth();
        LocalDate currentDate = LocalDate.now(ZoneId.of("UTC"));
        long age = YEARS.between(dateOfBirth, currentDate);
        LocalDate nextBirthday = dateOfBirth.plusYears(age);
        if (nextBirthday.equals(currentDate)) {
            return new Response("Hello, " + name + "! Happy Birthday!");
        } else if (nextBirthday.isBefore(currentDate)) {
            nextBirthday = dateOfBirth.plusYears(age + 1);
        }
        long daysUntilNextBirthday = DAYS.between(currentDate, nextBirthday);
        int days = Math.toIntExact(daysUntilNextBirthday);
        return new Response("Hello, " + name + "! Your birthday is in " + days + " day(s)");
    }
}
