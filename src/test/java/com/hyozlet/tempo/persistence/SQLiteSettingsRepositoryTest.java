package com.hyozlet.tempo.persistence;

import com.hyozlet.tempo.domain.MeridiemPreference;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SQLiteSettingsRepositoryTest {
    @TempDir
    Path tempDirectory;

    @Test
    void defaultsToPmAndPersistsSubsequentChanges() {
        var databasePath = tempDirectory.resolve("tempo.db");
        var databaseManager = new DatabaseManager(databasePath);
        databaseManager.initializeSchema();
        var repository = new SQLiteSettingsRepository(databaseManager);

        assertEquals(MeridiemPreference.PM, repository.getDefaultMeridiem());

        repository.saveDefaultMeridiem(MeridiemPreference.AM);
        assertEquals(MeridiemPreference.AM, repository.getDefaultMeridiem());

        var reopenedManager = new DatabaseManager(databasePath);
        reopenedManager.initializeSchema();
        var reopenedRepository = new SQLiteSettingsRepository(reopenedManager);
        assertEquals(MeridiemPreference.AM, reopenedRepository.getDefaultMeridiem());

        reopenedRepository.saveDefaultMeridiem(MeridiemPreference.PM);
        assertEquals(MeridiemPreference.PM, reopenedRepository.getDefaultMeridiem());
    }
}
