package com.hyozlet.tempo.repository;

import com.hyozlet.tempo.domain.Schedule;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ScheduleRepository {
    /**
     * Persists a schedule without an ID and returns it with the generated ID.
     */
    Schedule save(Schedule schedule);

    Optional<Schedule> findById(long id);

    List<Schedule> findByDate(LocalDate date);

    /**
     * Finds schedules whose dates are within the inclusive range.
     */
    List<Schedule> findBetween(LocalDate startInclusive, LocalDate endInclusive);

    /**
     * Replaces the persisted values of a schedule that has an ID.
     */
    Schedule update(Schedule schedule);
}
