package com.fitnessapp.repository;

import com.fitnessapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// All by myself
// Repository interface
// For accessing user data
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
