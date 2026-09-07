# Codex UI Implementation Brief — Quick Scheduler

Implement the existing scheduler behavior using the supplied macOS-inspired design system.

## Non-negotiable

- `quick_scheduler_apple_ui_kit.svg` is a design reference, NOT an image to embed as the app UI.
- Build real JavaFX controls/layouts so text, dates, schedule count, hover, selection, completion and sorting remain dynamic.
- Preserve existing functional behavior and screen structure.
- Do not redesign the app while implementing it.
- Do not introduce large content cards.
- Do not enlarge typography or row height “for readability” unless explicitly requested.
- Do not add decorative gradients, glass cards, shadows, category pills, or accent colors beyond the design system.
- Material/translucency is for sidebar/toolbar/floating surfaces, not ordinary content.
- Use the bundled original SVG icons or a single coherent licensed icon set. Do not mix styles.

## Build order

1. Tokens / typography / spacing
2. Window shell + sidebar + toolbar
3. IconButton, standard buttons, fields, checkbox, segmented control
4. ScheduleRow
5. CalendarCell and calendar grids
6. Quick Create panel and menus
7. Today screen
8. Week screen
9. Month screen
10. Settings screen
11. interaction polish and keyboard focus

## Fidelity checks

Before calling a screen complete, compare against the SVG for:
- sidebar width
- toolbar height
- typography scale
- row height
- icon size
- section spacing
- separator placement
- selected/hover state intensity
- calendar density
- use of shadows/materials

If a JavaFX limitation prevents direct reproduction, preserve the visual hierarchy and measurements first; use the nearest technically stable effect rather than inventing a new visual style.
