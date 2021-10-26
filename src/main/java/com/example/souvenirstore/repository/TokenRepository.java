package com.example.souvenirstore.repository;

import com.example.souvenirstore.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TokenRepository extends JpaRepository<Token, Long> {

     Token getTokenByUuid(UUID uuid);
     boolean existsTokenByUuid(UUID uuid);
     List<Token> getTokenByUserId(long userId);
     Token getTokenById(long id);

     @Query("select t from Token t where t.expireDateTime <= :timeNow")
     List<Token> getAllByExpireDateTimeBefore(@Param("timeNow") LocalDateTime timeNow);

}
