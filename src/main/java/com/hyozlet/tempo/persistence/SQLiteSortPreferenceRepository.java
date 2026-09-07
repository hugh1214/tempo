package com.hyozlet.tempo.persistence;

import com.hyozlet.tempo.domain.SortContext;
import com.hyozlet.tempo.domain.SortType;
import com.hyozlet.tempo.repository.SortPreferenceRepository;

import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

public final class SQLiteSortPreferenceRepository implements SortPreferenceRepository {
    private static final String SELECT = """
            SELECT sort_type
            FROM sort_preferences
            WHERE context = ?
            """;
    private static final String UPSERT = """
            INSERT INTO sort_preferences (context, sort_type)
            VALUES (?, ?)
            ON CONFLICT(context) DO UPDATE SET sort_type = excluded.sort_type
            """;

    private final DatabaseManager databaseManager;

    public SQLiteSortPreferenceRepository(DatabaseManager databaseManager) {
        this.databaseManager = Objects.requireNonNull(databaseManager, "databaseManager must not be null");
    }

    @Override
    public Optional<SortType> findByContext(SortContext context) {
        Objects.requireNonNull(context, "context must not be null");

        try (var connection = databaseManager.openConnection();
             var statement = connection.prepareStatement(SELECT)) {
            statement.setString(1, context.name());
            try (var resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }
                return Optional.of(parseSortType(resultSet.getString("sort_type")));
            }
        } catch (SQLException exception) {
            throw new PersistenceException("Failed to load sort preference", exception);
        }
    }

    @Override
    public void save(SortContext context, SortType sortType) {
        Objects.requireNonNull(context, "context must not be null");
        Objects.requireNonNull(sortType, "sortType must not be null");

        try (var connection = databaseManager.openConnection();
             var statement = connection.prepareStatement(UPSERT)) {
            statement.setString(1, context.name());
            statement.setString(2, sortType.name());
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new PersistenceException("Failed to save sort preference", exception);
        }
    }

    private static SortType parseSortType(String storedValue) {
        try {
            return SortType.valueOf(storedValue);
        } catch (RuntimeException exception) {
            throw new PersistenceException("Stored sort preference is invalid", exception);
        }
    }
}
