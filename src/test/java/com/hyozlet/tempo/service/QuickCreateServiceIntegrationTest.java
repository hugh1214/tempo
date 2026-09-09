package com.hyozlet.tempo.service;

import com.hyozlet.tempo.domain.MeridiemPreference;
import com.hyozlet.tempo.domain.Priority;
import com.hyozlet.tempo.persistence.DatabaseManager;
import com.hyozlet.tempo.persistence.SQLiteScheduleRepository;
import com.hyozlet.tempo.persistence.SQLiteSettingsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class QuickCreateServiceIntegrationTest {
    private static final Instant NOW = Instant.parse("2026-09-09T09:00:00Z");

    @TempDir
    Path tempDirectory;

    @Test
    void parsesAndPersistsSchedulesUsingTheStoredMeridiemSetting() {
        var manager = new DatabaseManager(tempDirectory.resolve("tempo.db"));
        manager.initializeSchema();
        var scheduleRepository = new SQLiteScheduleRepository(manager);
        var settingsService = new SettingsService(new SQLiteSettingsRepository(manager));
        var clock = Clock.fixed(NOW, ZoneOffset.UTC);
        var scheduleService = new ScheduleService(scheduleRepository, clock);
        var quickCreateService = new QuickCreateService(
                new QuickCreateParser(clock),
                settingsService,
                scheduleService,
                clock
        );

        var pmSchedule = quickCreateService.create("내일 6시 팀 회의", Priority.HIGH);

        assertNotNull(pmSchedule.id());
        assertEquals("팀 회의", pmSchedule.title());
        assertEquals(LocalDate.of(2026, 9, 10), pmSchedule.date());
        assertEquals(LocalTime.of(18, 0), pmSchedule.time());
        assertEquals(Priority.HIGH, pmSchedule.priority());
        assertFalse(pmSchedule.completed());
        assertEquals(NOW, pmSchedule.createdAt());
        assertEquals(pmSchedule, scheduleRepository.findById(pmSchedule.id()).orElseThrow());

        settingsService.setDefaultMeridiem(MeridiemPreference.AM);
        var amSchedule = quickCreateService.create("내일 6시 아침 운동", Priority.LOW);

        assertEquals(LocalTime.of(6, 0), amSchedule.time());
        assertEquals(amSchedule, scheduleRepository.findById(amSchedule.id()).orElseThrow());
    }
}
