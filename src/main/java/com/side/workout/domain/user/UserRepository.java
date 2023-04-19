package com.side.workout.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    //select * from users where username = ?
    Optional<User> findByUsername(String username);
}
