package com.example.souvenirstore.service;

import com.example.souvenirstore.entity.Token;
import com.example.souvenirstore.entity.User;
import com.example.souvenirstore.repository.TokenRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class TokenService {

    @Autowired
    private TokenRepository tokenRepository;

    public boolean isUserTokenExist(UUID uuid) {
        if (tokenRepository.existsTokenByUuid(uuid)) {
            log.info("IN isUserTokenExist: Token with {} uuid exists", uuid);
            return true;
        }
        log.info("IN isUserTokenExist: Token with {} uuid does not exist", uuid);
        return false;
    }

    public Token save(User user) {
        Token token = new Token();
        token.setUser(user);
        token.setUuid(UUID.randomUUID());
        token.setCreatedDateTime(LocalDateTime.now());
        LocalDateTime expDate = LocalDateTime.now().plusMinutes(60);
        token.setExpireDateTime(expDate);
        log.info("IN save: Save toke - {}", token);
        return tokenRepository.save(token);
    }

    public Token getTokenByUuid(UUID uuid) {
        return tokenRepository.getTokenByUuid(uuid);
    }

    public void delete(Token token) {
        tokenRepository.delete(token);
    }

    private List<Token> getTokenByUserId(long userId) {
        return tokenRepository.getTokenByUserId(userId);
    }

    public boolean isTokenActive(long tokenId) {
        Token token = tokenRepository.getTokenById(tokenId);
        LocalDateTime expDate = token.getExpireDateTime();

        boolean isExpEqNow = expDate.equals(LocalDateTime.now());
        boolean isExpInPast = expDate.isBefore(LocalDateTime.now());


        if (isExpEqNow || isExpInPast) {
            log.info("IN isTokenActive: Token is expired - {}", token);
            return false;
        }
        return true;
    }

    public boolean isUserHasActiveToken(long userId) {
        List<Token> userTokenList = getTokenByUserId(userId);
        boolean isActiveExist = userTokenList.stream()
                .anyMatch(i -> isTokenActive(i.getId()));

        if (userTokenList.isEmpty() || !isActiveExist) {
            log.info("IN isUserHasActiveToken: User does not have active token");
            return false;
        } else {
            log.info("IN isUserHasActiveToken: User has active token");
            return true;
        }
    }

    public Token getActiveTokenByUserId(long userId) {
        List<Token> listOfUserTokens = getTokenByUserId(userId);
        for (Token token : listOfUserTokens) {
            if (isTokenActive(token.getId())) {
                return token;
            }
        }
        return null;
    }

    public void deleteAllExpiredTokens() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        log.info("Getting list of expired tokens");
        List<Token> tokenList = tokenRepository.getAllByExpireDateTimeBefore(currentDateTime);
        log.info("{} expired tokens found", tokenList.size());
        tokenList.stream()
                .forEach(t -> delete(t));
    }
}
