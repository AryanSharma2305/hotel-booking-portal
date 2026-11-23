package com.hotelbooking.config;

import java.time.Clock;
import java.time.ZoneId;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * "Today" is always taken from this clock, so the date rules can be tested
 * with a fixed date. Hotel dates are treated as Indian Standard Time.
 */
@Configuration
public class ClockConfig {

    @Bean
    public Clock clock() {
        return Clock.system(ZoneId.of("Asia/Kolkata"));
    }
}
