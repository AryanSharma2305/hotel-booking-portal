package com.hotelbooking.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AlternativeStay(LocalDate checkIn, LocalDate checkOut, BigDecimal totalAmount) {
}
