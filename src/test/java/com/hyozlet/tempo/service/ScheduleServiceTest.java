package com.hyozlet.tempo.service;

import com.hyozlet.tempo.domain.Priority;
import com.hyozlet.tempo.domain.Schedule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScheduleServiceTest {
    private static final Instant NOW = Instant.parse("2026-09-09T09:00:00Z");

    private TestScheduleRepository repository;
    private ScheduleService service;

    @BeforeEach
    void setUp() {
        repository = new TestScheduleRepository();
        service = new ScheduleService(repository, Clock.fixed(NOW, ZoneOffset.UTC));
    }

    @Test
    void savesAndFindsSchedulesThroughTheRepositoryContract() {
        var saved = service.save(schedule("Saved", LocalDate.of(2026, 9, 9)));

        assertEquals(saved, service.findById(saved.id()).orElseThrow());
        assertEquals(saved, service.findByDate(saved.date()).getFirst());
    }

    @Test
    void findsSchedulesForTodayUsingTheInjectedClock() {
        var today = service.save(schedule("Today", LocalDate.of(2026, 9, 9)));
        service.save(schedule("Tomorrow", LocalDate.of(2026, 9, 10)));

        assertEquals(java.util.List.of(today), service.findToday());
    }

    @Test
    void findsTheCurrentWeekFromMondayThroughSundayInclusively() {
        service.save(schedule("Before", LocalDate.of(2026, 9, 6)));
        var monday = service.save(schedule("Monday", LocalDate.of(2026, 9, 7)));
        var sunday = service.save(schedule("Sunday", LocalDate.of(2026, 9, 13)));
        service.save(schedule("After", LocalDate.of(2026, 9, 14)));

        assertEquals(java.util.List.of(monday, sunday), service.findCurrentWeek());
    }

    @Test
    void findsEveryScheduleInTheRequestedMonth() {
        service.save(schedule("Before", LocalDate.of(2026, 8, 31)));
        var first = service.save(schedule("First", LocalDate.of(2026, 9, 1)));
        var last = service.save(schedule("Last", LocalDate.of(2026, 9, 30)));
        service.save(schedule("After", LocalDate.of(2026, 10, 1)));

        assertEquals(java.util.List.of(first, last), service.findMonth(YearMonth.of(2026, 9)));
    }

    @Test
    void persistsCompletionAndCompletionCancellation() {
        var saved = service.save(schedule("Toggle", LocalDate.of(2026, 9, 9)));

        var completed = service.setCompleted(saved.id(), true);
        assertTrue(completed.completed());
        assertTrue(service.findById(saved.id()).orElseThrow().completed());

        var reopened = service.setCompleted(saved.id(), false);
        assertFalse(reopened.completed());
        assertFalse(service.findById(saved.id()).orElseThrow().completed());
    }

    @Test
    void rejectsCompletionChangesForAnUnknownId() {
        assertThrows(NoSuchElementException.class, () -> service.setCompleted(999, true));
    }

    private static Schedule schedule(String title, LocalDate date) {
        return new Schedule(title, date, LocalTime.of(18, 0), Priority.MEDIUM, false, NOW);
    }
}
