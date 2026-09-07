package com.hyozlet.tempo.persistence;

import com.hyozlet.tempo.domain.SortContext;
import com.hyozlet.tempo.domain.SortType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SQLiteSortPreferenceRepositoryTest {
    @TempDir
    Path tempDirectory;

    @Test
    void persistsEachContextIndependentlyAcrossRepositoryInstances() {
        var databasePath = tempDirectory.resolve("tempo.db");
        var databaseManager = new DatabaseManager(databasePath);
        databaseManager.initializeSchema();
        var repository = new SQLiteSortPreferenceRepository(databaseManager);

        assertTrue(repository.findByContext(SortContext.TODAY_LIST).isEmpty());
        repository.save(SortContext.TODAY_LIST, SortType.PRIORITY);
        repository.save(SortContext.WEEK_LIST, SortType.TIME);

        repository.save(SortContext.TODAY_LIST, SortType.CREATED);
        assertEquals(SortType.CREATED, repository.findByContext(SortContext.TODAY_LIST).orElseThrow());
        assertEquals(SortType.TIME, repository.findByContext(SortContext.WEEK_LIST).orElseThrow());

        var reopenedManager = new DatabaseManager(databasePath);
        reopenedManager.initializeSchema();
        var reopenedRepository = new SQLiteSortPreferenceRepository(reopenedManager);
        assertEquals(SortType.CREATED,
                reopenedRepository.findByContext(SortContext.TODAY_LIST).orElseThrow());
        assertEquals(SortType.TIME,
                reopenedRepository.findByContext(SortContext.WEEK_LIST).orElseThrow());
    }
}
