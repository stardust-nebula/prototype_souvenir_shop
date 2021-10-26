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
public class AdminInterceptor implements HandlerInterceptor {

    @Autowired
    private TokenService tokenService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Optional<String> header = Optional.ofNullable(request.getHeader("X-Token"));
        if (!header.isPresent()) {
            UUID tokenUuid = UUID.fromString(header.get());
            boolean isTokenExists = tokenService.isUserTokenExist(tokenUuid);
            long tokenId = tokenService.getTokenByUuid(tokenUuid).getId();

            if (isTokenExists) {
                if (tokenService.isTokenActive(tokenId)){
                    String userRole = tokenService.getTokenByUuid(tokenUuid).getUser().getUserRole().name();

                    if (userRole.equals("ADMIN")){
                        return true;
                    }else {
                        response.sendError(403, "Forbidden");
                        return false;
                    }
                }
            }
        }
        response.sendError(401, "Unauthorized");
        return false;
    }
}
