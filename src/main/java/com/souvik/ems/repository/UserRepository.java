package com.souvik.ems.repository;

import com.souvik.ems.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);
    Page<User> findByFirstNameContainsIgnoreCase(String firstName, Pageable pageable);
    Page<User> findByLastNameContainsIgnoreCase(String lastName, Pageable pageable);
    Page<User> findByEmailContainsIgnoreCase(String firstName, Pageable pageable);
    Page<User> findByFirstNameContainsIgnoreCaseAndLastNameContainsIgnoreCase(String firstName, String lastName, Pageable pageable);
    Page<User> findByFirstNameContainsIgnoreCaseAndEmailContainsIgnoreCase(String firstName, String lastName, Pageable pageable);
    Page<User> findByLastNameContainsIgnoreCaseAndEmailContainsIgnoreCase(String lastName, String email, Pageable pageable);
    Page<User> findByFirstNameContainsIgnoreCaseAndLastNameContainsIgnoreCaseAndEmailContainsIgnoreCase(String firstName, String lastName, String email, Pageable pageable);

}