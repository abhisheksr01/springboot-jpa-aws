package com.sampleapp.helloworld.service;

import com.sampleapp.helloworld.controller.UserDetailsDTO;
import com.sampleapp.helloworld.repository.HelloWorldRepository;
import org.springframework.stereotype.Service;

@Service
public class HelloWorldService {

    private HelloWorldRepository helloWorldRepository;

    public HelloWorldService(HelloWorldRepository helloWorldRepository) {
        this.helloWorldRepository = helloWorldRepository;
    }

    public void updateUserDetails(UserDetailsDTO userDetailsDTO) {
        this.helloWorldRepository.updateUserDetailsInDB(userDetailsDTO);
    }
}
