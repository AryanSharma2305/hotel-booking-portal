package com.hotelbooking.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;

import org.junit.jupiter.api.Test;

import com.hotelbooking.exception.BadRequestException;

/** Plain unit tests: pricing and date rules, with "today" fixed to 1 Jan 2027. */
class BookingRulesTest {

    private static final LocalDate TODAY = LocalDate.of(2027, 1, 1);

    private final Clock fixedClock = Clock.fixed(
            TODAY.atStartOfDay(ZoneId.of("Asia/Kolkata")).toInstant(), ZoneId.of("Asia/Kolkata"));
    private final BookingRules rules = new BookingRules(fixedClock, 365, 30);

    @Test
    void totalIsPricePerNightTimesNumberOfNights() {
        int nights = rules.countNights(LocalDate.of(2027, 1, 10), LocalDate.of(2027, 1, 13));

        assertThat(nights).isEqualTo(3);
        assertThat(rules.calculateTotal(new BigDecimal("5500.00"), nights)).isEqualByComparingTo("16500.00");
    }

    @Test
    void stayOverMonthEndIsCountedCorrectly() {
        assertThat(rules.countNights(LocalDate.of(2027, 1, 30), LocalDate.of(2027, 2, 2))).isEqualTo(3);
    }

    @Test
    void checkInTodayIsAllowed() {
        rules.validateDates(TODAY, TODAY.plusDays(1));
    }

    @Test
    void checkInInThePastIsRejected() {
        assertThatThrownBy(() -> rules.validateDates(TODAY.minusDays(1), TODAY.plusDays(2)))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("past");
    }

    @Test
    void checkOutOnSameDayOrBeforeCheckInIsRejected() {
        assertThatThrownBy(() -> rules.validateDates(TODAY.plusDays(5), TODAY.plusDays(5)))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("after the check-in");
        assertThatThrownBy(() -> rules.validateDates(TODAY.plusDays(5), TODAY.plusDays(3)))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void staysLongerThanMaximumAreRejected() {
        assertThatThrownBy(() -> rules.validateDates(TODAY.plusDays(1), TODAY.plusDays(32)))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("30 nights");
    }

    @Test
    void datesBeyondTheBookingWindowAreRejected() {
        assertThat(rules.areDatesAllowed(TODAY.plusDays(364), TODAY.plusDays(366))).isTrue();
        assertThat(rules.areDatesAllowed(TODAY.plusDays(365), TODAY.plusDays(367))).isFalse();
    }
}
