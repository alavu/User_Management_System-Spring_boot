package com.souvik.ems.service;

import com.souvik.ems.model.User;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public interface UserService {
    Long getUserCount();
    Page<User> getAllUser(int pageNo, int pageSize, String sortField, String sortOrder);
    void saveUser(User user);
    User getUser(UUID id);
    void deleteUser(UUID id);
    Page<User> search(String firstName, String lastName, String email, int pageNo, int pageSize, String sortField, String sortOrder);
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);
    String getLoggedInUserUsername();
}