package com.souvik.ems.service;

import com.souvik.ems.model.User;
import com.souvik.ems.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImplementation implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public Long getUserCount() {
        return userRepository.count();
    }

    @Override
    public Page<User> getAllUser(int pageNo, int pageSize, String sortField, String sortOrder) {

        // if current order is "asc" then change it to "desc" and vice-versa
        Sort sort = sortOrder.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);

        return userRepository.findAll(pageable);
    }

    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Override
    public User getUser(UUID id) {
        Optional<User> user = userRepository.findById(id);
        return user.orElse(null);
    }

    @Override
    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }

    @Override
    public Page<User> search(String firstName, String lastName, String email, int pageNo, int pageSize, String sortField, String sortOrder) {

        // if current order is "asc" then change it to "desc" and vice-versa
        Sort sort = sortOrder.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);

        if (firstName.length() > 0 && lastName.length() > 0 && email.length() > 0)
            return userRepository.findByFirstNameContainsIgnoreCaseAndLastNameContainsIgnoreCaseAndEmailContainsIgnoreCase(firstName, lastName, email, pageable);
        else if (firstName.length() > 0 && lastName.length() > 0)
            return userRepository.findByFirstNameContainsIgnoreCaseAndLastNameContainsIgnoreCase(firstName, lastName, pageable);
        else if (firstName.length() > 0 && email.length() > 0)
            return userRepository.findByFirstNameContainsIgnoreCaseAndEmailContainsIgnoreCase(firstName, email, pageable);
        else if (lastName.length() > 0 && email.length() > 0)
            return userRepository.findByLastNameContainsIgnoreCaseAndEmailContainsIgnoreCase(lastName, email, pageable);
        else if (firstName.length() > 0)
            return userRepository.findByFirstNameContainsIgnoreCase(firstName, pageable);
        else if (lastName.length() > 0)
            return userRepository.findByLastNameContainsIgnoreCase(lastName, pageable);
        else if (email.length() > 0)
            return userRepository.findByEmailContainsIgnoreCase(email, pageable);
        // dummy return, condition checked at controller
        return userRepository.findAll(pageable);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public String getLoggedInUserUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String loggedInUserEmail = userDetails.getUsername();
        User loggedInUserDetails = userRepository.findByEmail(loggedInUserEmail).get();
        return loggedInUserDetails.getFirstName() + " " + loggedInUserDetails.getLastName();
    }
}