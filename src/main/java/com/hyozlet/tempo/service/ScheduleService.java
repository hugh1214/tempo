package com.hyozlet.tempo.service;

import com.hyozlet.tempo.domain.Schedule;
import com.hyozlet.tempo.repository.ScheduleRepository;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

public final class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final Clock clock;

    public ScheduleService(ScheduleRepository scheduleRepository, Clock clock) {
        this.scheduleRepository = Objects.requireNonNull(
                scheduleRepository,
                "scheduleRepository must not be null"
        );
        this.clock = Objects.requireNonNull(clock, "clock must not be null");
    }

    public Schedule save(Schedule schedule) {
        return scheduleRepository.save(Objects.requireNonNull(schedule, "schedule must not be null"));
    }

    public Optional<Schedule> findById(long id) {
        return scheduleRepository.findById(id);
    }

    public List<Schedule> findByDate(LocalDate date) {
        return scheduleRepository.findByDate(Objects.requireNonNull(date, "date must not be null"));
    }

    public List<Schedule> findToday() {
        return findByDate(LocalDate.now(clock));
    }

    public List<Schedule> findCurrentWeek() {
        var today = LocalDate.now(clock);
        var monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        return scheduleRepository.findBetween(monday, monday.plusDays(6));
    }

    public List<Schedule> findMonth(YearMonth month) {
        Objects.requireNonNull(month, "month must not be null");
        return scheduleRepository.findBetween(month.atDay(1), month.atEndOfMonth());
    }

    public Schedule setCompleted(long id, boolean completed) {
        var schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No schedule exists with ID " + id));
        return scheduleRepository.update(schedule.withCompleted(completed));
    }
}
