package com.example.souvenirstore.schedule;

import com.example.souvenirstore.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component

public class ClearExpiredTokenSchedule {

    @Autowired
    private TokenService tokenService;

    @Scheduled(cron = "0 0/30 * * * *")
    public void schedulerJob() {
        log.info("Delete expired tokens scheduled task STARTED");
        tokenService.deleteAllExpiredTokens();
        log.info("Delete expired tokens scheduled task FINISHED");
    }


}
