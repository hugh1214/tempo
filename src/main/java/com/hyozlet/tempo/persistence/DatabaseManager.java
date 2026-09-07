package com.hyozlet.tempo.persistence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

public final class DatabaseManager {
    private static final String CREATE_SCHEDULES = """
            CREATE TABLE IF NOT EXISTS schedules (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL CHECK (length(trim(title)) > 0),
                schedule_date TEXT NOT NULL,
                schedule_time TEXT NOT NULL,
                priority TEXT NOT NULL CHECK (priority IN ('HIGH', 'MEDIUM', 'LOW')),
                completed INTEGER NOT NULL CHECK (completed IN (0, 1)),
                created_at TEXT NOT NULL
            )
            """;
    private static final String CREATE_SCHEDULE_DATE_INDEX = """
            CREATE INDEX IF NOT EXISTS idx_schedules_schedule_date
            ON schedules(schedule_date)
            """;
    private static final String CREATE_SETTINGS = """
            CREATE TABLE IF NOT EXISTS settings (
                setting_key TEXT PRIMARY KEY,
                setting_value TEXT NOT NULL
            )
            """;
    private static final String CREATE_SORT_PREFERENCES = """
            CREATE TABLE IF NOT EXISTS sort_preferences (
                context TEXT PRIMARY KEY
                    CHECK (context IN ('TODAY_LIST', 'WEEK_LIST', 'TWO_WEEK_CALENDAR', 'MONTH_CALENDAR')),
                sort_type TEXT NOT NULL
                    CHECK (sort_type IN ('TIME', 'PRIORITY', 'CREATED'))
            )
            """;

    private final Path databasePath;

    public DatabaseManager(Path databasePath) {
        this.databasePath = Objects.requireNonNull(databasePath, "databasePath must not be null")
                .toAbsolutePath()
                .normalize();
    }

    public Path databasePath() {
        return databasePath;
    }

    public void initializeSchema() {
        try (var connection = openConnection()) {
            connection.setAutoCommit(false);
            try (var statement = connection.createStatement()) {
                statement.executeUpdate(CREATE_SCHEDULES);
                statement.executeUpdate(CREATE_SCHEDULE_DATE_INDEX);
                statement.executeUpdate(CREATE_SETTINGS);
                statement.executeUpdate(CREATE_SORT_PREFERENCES);
                connection.commit();
            } catch (SQLException exception) {
                rollback(connection, exception);
                throw exception;
            }
        } catch (SQLException exception) {
            throw new PersistenceException("Failed to initialize the SQLite schema", exception);
        }
    }

    Connection openConnection() throws SQLException {
        createDatabaseDirectory();
        return DriverManager.getConnection("jdbc:sqlite:" + databasePath);
    }

    private void createDatabaseDirectory() {
        var parent = databasePath.getParent();
        if (parent == null) {
            return;
        }

        try {
            Files.createDirectories(parent);
        } catch (IOException exception) {
            throw new PersistenceException("Failed to create the database directory: " + parent, exception);
        }
    }

    private static void rollback(Connection connection, SQLException originalException) {
        try {
            connection.rollback();
        } catch (SQLException rollbackException) {
            originalException.addSuppressed(rollbackException);
        }
    }
}
