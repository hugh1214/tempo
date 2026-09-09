package com.hyozlet.tempo.service;

import com.hyozlet.tempo.domain.MeridiemPreference;
import com.hyozlet.tempo.persistence.DatabaseManager;
import com.hyozlet.tempo.persistence.SQLiteSettingsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SettingsServiceTest {
    @TempDir
    Path tempDirectory;

    @Test
    void readsTheDefaultPmAndPersistsAmAndPmChanges() {
        var manager = new DatabaseManager(tempDirectory.resolve("tempo.db"));
        manager.initializeSchema();
        var service = new SettingsService(new SQLiteSettingsRepository(manager));

        assertEquals(MeridiemPreference.PM, service.getDefaultMeridiem());

        service.setDefaultMeridiem(MeridiemPreference.AM);
        assertEquals(MeridiemPreference.AM, service.getDefaultMeridiem());

        service.setDefaultMeridiem(MeridiemPreference.PM);
        assertEquals(MeridiemPreference.PM, service.getDefaultMeridiem());
    }
}
