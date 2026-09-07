# Quick Scheduler — macOS-inspired UI Design System v1.0

## 0. Purpose

This file and `quick_scheduler_apple_ui_kit.svg` are the visual source of truth for the JavaFX rebuild.
The goal is not “generic modern JavaFX”. The goal is a compact macOS productivity-app feel with restrained material, high information density, and state-driven controls.

The kit is original work informed by Apple's public macOS design resources and Human Interface Guidelines. Do not embed or redistribute Apple UI Kit source assets, SF Symbols, or SF Pro font files in the application.

## 1. Core principles

1. Content is mostly flat. Do not wrap schedule sections in large cards.
2. Use separators, type hierarchy, and spacing before adding containers.
3. Material/glass is reserved for navigation and floating control surfaces.
4. Keep toolbar actions sparse and symbol-first.
5. Sidebar selection is subtle; do not use saturated blue fills for normal navigation selection.
6. Base macOS information density around 13px body text and 44px schedule rows.
7. Hover reveals secondary actions; avoid showing every control all the time.
8. Use semantic tokens; never scatter hardcoded colors/radii across screen CSS.

## 2. Typography

Preferred runtime font strategy:
- macOS: system UI font
- Windows: Segoe UI Variable / Segoe UI
- Fallback: system sans-serif

Do not ship Apple's SF Pro files inside this cross-platform Java application.

| Token | Size | Weight | Use |
|---|---:|---:|---|
| `type.page-title` | 22px | 650 | Today / Week / Month title |
| `type.window-title` | 14px | 650 | toolbar title |
| `type.section-title` | 13–14px | 650 | schedule/calendar section |
| `type.body` | 13px | 500 | schedule title, sidebar |
| `type.control` | 11–12px | 600 | buttons / segmented control |
| `type.meta` | 10.5–11.5px | 400–500 | date/time metadata |
| `type.caption` | 9.5–10px | 400–500 | keyboard hints / small labels |

## 3. Color tokens — Light

| Token | Value | Meaning |
|---|---|---|
| `bg.canvas` | `#F2F2F5` | design canvas only |
| `bg.window` | `#FBFBFC` | app window |
| `bg.content` | `#FFFFFF` | primary content |
| `bg.sidebar` | `#F4F4F6` | sidebar base |
| `text.primary` | `#1D1D1F` | primary text |
| `text.secondary` | `#6E6E73` | secondary text |
| `text.tertiary` | `#98989D` | metadata / disabled |
| `separator` | `#D8D8DC` | structural separator |
| `accent` | `#007AFF` | selected day / primary action |
| `selection` | `#E8F2FF` | content selection |
| `danger` | `#FF3B30` | destructive / high importance |
| `warning` | `#FF9F0A` | medium importance |
| `success` | `#34C759` | success state |
| `purple` | `#AF52DE` | event category accent |

### Interaction overlay alpha

- Hover: black 3–4%
- Pressed: black 7–8%
- Sidebar selected: black ~5–6%
- Disabled text: tertiary + reduced opacity

## 4. Spacing scale

`4 / 6 / 8 / 12 / 16 / 20 / 24 / 32`

Rules:
- Toolbar inner control gap: 8–12px
- Sidebar icon-to-label: 10px
- Main content horizontal inset: 24–28px
- Section-to-section vertical gap: 28–36px
- Schedule title-to-time gap: 2–4px

## 5. Radius

| Token | Radius | Use |
|---|---:|---|
| `radius.small` | 6px | sidebar item |
| `radius.control` | 8px | icon button / standard button |
| `radius.field` | 9–10px | text/search fields |
| `radius.popover` | 12px | context menu / toast |
| `radius.floating` | 14–18px | Quick Create / app window shell |

Avoid large 20–28px card radii in content areas.

## 6. Motion

| Interaction | Duration |
|---|---:|
| Hover tint | 100ms |
| Selection tint | 120ms |
| Popover enter/exit | 160ms |
| Press | Immediate |

Popover motion: opacity + very small 0.98 → 1.0 scale. Avoid bouncy spring motion in routine controls.

