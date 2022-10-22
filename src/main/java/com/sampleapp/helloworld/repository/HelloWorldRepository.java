package com.sampleapp.helloworld.repository;

import com.sampleapp.helloworld.repository.dao.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HelloWorldRepository extends JpaRepository<User, Long> {
    User findByNameIgnoreCase(String name);
}