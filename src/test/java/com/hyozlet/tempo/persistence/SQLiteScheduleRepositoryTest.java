package com.hyozlet.tempo.persistence;

import com.hyozlet.tempo.domain.Priority;
import com.hyozlet.tempo.domain.Schedule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SQLiteScheduleRepositoryTest {
    private static final Instant CREATED_AT = Instant.parse("2026-09-01T09:00:00.123456Z");

    @TempDir
    Path tempDirectory;

    private Path databasePath;
    private SQLiteScheduleRepository repository;

    @BeforeEach
    void setUp() {
        databasePath = tempDirectory.resolve("tempo.db");
        var databaseManager = new DatabaseManager(databasePath);
        databaseManager.initializeSchema();
        repository = new SQLiteScheduleRepository(databaseManager);
    }

    @Test
    void savesScheduleWithGeneratedIdAndFindsItById() {
        var original = schedule("Team meeting", LocalDate.of(2026, 9, 10));

        var saved = repository.save(original);

        assertNotNull(saved.id());
        assertTrue(saved.id() > 0);
        assertEquals(saved, repository.findById(saved.id()).orElseThrow());
    }

    @Test
    void findsOnlySchedulesOnTheRequestedDate() {
        var requestedDate = LocalDate.of(2026, 9, 10);
        var first = repository.save(schedule("First", requestedDate));
        var second = repository.save(schedule("Second", requestedDate));
        repository.save(schedule("Different day", requestedDate.plusDays(1)));

        var foundIds = repository.findByDate(requestedDate).stream()
                .map(Schedule::id)
                .collect(Collectors.toSet());

        assertEquals(Set.of(first.id(), second.id()), foundIds);
    }

    @Test
    void findsSchedulesWithinAnInclusiveDateRange() {
        var start = LocalDate.of(2026, 9, 7);
        var end = LocalDate.of(2026, 9, 20);
        repository.save(schedule("Before", start.minusDays(1)));
        var atStart = repository.save(schedule("At start", start));
        var atEnd = repository.save(schedule("At end", end));
        repository.save(schedule("After", end.plusDays(1)));

        var foundIds = repository.findBetween(start, end).stream()
                .map(Schedule::id)
                .collect(Collectors.toSet());

        assertEquals(Set.of(atStart.id(), atEnd.id()), foundIds);
    }

    @Test
    void persistsCompletedStateThroughUpdate() {
        var saved = repository.save(schedule("Complete me", LocalDate.of(2026, 9, 10)));
        assertFalse(saved.completed());

        repository.update(saved.withCompleted(true));

        assertTrue(repository.findById(saved.id()).orElseThrow().completed());

        repository.update(saved.withCompleted(false));

        assertFalse(repository.findById(saved.id()).orElseThrow().completed());
    }

    @Test
    void retainsSchedulesAfterManagerAndRepositoryAreRecreated() {
        var saved = repository.save(schedule("Durable schedule", LocalDate.of(2026, 9, 10)));

        var reopenedManager = new DatabaseManager(databasePath);
        reopenedManager.initializeSchema();
        var reopenedRepository = new SQLiteScheduleRepository(reopenedManager);

        assertEquals(saved, reopenedRepository.findById(saved.id()).orElseThrow());
    }

    private static Schedule schedule(String title, LocalDate date) {
        return new Schedule(title, date, LocalTime.of(18, 30), Priority.HIGH, false, CREATED_AT);
    }
}
