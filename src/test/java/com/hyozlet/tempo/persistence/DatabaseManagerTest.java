package com.hyozlet.tempo.persistence;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DatabaseManagerTest {
    @TempDir
    Path tempDirectory;

    @Test
    void initializesRequiredTablesInAnEmptyDatabase() throws Exception {
        var databasePath = tempDirectory.resolve("nested/tempo.db");
        var databaseManager = new DatabaseManager(databasePath);

        databaseManager.initializeSchema();

        assertTrue(Files.isRegularFile(databasePath));
        try (var connection = databaseManager.openConnection();
             var statement = connection.prepareStatement("""
                     SELECT name
                     FROM sqlite_master
                     WHERE type = 'table' AND name IN ('schedules', 'settings', 'sort_preferences')
                     """);
             var resultSet = statement.executeQuery()) {
            var tables = new HashSet<String>();
            while (resultSet.next()) {
                tables.add(resultSet.getString("name"));
            }
            assertEquals(Set.of("schedules", "settings", "sort_preferences"), tables);
        }
    }
}
