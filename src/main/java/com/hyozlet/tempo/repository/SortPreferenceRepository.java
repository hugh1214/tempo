package com.hyozlet.tempo.repository;

import com.hyozlet.tempo.domain.SortContext;
import com.hyozlet.tempo.domain.SortType;

import java.util.Optional;

public interface SortPreferenceRepository {
    Optional<SortType> findByContext(SortContext context);

    void save(SortContext context, SortType sortType);
}
