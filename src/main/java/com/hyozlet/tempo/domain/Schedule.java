package com.hyozlet.tempo.domain;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public record Schedule(
        Long id,
        String title,
        LocalDate date,
        LocalTime time,
        Priority priority,
        boolean completed,
        Instant createdAt
) {
    public Schedule {
        if (id != null && id <= 0) {
            throw new IllegalArgumentException("id must be positive when present");
        }
        Objects.requireNonNull(title, "title must not be null");
        if (title.isBlank()) {
            throw new IllegalArgumentException("title must not be blank");
        }
        Objects.requireNonNull(date, "date must not be null");
        Objects.requireNonNull(time, "time must not be null");
        Objects.requireNonNull(priority, "priority must not be null");
        Objects.requireNonNull(createdAt, "createdAt must not be null");
    }

    public Schedule(
            String title,
            LocalDate date,
            LocalTime time,
            Priority priority,
            boolean completed,
            Instant createdAt
    ) {
        this(null, title, date, time, priority, completed, createdAt);
    }

    public Schedule withId(long generatedId) {
        return new Schedule(generatedId, title, date, time, priority, completed, createdAt);
    }

    public Schedule withCompleted(boolean newCompleted) {
        return new Schedule(id, title, date, time, priority, newCompleted, createdAt);
    }
}
