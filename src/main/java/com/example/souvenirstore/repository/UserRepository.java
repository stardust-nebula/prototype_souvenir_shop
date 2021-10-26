package com.example.souvenirstore.repository;

import com.example.souvenirstore.entity.User;
import com.example.souvenirstore.entity.UserRoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmailAddress(String emailAddress);
    Optional<User> getUserByEmailAddress(String emailAddress);
    Optional<User> getUserByUsername(String username);

    List<User> findAll();

    User getUserById(long userId);

    @Modifying
    @Query("update User u set u.userRole =?2 where u.id =?1")
    @Transactional
    void changeUserRole(long userId, UserRoleEnum userRoleEnum);

    Optional<User> findByEmailAddressAndPassword(String email, String password);
    Optional<User> findByUsername(String username);



}
