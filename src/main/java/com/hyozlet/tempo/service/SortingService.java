package com.hyozlet.tempo.service;

import com.hyozlet.tempo.domain.Priority;
import com.hyozlet.tempo.domain.Schedule;
import com.hyozlet.tempo.domain.SortType;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public final class SortingService {
    private static final Comparator<Schedule> BY_ID = Comparator.comparing(
            Schedule::id,
            Comparator.nullsLast(Comparator.naturalOrder())
    );
    private static final Comparator<Schedule> BY_TITLE = Comparator.comparing(Schedule::title);
    private static final Comparator<Schedule> BY_TIME = Comparator
            .comparing(Schedule::date)
            .thenComparing(Schedule::time)
            .thenComparing(Schedule::createdAt)
            .thenComparing(BY_ID)
            .thenComparing(BY_TITLE);
    private static final Comparator<Schedule> BY_PRIORITY = Comparator
            .comparingInt((Schedule schedule) -> priorityRank(schedule.priority()))
            .thenComparing(Schedule::date)
            .thenComparing(Schedule::time)
            .thenComparing(Schedule::createdAt)
            .thenComparing(BY_ID)
            .thenComparing(BY_TITLE);
    private static final Comparator<Schedule> BY_CREATED = Comparator
            .comparing(Schedule::createdAt)
            .thenComparing(BY_ID)
            .thenComparing(Schedule::date)
            .thenComparing(Schedule::time)
            .thenComparing(BY_TITLE);

    public List<Schedule> sort(List<Schedule> schedules, SortType sortType) {
        Objects.requireNonNull(schedules, "schedules must not be null");
        Objects.requireNonNull(sortType, "sortType must not be null");
        schedules.forEach(schedule -> Objects.requireNonNull(schedule, "schedule must not be null"));

        return schedules.stream()
                .sorted(comparatorFor(sortType))
                .toList();
    }

    private static Comparator<Schedule> comparatorFor(SortType sortType) {
        return switch (sortType) {
            case TIME -> BY_TIME;
            case PRIORITY -> BY_PRIORITY;
            case CREATED -> BY_CREATED;
        };
    }

    private static int priorityRank(Priority priority) {
        return switch (priority) {
            case HIGH -> 0;
            case MEDIUM -> 1;
            case LOW -> 2;
        };
    }
}
