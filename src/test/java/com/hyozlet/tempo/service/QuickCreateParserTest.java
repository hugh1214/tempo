package com.hyozlet.tempo.service;

import com.hyozlet.tempo.domain.MeridiemPreference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class QuickCreateParserTest {
    private QuickCreateParser parser;

    @BeforeEach
    void setUp() {
        var clock = Clock.fixed(Instant.parse("2026-09-09T09:00:00Z"), ZoneOffset.UTC);
        parser = new QuickCreateParser(clock);
    }

    @Test
    void parsesTomorrowWithTheDefaultPm() {
        assertParsed(
                "내일 6시 팀 회의",
                MeridiemPreference.PM,
                "팀 회의",
                LocalDate.of(2026, 9, 10),
                LocalTime.of(18, 0)
        );
    }

    @Test
    void parsesTomorrowWithTheDefaultAm() {
        assertParsed(
                "내일 6시 팀 회의",
                MeridiemPreference.AM,
                "팀 회의",
                LocalDate.of(2026, 9, 10),
                LocalTime.of(6, 0)
        );
    }

    @Test
    void explicitMeridiemOverridesTheDefault() {
        assertParsed(
                "오늘 오전 9시 수업",
                MeridiemPreference.PM,
                "수업",
                LocalDate.of(2026, 9, 9),
                LocalTime.of(9, 0)
        );
        assertParsed(
                "내일 오후 6시 회의",
                MeridiemPreference.AM,
                "회의",
                LocalDate.of(2026, 9, 10),
                LocalTime.of(18, 0)
        );
    }

    @Test
    void parsesMinutesWithOrWithoutWhitespace() {
        assertParsed(
                "내일 3시 30분 발표",
                MeridiemPreference.PM,
                "발표",
                LocalDate.of(2026, 9, 10),
                LocalTime.of(15, 30)
        );
        assertParsed(
                "내일 3시30분 발표",
                MeridiemPreference.PM,
                "발표",
                LocalDate.of(2026, 9, 10),
                LocalTime.of(15, 30)
        );
    }

    @Test
    void handlesTwelveAmAndTwelvePm() {
        assertParsed(
                "오늘 오전 12시 자정",
                MeridiemPreference.PM,
                "자정",
                LocalDate.of(2026, 9, 9),
                LocalTime.MIDNIGHT
        );
        assertParsed(
                "오늘 오후 12시 점심",
                MeridiemPreference.AM,
                "점심",
                LocalDate.of(2026, 9, 9),
                LocalTime.NOON
        );
    }

    @Test
    void rejectsMissingOrInvalidInputParts() {
        var invalidInputs = List.of(
                "",
                "   ",
                "6시 회의",
                "내일 회의",
                "내일 6시",
                "내일 3시 30분",
                "내일 14시 회의",
                "내일 0시 회의",
                "내일 3시 70분 발표"
        );

        assertThrows(QuickCreateParseException.class, () -> parser.parse(null, MeridiemPreference.PM));
        invalidInputs.forEach(input -> assertThrows(
                QuickCreateParseException.class,
                () -> parser.parse(input, MeridiemPreference.PM),
                input
        ));
    }

    private void assertParsed(
            String input,
            MeridiemPreference defaultMeridiem,
            String expectedTitle,
            LocalDate expectedDate,
            LocalTime expectedTime
    ) {
        var parsed = parser.parse(input, defaultMeridiem);
        assertEquals(expectedTitle, parsed.title());
        assertEquals(expectedDate, parsed.date());
        assertEquals(expectedTime, parsed.time());
    }
}
