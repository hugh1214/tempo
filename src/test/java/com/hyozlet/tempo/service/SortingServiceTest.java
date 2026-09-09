package com.hyozlet.tempo.service;

import com.hyozlet.tempo.domain.Priority;
import com.hyozlet.tempo.domain.Schedule;
import com.hyozlet.tempo.domain.SortType;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

class SortingServiceTest {
    private final SortingService service = new SortingService();

    @Test
    void sortsByDateTimeCreatedAtAndIdForTimeOrder() {
        var laterDate = schedule(1, "Later date", "2026-09-10", "08:00", Priority.HIGH, "2026-09-01T08:00:00Z");
        var laterCreated = schedule(4, "Later created", "2026-09-09", "09:00", Priority.LOW, "2026-09-01T10:00:00Z");
        var higherId = schedule(3, "Higher id", "2026-09-09", "09:00", Priority.MEDIUM, "2026-09-01T09:00:00Z");
        var lowerId = schedule(2, "Lower id", "2026-09-09", "09:00", Priority.HIGH, "2026-09-01T09:00:00Z");

        var result = service.sort(List.of(laterDate, laterCreated, higherId, lowerId), SortType.TIME);

        assertEquals(List.of(lowerId, higherId, laterCreated, laterDate), result);
    }

    @Test
    void sortsPriorityUsingExplicitHighMediumLowOrder() {
        var low = schedule(1, "Low", "2026-09-08", "08:00", Priority.LOW, "2026-09-01T08:00:00Z");
        var medium = schedule(2, "Medium", "2026-09-08", "08:00", Priority.MEDIUM, "2026-09-01T08:00:00Z");
        var highLater = schedule(3, "High later", "2026-09-10", "08:00", Priority.HIGH, "2026-09-01T08:00:00Z");
        var highEarlier = schedule(4, "High earlier", "2026-09-09", "08:00", Priority.HIGH, "2026-09-01T08:00:00Z");

        var result = service.sort(List.of(low, highLater, medium, highEarlier), SortType.PRIORITY);

        assertEquals(List.of(highEarlier, highLater, medium, low), result);
    }

    @Test
    void sortsByCreatedAtAndUsesIdAsTheFirstTieBreaker() {
        var createdLater = schedule(1, "Created later", "2026-09-09", "08:00", Priority.HIGH, "2026-09-02T08:00:00Z");
        var higherId = schedule(3, "Higher id", "2026-09-08", "08:00", Priority.LOW, "2026-09-01T08:00:00Z");
        var lowerId = schedule(2, "Lower id", "2026-09-10", "08:00", Priority.MEDIUM, "2026-09-01T08:00:00Z");

        var result = service.sort(List.of(createdLater, higherId, lowerId), SortType.CREATED);

        assertEquals(List.of(lowerId, higherId, createdLater), result);
    }

    @Test
    void returnsANewListWithoutChangingTheInputOrder() {
        var first = schedule(1, "First", "2026-09-10", "08:00", Priority.LOW, "2026-09-02T08:00:00Z");
        var second = schedule(2, "Second", "2026-09-09", "08:00", Priority.HIGH, "2026-09-01T08:00:00Z");
        var input = new ArrayList<>(List.of(first, second));
        var originalOrder = List.copyOf(input);

        var result = service.sort(input, SortType.TIME);

        assertEquals(originalOrder, input);
        assertEquals(List.of(second, first), result);
        assertNotSame(input, result);
    }

    private static Schedule schedule(
            long id,
            String title,
            String date,
            String time,
            Priority priority,
            String createdAt
    ) {
        return new Schedule(
                id,
                title,
                LocalDate.parse(date),
                LocalTime.parse(time),
                priority,
                false,
                Instant.parse(createdAt)
        );
    }
}
