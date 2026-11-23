package com.hotelbooking.service;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** Creates room inventory on startup and extends it by one day every night. */
@Component
public class InventoryScheduler {

    private final InventoryService inventoryService;

    public InventoryScheduler(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onStartup() {
        inventoryService.fillBookingWindow();
    }

    @Scheduled(cron = "0 5 0 * * *", zone = "Asia/Kolkata")
    public void everyNight() {
        inventoryService.fillBookingWindow();
    }
}
