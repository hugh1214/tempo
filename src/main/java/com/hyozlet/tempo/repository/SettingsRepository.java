package com.hyozlet.tempo.repository;

import com.hyozlet.tempo.domain.MeridiemPreference;

public interface SettingsRepository {
    /**
     * Returns PM when no preference has been persisted.
     */
    MeridiemPreference getDefaultMeridiem();

    void saveDefaultMeridiem(MeridiemPreference preference);
}
