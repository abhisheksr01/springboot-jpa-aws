package com.sampleapp.helloworld.controller;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class UserDetailsDTO {
    private String userName;
    private LocalDate dateOfBirth;
}
