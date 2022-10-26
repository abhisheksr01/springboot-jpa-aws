package com.sampleapp.helloworld.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HelloWorldRepository extends JpaRepository<User, Long> {
    User findByNameIgnoreCase(String name);
}