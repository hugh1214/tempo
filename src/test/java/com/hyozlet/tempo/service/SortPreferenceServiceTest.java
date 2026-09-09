package com.hyozlet.tempo.service;

import com.hyozlet.tempo.domain.SortContext;
import com.hyozlet.tempo.domain.SortType;
import com.hyozlet.tempo.persistence.DatabaseManager;
import com.hyozlet.tempo.persistence.SQLiteSortPreferenceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SortPreferenceServiceTest {
    @TempDir
    Path tempDirectory;

    private Path databasePath;
    private SortPreferenceService service;

    @BeforeEach
    void setUp() {
        databasePath = tempDirectory.resolve("tempo.db");
        var manager = new DatabaseManager(databasePath);
        manager.initializeSchema();
        service = new SortPreferenceService(new SQLiteSortPreferenceRepository(manager));
    }

    @Test
    void preservesTheUnsetState() {
        assertEquals(Optional.empty(), service.findSortType(SortContext.TODAY_LIST));
    }

    @Test
    void changesOneContextWithoutChangingAnotherAndPersistsBoth() {
        service.setSortType(SortContext.TODAY_LIST, SortType.PRIORITY);
        service.setSortType(SortContext.WEEK_LIST, SortType.TIME);
        service.setSortType(SortContext.TODAY_LIST, SortType.CREATED);

        assertEquals(Optional.of(SortType.CREATED), service.findSortType(SortContext.TODAY_LIST));
        assertEquals(Optional.of(SortType.TIME), service.findSortType(SortContext.WEEK_LIST));

        var reopenedManager = new DatabaseManager(databasePath);
        reopenedManager.initializeSchema();
        var reopened = new SortPreferenceService(new SQLiteSortPreferenceRepository(reopenedManager));
        assertEquals(Optional.of(SortType.CREATED), reopened.findSortType(SortContext.TODAY_LIST));
        assertEquals(Optional.of(SortType.TIME), reopened.findSortType(SortContext.WEEK_LIST));
    }
}
