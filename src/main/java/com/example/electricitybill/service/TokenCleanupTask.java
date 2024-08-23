package com.example.electricitybill.service;
import com.example.electricitybill.repository.BlacklistRepository;
import com.example.electricitybill.model.BlacklistedToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class TokenCleanupTask {

    @Autowired
    private BlacklistRepository blacklistRepository;

    @Scheduled(fixedRate = 3600000)
    public void cleanupExpiredTokens() {
        Date now = new Date();
        blacklistRepository.findAll().forEach(blacklistedToken -> {
            if (blacklistedToken.getLoginTime().before(new Date(now.getTime() - 3600000))) {
                blacklistRepository.delete(blacklistedToken);
            }
        });
    }
}