## 7. Window shell

- Integrated title/toolbar region: 52px
- Sidebar width: 220px
- Window corner radius reference: 16px
- Content begins immediately under toolbar separator
- Do not add a second large page header card beneath toolbar

JavaFX note: on Windows, custom window chrome is optional. If used, preserve native resize/minimize/maximize behavior and keyboard accessibility.

## 8. Sidebar

- Width: 220px
- Item height: 30px
- Item side inset: 10px
- Icon: 16px
- Icon-to-label gap: 10px
- Selected background: neutral translucent fill, not blue
- Settings anchored toward bottom after separator

States:
- Default
- Hover
- Selected
- Focused (keyboard; subtle outline/highlight)
- Disabled if ever needed

## 9. Toolbar

- Height: 52px
- Aim for max 3 logical groups
- Leading: title/navigation
- Center: date navigation when needed
- Trailing: sort + add
- Icon button: 30–32px visual container, 16px glyph
- Primary add button may use accent fill
- Avoid text labels when a symbol is obvious

## 10. Schedule row

- Height: 44px
- No outer card
- Checkbox: 14–16px circle
- Title: 13px / medium
- Time: 10.5–11px / secondary or tertiary
- Separator starts after checkbox/title inset, not necessarily full width
- Hover reveals overflow action
- Priority is a small semantic dot or minimal badge, never a large pill by default

States in the SVG:
- default
- hover
- selected
- completed
- priority high/medium/low

## 11. Calendar

### Two-week calendar
- 7 columns × 2 rows
- Thin separators
- Day label 10–12px
- Today: filled blue circular day marker
- Selected: light accent cell surface
- Event markers: tiny dot + compact label

### Monthly calendar
- 7 columns × 6 rows
- Outside-month days: tertiary text
- Do not give every event a large colored pill
- Selected date can coexist with “today” state; if both occur, ensure contrast remains clear

## 12. Quick Create

Signature interaction of the app.

- Floating panel width reference: ~700px in large desktop layout
- Radius: 18px in the kit; 14–18px acceptable depending on final scale
- Use translucent/raised material appearance only here and similar floating surfaces
- Natural-language input is the visual focal point
- Parsed date/time appears beneath input as secondary controls/metadata
- Primary action on trailing edge
- Enter key hint may be shown

## 13. Menus / popovers / toast

- Popover radius: 12px
- Thin border + soft shadow
- Menu rows should remain compact
- Destructive item uses red
- Toast should be compact and temporary
- Avoid placing the same glass effect on ordinary schedule rows or calendar cells

## 14. Icons

Use the original line icons in `icons/` or replace them with another appropriately licensed monochrome icon family.

Visual rules:
- 16×16 default
- Rounded joins/caps
- ~1.5–1.7px stroke
- monochrome unless semantic state requires color
- do not mix multiple unrelated icon styles

The bundled icons are original project assets, not SF Symbols.

## 15. JavaFX mapping

Recommended component layer:

```text
presentation/
  foundation/
    UiTokens.java
    Typography.java
  components/
    QsIconButton.java
    QsSidebarItem.java
    QsScheduleRow.java
    QsCalendarCell.java
    QsSegmentedControl.java
    QsPopover.java
    QsQuickCreatePane.java
  views/
    TodayView.fxml
    WeekView.fxml
    MonthView.fxml
    SettingsView.fxml
resources/
  css/
    tokens.css
    typography.css
    controls.css
    sidebar.css
    schedule.css
    calendar.css
    floating.css
    app.css
  icons/
```

Do not implement the SVG as one image. Rebuild it from real JavaFX layout nodes and controls.

## 16. Source of truth priority

When implementation details conflict, use this priority:

1. Existing Quick Scheduler functional specification
2. `quick_scheduler_apple_ui_kit.svg` for visual composition
3. This file for numeric rules/tokens
4. Screen-specific implementation constraints
5. Developer preference

Codex must not invent a new radius, text scale, or UI pattern simply because it is easier to code.
