package com.hyozlet.tempo.service;

import com.hyozlet.tempo.domain.Schedule;
import com.hyozlet.tempo.repository.ScheduleRepository;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

final class TestScheduleRepository implements ScheduleRepository {
    private final Map<Long, Schedule> schedules = new LinkedHashMap<>();
    private long nextId = 1;

    @Override
    public Schedule save(Schedule schedule) {
        var saved = schedule.withId(nextId++);
        schedules.put(saved.id(), saved);
        return saved;
    }

    @Override
    public Optional<Schedule> findById(long id) {
        return Optional.ofNullable(schedules.get(id));
    }

    @Override
    public List<Schedule> findByDate(LocalDate date) {
        return schedules.values().stream()
                .filter(schedule -> schedule.date().equals(date))
                .toList();
    }

    @Override
    public List<Schedule> findBetween(LocalDate startInclusive, LocalDate endInclusive) {
        return schedules.values().stream()
                .filter(schedule -> !schedule.date().isBefore(startInclusive))
                .filter(schedule -> !schedule.date().isAfter(endInclusive))
                .toList();
    }

    @Override
    public Schedule update(Schedule schedule) {
        if (schedule.id() == null || !schedules.containsKey(schedule.id())) {
            throw new NoSuchElementException("Schedule does not exist");
        }
        schedules.put(schedule.id(), schedule);
        return schedule;
    }
}
