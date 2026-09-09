package com.hyozlet.tempo.service;

import com.hyozlet.tempo.domain.MeridiemPreference;
import com.hyozlet.tempo.repository.SettingsRepository;

import java.util.Objects;

public final class SettingsService {
    private final SettingsRepository settingsRepository;

    public SettingsService(SettingsRepository settingsRepository) {
        this.settingsRepository = Objects.requireNonNull(
                settingsRepository,
                "settingsRepository must not be null"
        );
    }

    public MeridiemPreference getDefaultMeridiem() {
        return settingsRepository.getDefaultMeridiem();
    }

    public void setDefaultMeridiem(MeridiemPreference preference) {
        settingsRepository.saveDefaultMeridiem(
                Objects.requireNonNull(preference, "preference must not be null")
        );
    }
}
