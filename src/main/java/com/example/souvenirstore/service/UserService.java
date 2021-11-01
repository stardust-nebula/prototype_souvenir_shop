package com.example.souvenirstore.service;

import com.example.souvenirstore.entity.User;
import com.example.souvenirstore.entity.UserRoleEnum;
import com.example.souvenirstore.exception.ExceptionHandler;
import com.example.souvenirstore.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public boolean existsByEmailAddress(String emailAddress) {
        return userRepository.existsByEmailAddress(emailAddress);
    }

    public Optional<User> getUserByEmailAddress(String emailAddress) {
        if (!existsByEmailAddress(emailAddress)) {
            return Optional.empty();
        } else {
            return userRepository.getUserByEmailAddress(emailAddress);
        }
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.getUserByEmailAddress(username);
    }

    public void save(User user) throws ExceptionHandler {
        boolean isUserExistsByEmailAddress = userRepository.existsByEmailAddress(user.getEmailAddress());
        if (isUserExistsByEmailAddress) {
            log.warn("IN save - user: {} email already used", user.getEmailAddress());
            throw new ExceptionHandler("Email address already used");
        } else {
            user.setCreationDate(LocalDateTime.now());
            user.setUpdateDate(LocalDateTime.now());
            user.setUserRole(UserRoleEnum.USER);
            user.setPassword(user.getPassword());
            User registeredUser = userRepository.save(user);
            log.info("IN save - user: {} successfully registered", registeredUser);
        }
    }

    public List<User> findAll() {
        List<User> allUsers = userRepository.findAll();
        log.info("IN allUsers - {} users found", allUsers.size());
        return allUsers;
    }

    public Optional<User> login(String username, String password) throws ExceptionHandler {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new ExceptionHandler("User not found");
        } else if (!password.equals(user.get().getPassword())) {
            throw new ExceptionHandler("User credentials are invalid");
        }
        return user;
    }

    public User getUserById(long userId) {
        return userRepository.getUserById(userId);
    }

    public void changeUserRole(long userId, String userRole) {
        userRepository.changeUserRole(userId, UserRoleEnum.valueOf(userRole));
    }
}
