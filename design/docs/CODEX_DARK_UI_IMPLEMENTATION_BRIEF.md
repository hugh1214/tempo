# Codex Brief — Quick Scheduler Dark Mode

Implement Dark Mode using `quick_scheduler_apple_ui_kit_dark.svg` and `QUICK_SCHEDULER_DARK_DESIGN_SYSTEM.md`.

## Hard rules

- Do not create a separate Dark layout or duplicate FXML screens.
- Light and Dark modes share all dimensions, spacing, typography, component hierarchy, and behavior.
- Only semantic theme tokens/materials change.
- Do not use pure black for primary content.
- Do not turn every content area into an elevated card.
- Do not add gradients, neon glows, heavy blue borders, or decorative shadows.
- Keep blue accent selective.
- Use translucent/elevated material only for sidebar, toolbar, menus, Quick Create, toast, and other floating controls.
- Schedule rows and calendar cells remain flat and dense.
- The SVG is a reference. Rebuild it with real JavaFX controls, not one embedded SVG/image.

## Required CSS architecture

Prefer:

```text
.root.theme-light { ...light semantic tokens... }
.root.theme-dark  { ...dark semantic tokens... }
```

or separate `tokens-light.css` and `tokens-dark.css` loaded above shared component CSS.

Shared component CSS must consume semantic looked-up colors so a component is not reimplemented twice.

## Required semantic roles

At minimum define:

- `-qs-bg-window`
- `-qs-bg-content`
- `-qs-bg-sidebar`
- `-qs-bg-field`
- `-qs-bg-elevated`
- `-qs-text-primary`
- `-qs-text-secondary`
- `-qs-text-tertiary`
- `-qs-separator`
- `-qs-accent`
- `-qs-selection`
- `-qs-danger`
- `-qs-warning`
- `-qs-success`

## Validation checklist

Before completing Dark Mode verify:

- sidebar remains 220px
- toolbar remains 52px
- schedule rows remain 44px
- body text remains around 13px
- normal content has no card shadow
- floating surfaces are visually above content
- selected date and today state remain distinguishable
- hover remains subtle
- secondary/tertiary text is not promoted to pure white
- completion, priority colors, menus and focus state remain readable
- Light ↔ Dark switching does not alter layout or resize controls

## SVG group map

Inspect these named groups in the SVG:

`section-foundations`, `section-navigation`, `section-controls`, `section-schedule-components`, `section-calendar-system`, `section-floating-surfaces`, `section-composed-screens`, `screen-today`, `screen-week`, `screen-month`.
