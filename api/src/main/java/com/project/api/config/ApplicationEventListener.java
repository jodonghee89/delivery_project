package com.project.api.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ApplicationEventListener implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("Spring Application Context has been refreshed!");
        log.info("Application Name: {}", event.getApplicationContext().getApplicationName());
        log.info("Display Name: {}", event.getApplicationContext().getDisplayName());
    }
} 