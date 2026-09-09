package com.hyozlet.tempo.service;

import com.hyozlet.tempo.domain.SortContext;
import com.hyozlet.tempo.domain.SortType;
import com.hyozlet.tempo.repository.SortPreferenceRepository;

import java.util.Objects;
import java.util.Optional;

public final class SortPreferenceService {
    private final SortPreferenceRepository sortPreferenceRepository;

    public SortPreferenceService(SortPreferenceRepository sortPreferenceRepository) {
        this.sortPreferenceRepository = Objects.requireNonNull(
                sortPreferenceRepository,
                "sortPreferenceRepository must not be null"
        );
    }

    public Optional<SortType> findSortType(SortContext context) {
        return sortPreferenceRepository.findByContext(
                Objects.requireNonNull(context, "context must not be null")
        );
    }

    public void setSortType(SortContext context, SortType sortType) {
        sortPreferenceRepository.save(
                Objects.requireNonNull(context, "context must not be null"),
                Objects.requireNonNull(sortType, "sortType must not be null")
        );
    }
}
