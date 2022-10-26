package com.sampleapp.helloworld.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class BasicAuthConfig {

    private String authUser;
    private String authPassword;

    public BasicAuthConfig(@Value("${config.authUser}") String authUser, @Value("${config.authPassword}") String authPassword) {
        this.authUser = authUser;
        this.authPassword = authPassword;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests().anyRequest().authenticated()
                .and()
                .httpBasic();

        return http.build();
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        String auth = this.authUser;
        String password = this.authPassword;
        UserDetails user = User
                .withUsername(this.authUser)
                .password(this.authPassword)
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(user);
    }
}
