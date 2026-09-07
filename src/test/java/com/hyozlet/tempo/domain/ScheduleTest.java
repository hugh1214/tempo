package com.hyozlet.tempo.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ScheduleTest {
    private static final LocalDate DATE = LocalDate.of(2026, 9, 10);
    private static final LocalTime TIME = LocalTime.of(18, 0);
    private static final Instant CREATED_AT = Instant.parse("2026-09-01T09:00:00Z");

    @Test
    void createsValidSchedule() {
        var schedule = new Schedule("Team meeting", DATE, TIME, Priority.HIGH, false, CREATED_AT);

        assertAll(
                () -> assertNull(schedule.id()),
                () -> assertEquals("Team meeting", schedule.title()),
                () -> assertEquals(DATE, schedule.date()),
                () -> assertEquals(TIME, schedule.time()),
                () -> assertEquals(Priority.HIGH, schedule.priority()),
                () -> assertFalse(schedule.completed()),
                () -> assertEquals(CREATED_AT, schedule.createdAt())
        );
    }

    @Test
    void rejectsBlankTitle() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new Schedule("", DATE, TIME, Priority.LOW, false, CREATED_AT)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new Schedule("   ", DATE, TIME, Priority.LOW, false, CREATED_AT))
        );
    }

    @Test
    void rejectsNullRequiredFields() {
        assertAll(
                () -> assertThrows(NullPointerException.class,
                        () -> new Schedule(null, DATE, TIME, Priority.LOW, false, CREATED_AT)),
                () -> assertThrows(NullPointerException.class,
                        () -> new Schedule("Meeting", null, TIME, Priority.LOW, false, CREATED_AT)),
                () -> assertThrows(NullPointerException.class,
                        () -> new Schedule("Meeting", DATE, null, Priority.LOW, false, CREATED_AT)),
                () -> assertThrows(NullPointerException.class,
                        () -> new Schedule("Meeting", DATE, TIME, null, false, CREATED_AT)),
                () -> assertThrows(NullPointerException.class,
                        () -> new Schedule("Meeting", DATE, TIME, Priority.LOW, false, null))
        );
    }
}
