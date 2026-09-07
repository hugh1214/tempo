package com.hyozlet.tempo.persistence;

import com.hyozlet.tempo.domain.Priority;
import com.hyozlet.tempo.domain.Schedule;
import com.hyozlet.tempo.repository.ScheduleRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class SQLiteScheduleRepository implements ScheduleRepository {
    private static final String INSERT = """
            INSERT INTO schedules (
                title, schedule_date, schedule_time, priority, completed, created_at
            ) VALUES (?, ?, ?, ?, ?, ?)
            """;
    private static final String SELECT_BY_ID = """
            SELECT id, title, schedule_date, schedule_time, priority, completed, created_at
            FROM schedules
            WHERE id = ?
            """;
    private static final String SELECT_BY_DATE = """
            SELECT id, title, schedule_date, schedule_time, priority, completed, created_at
            FROM schedules
            WHERE schedule_date = ?
            """;
    private static final String SELECT_BETWEEN = """
            SELECT id, title, schedule_date, schedule_time, priority, completed, created_at
            FROM schedules
            WHERE schedule_date >= ? AND schedule_date <= ?
            """;
    private static final String UPDATE = """
            UPDATE schedules
            SET title = ?, schedule_date = ?, schedule_time = ?, priority = ?, completed = ?, created_at = ?
            WHERE id = ?
            """;

    private final DatabaseManager databaseManager;

    public SQLiteScheduleRepository(DatabaseManager databaseManager) {
        this.databaseManager = Objects.requireNonNull(databaseManager, "databaseManager must not be null");
    }

    @Override
    public Schedule save(Schedule schedule) {
        Objects.requireNonNull(schedule, "schedule must not be null");
        if (schedule.id() != null) {
            throw new IllegalArgumentException("A new schedule must not already have an ID");
        }

        try (var connection = databaseManager.openConnection();
             var statement = connection.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            bindSchedule(statement, schedule);
            if (statement.executeUpdate() != 1) {
                throw new PersistenceException("Expected one schedule to be inserted");
            }

            try (var generatedKeys = statement.getGeneratedKeys()) {
                if (!generatedKeys.next()) {
                    throw new PersistenceException("SQLite did not return a generated schedule ID");
                }
                return schedule.withId(generatedKeys.getLong(1));
            }
        } catch (SQLException exception) {
            throw new PersistenceException("Failed to save schedule", exception);
        }
    }

    @Override
    public Optional<Schedule> findById(long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("id must be positive");
        }

        try (var connection = databaseManager.openConnection();
             var statement = connection.prepareStatement(SELECT_BY_ID)) {
            statement.setLong(1, id);
            try (var resultSet = statement.executeQuery()) {
                return resultSet.next() ? Optional.of(readSchedule(resultSet)) : Optional.empty();
            }
        } catch (SQLException exception) {
            throw new PersistenceException("Failed to find schedule by ID", exception);
        }
    }

    @Override
    public List<Schedule> findByDate(LocalDate date) {
        Objects.requireNonNull(date, "date must not be null");

        try (var connection = databaseManager.openConnection();
             var statement = connection.prepareStatement(SELECT_BY_DATE)) {
            statement.setString(1, date.toString());
            return readSchedules(statement.executeQuery());
        } catch (SQLException exception) {
            throw new PersistenceException("Failed to find schedules by date", exception);
        }
    }

    @Override
    public List<Schedule> findBetween(LocalDate startInclusive, LocalDate endInclusive) {
        Objects.requireNonNull(startInclusive, "startInclusive must not be null");
        Objects.requireNonNull(endInclusive, "endInclusive must not be null");
        if (startInclusive.isAfter(endInclusive)) {
            throw new IllegalArgumentException("startInclusive must not be after endInclusive");
        }

        try (var connection = databaseManager.openConnection();
             var statement = connection.prepareStatement(SELECT_BETWEEN)) {
            statement.setString(1, startInclusive.toString());
            statement.setString(2, endInclusive.toString());
            return readSchedules(statement.executeQuery());
        } catch (SQLException exception) {
            throw new PersistenceException("Failed to find schedules by date range", exception);
        }
    }

    @Override
    public Schedule update(Schedule schedule) {
        Objects.requireNonNull(schedule, "schedule must not be null");
        if (schedule.id() == null) {
            throw new IllegalArgumentException("An existing schedule must have an ID");
        }

        try (var connection = databaseManager.openConnection();
             var statement = connection.prepareStatement(UPDATE)) {
            bindSchedule(statement, schedule);
            statement.setLong(7, schedule.id());
            if (statement.executeUpdate() != 1) {
                throw new PersistenceException("No schedule exists with ID " + schedule.id());
            }
            return schedule;
        } catch (SQLException exception) {
            throw new PersistenceException("Failed to update schedule", exception);
        }
    }

    private static void bindSchedule(java.sql.PreparedStatement statement, Schedule schedule) throws SQLException {
        statement.setString(1, schedule.title());
        statement.setString(2, schedule.date().toString());
        statement.setString(3, schedule.time().toString());
        statement.setString(4, schedule.priority().name());
        statement.setInt(5, schedule.completed() ? 1 : 0);
        statement.setString(6, schedule.createdAt().toString());
    }

    private static List<Schedule> readSchedules(ResultSet resultSet) throws SQLException {
        try (resultSet) {
            var schedules = new ArrayList<Schedule>();
            while (resultSet.next()) {
                schedules.add(readSchedule(resultSet));
            }
            return List.copyOf(schedules);
        }
    }

    private static Schedule readSchedule(ResultSet resultSet) throws SQLException {
        try {
            return new Schedule(
                    resultSet.getLong("id"),
                    resultSet.getString("title"),
                    LocalDate.parse(resultSet.getString("schedule_date")),
                    LocalTime.parse(resultSet.getString("schedule_time")),
                    Priority.valueOf(resultSet.getString("priority")),
                    resultSet.getInt("completed") == 1,
                    Instant.parse(resultSet.getString("created_at"))
            );
        } catch (RuntimeException exception) {
            throw new PersistenceException("Stored schedule data is invalid", exception);
        }
    }
}
