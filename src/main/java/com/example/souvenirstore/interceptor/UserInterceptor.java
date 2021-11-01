package com.example.souvenirstore.interceptor;

import com.example.souvenirstore.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.UUID;

@Component
public class UserInterceptor implements HandlerInterceptor {

    @Autowired
    private TokenService tokenService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Optional<String> header = Optional.ofNullable(request.getHeader("X-Token"));
        if (header.isPresent()) {
            UUID tokenUuid = UUID.fromString(header.get());
            boolean isTokenExists = tokenService.isUserTokenExist(tokenUuid);
            long tokenId = tokenService.getTokenByUuid(tokenUuid).getId();

            if (isTokenExists) {
                if (tokenService.isTokenActive(tokenId)) {
                    return true;
                }
            }
        }
        response.sendError(401, "Unauthorized");
        return false;
    }
}
