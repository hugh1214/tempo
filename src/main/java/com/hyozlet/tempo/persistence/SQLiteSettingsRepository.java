package com.hyozlet.tempo.persistence;

import com.hyozlet.tempo.domain.MeridiemPreference;
import com.hyozlet.tempo.repository.SettingsRepository;

import java.sql.SQLException;
import java.util.Objects;

public final class SQLiteSettingsRepository implements SettingsRepository {
    private static final String DEFAULT_MERIDIEM_KEY = "default_meridiem";
    private static final String SELECT = """
            SELECT setting_value
            FROM settings
            WHERE setting_key = ?
            """;
    private static final String UPSERT = """
            INSERT INTO settings (setting_key, setting_value)
            VALUES (?, ?)
            ON CONFLICT(setting_key) DO UPDATE SET setting_value = excluded.setting_value
            """;

    private final DatabaseManager databaseManager;

    public SQLiteSettingsRepository(DatabaseManager databaseManager) {
        this.databaseManager = Objects.requireNonNull(databaseManager, "databaseManager must not be null");
    }

    @Override
    public MeridiemPreference getDefaultMeridiem() {
        try (var connection = databaseManager.openConnection();
             var statement = connection.prepareStatement(SELECT)) {
            statement.setString(1, DEFAULT_MERIDIEM_KEY);
            try (var resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return MeridiemPreference.PM;
                }
                return parsePreference(resultSet.getString("setting_value"));
            }
        } catch (SQLException exception) {
            throw new PersistenceException("Failed to load the default meridiem setting", exception);
        }
    }

    @Override
    public void saveDefaultMeridiem(MeridiemPreference preference) {
        Objects.requireNonNull(preference, "preference must not be null");

        try (var connection = databaseManager.openConnection();
             var statement = connection.prepareStatement(UPSERT)) {
            statement.setString(1, DEFAULT_MERIDIEM_KEY);
            statement.setString(2, preference.name());
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new PersistenceException("Failed to save the default meridiem setting", exception);
        }
    }

    private static MeridiemPreference parsePreference(String storedValue) {
        try {
            return MeridiemPreference.valueOf(storedValue);
        } catch (RuntimeException exception) {
            throw new PersistenceException("Stored default meridiem setting is invalid", exception);
        }
    }
}
