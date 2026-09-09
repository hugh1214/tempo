package com.hyozlet.tempo.service;

import com.hyozlet.tempo.domain.Priority;
import com.hyozlet.tempo.domain.Schedule;

import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class QuickCreateService {
    private final QuickCreateParser parser;
    private final SettingsService settingsService;
    private final ScheduleService scheduleService;
    private final Clock clock;

    public QuickCreateService(
            QuickCreateParser parser,
            SettingsService settingsService,
            ScheduleService scheduleService,
            Clock clock
    ) {
        this.parser = Objects.requireNonNull(parser, "parser must not be null");
        this.settingsService = Objects.requireNonNull(settingsService, "settingsService must not be null");
        this.scheduleService = Objects.requireNonNull(scheduleService, "scheduleService must not be null");
        this.clock = Objects.requireNonNull(clock, "clock must not be null");
    }

    public Schedule create(String input, Priority priority) {
        Objects.requireNonNull(priority, "priority must not be null");
        var parsed = parser.parse(input, settingsService.getDefaultMeridiem());
        var schedule = new Schedule(
                parsed.title(),
                parsed.date(),
                parsed.time(),
                priority,
                false,
                Instant.now(clock)
        );
        return scheduleService.save(schedule);
    }
}
