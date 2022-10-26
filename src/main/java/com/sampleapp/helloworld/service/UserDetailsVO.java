package com.sampleapp.helloworld.service;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class UserDetailsVO {
    private String userName;
    private LocalDate dateOfBirth;
}
