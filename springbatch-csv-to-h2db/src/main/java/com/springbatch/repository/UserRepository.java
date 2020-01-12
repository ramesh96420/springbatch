package com.springbatch.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springbatch.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {
}
