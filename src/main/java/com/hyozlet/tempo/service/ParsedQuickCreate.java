package com.hyozlet.tempo.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public record ParsedQuickCreate(String title, LocalDate date, LocalTime time) {
    public ParsedQuickCreate {
        Objects.requireNonNull(title, "title must not be null");
        if (title.isBlank()) {
            throw new IllegalArgumentException("title must not be blank");
        }
        Objects.requireNonNull(date, "date must not be null");
        Objects.requireNonNull(time, "time must not be null");
    }
}
