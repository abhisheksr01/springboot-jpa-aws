package com.sampleapp.helloworld.security.SecurityConfig;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class BasicAuthConfigTest {

    @Autowired
    private BasicAuthConfig basicAuthConfig;
    @Autowired
    private HttpSecurity httpSecurity;

    @Test
    void userDetailsService_shouldReturnBasicAuthCredentials() {
        UserDetails expectedUserDetails = User.withUsername("abhishek").password("{noop}rajput").roles("USER")
                .build();

        UserDetails actualUserDetails = basicAuthConfig.userDetailsService().loadUserByUsername("abhishek");

        assertEquals(expectedUserDetails, actualUserDetails);
    }
}