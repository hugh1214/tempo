package com.hyozlet.tempo.service;

import com.hyozlet.tempo.domain.MeridiemPreference;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;
import java.util.regex.Pattern;

public final class QuickCreateParser {
    private static final Pattern INPUT_PATTERN = Pattern.compile(
            "^(오늘|내일)\\s+(?:(오전|오후)\\s+)?(\\d{1,2})시(?:\\s*(\\d{1,2})분)?+\\s+(.+?)\\s*$"
    );

    private final Clock clock;

    public QuickCreateParser(Clock clock) {
        this.clock = Objects.requireNonNull(clock, "clock must not be null");
    }

    public ParsedQuickCreate parse(String input, MeridiemPreference defaultMeridiem) {
        if (input == null || input.isBlank()) {
            throw new QuickCreateParseException("Quick Create input must not be blank");
        }
        Objects.requireNonNull(defaultMeridiem, "defaultMeridiem must not be null");

        var matcher = INPUT_PATTERN.matcher(input.strip());
        if (!matcher.matches()) {
            throw new QuickCreateParseException("Input must contain a supported date, time, and title");
        }

        var date = parseDate(matcher.group(1));
        var meridiem = parseMeridiem(matcher.group(2), defaultMeridiem);
        var hour = parseNumber(matcher.group(3), "hour");
        var minute = matcher.group(4) == null ? 0 : parseNumber(matcher.group(4), "minute");
        validateTime(hour, minute);

        var title = matcher.group(5).strip();
        if (title.isBlank()) {
            throw new QuickCreateParseException("Schedule title must not be blank");
        }

        return new ParsedQuickCreate(title, date, toTime(hour, minute, meridiem));
    }

    private LocalDate parseDate(String dateText) {
        var today = LocalDate.now(clock);
        return switch (dateText) {
            case "오늘" -> today;
            case "내일" -> today.plusDays(1);
            default -> throw new QuickCreateParseException("Unsupported date: " + dateText);
        };
    }

    private static MeridiemPreference parseMeridiem(
            String meridiemText,
            MeridiemPreference defaultMeridiem
    ) {
        if (meridiemText == null) {
            return defaultMeridiem;
        }
        return switch (meridiemText) {
            case "오전" -> MeridiemPreference.AM;
            case "오후" -> MeridiemPreference.PM;
            default -> throw new QuickCreateParseException("Unsupported meridiem: " + meridiemText);
        };
    }

    private static int parseNumber(String value, String fieldName) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            throw new QuickCreateParseException("Invalid " + fieldName);
        }
    }

    private static void validateTime(int hour, int minute) {
        if (hour < 1 || hour > 12) {
            throw new QuickCreateParseException("Hour must be between 1 and 12");
        }
        if (minute < 0 || minute > 59) {
            throw new QuickCreateParseException("Minute must be between 0 and 59");
        }
    }

    private static LocalTime toTime(int hour, int minute, MeridiemPreference meridiem) {
        var hourOfDay = hour % 12;
        if (meridiem == MeridiemPreference.PM) {
            hourOfDay += 12;
        }
        return LocalTime.of(hourOfDay, minute);
    }
}
