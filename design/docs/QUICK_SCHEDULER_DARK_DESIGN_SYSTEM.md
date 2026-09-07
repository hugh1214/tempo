# Quick Scheduler — macOS-inspired Dark UI Design System v1.0

## 0. Source of truth

Use `quick_scheduler_apple_ui_kit_dark.svg` as the visual source of truth for dark mode. It is an implementation reference, not an image asset to place behind the app.

The Dark kit intentionally preserves the Light kit's geometry and component metrics. Theme switching should change semantic colors/materials, not layout, typography scale, row height, sidebar width, or calendar density.

## 1. Dark mode principles

1. Do not use pure black as the main content surface. Build depth with near-black graphite layers.
2. Keep ordinary content mostly solid. Translucent/material treatment belongs to sidebar, integrated toolbar, menus, Quick Create, toast, and other floating controls.
3. Use blue accent selectively for current selection and primary actions.
4. Use separators instead of large card outlines.
5. Text contrast must remain hierarchical: primary, secondary, tertiary. Do not make every label white.
6. Hover and selected states use subtle white/blue overlays rather than brighter opaque cards.
7. Light and Dark modes must keep identical component dimensions.

## 2. Semantic Dark tokens

| Token | Value | Use |
|---|---|---|
| `dark.bg.canvas` | `#101114` | design board / outer app background reference |
| `dark.bg.window` | `#17181B` | main window shell |
| `dark.bg.content` | `#1C1D20` | primary content surface |
| `dark.bg.sidebar` | `#181A1D` | sidebar base |
| `dark.bg.field` | `#25272B` | text/search fields |
| `dark.bg.elevated` | `#23252A` | elevated/floating material |
| `dark.text.primary` | `#F5F5F7` | title/body primary |
| `dark.text.secondary` | `#C6C6CC` | secondary text |
| `dark.text.tertiary` | `#85858D` | metadata/disabled |
| `dark.separator` | `#36383E` | structural separators |
| `dark.separator.elevated` | `#3A3C42` | floating panel border |
| `dark.accent` | `#0A84FF` | primary action / today / selection |
| `dark.accent.hover` | `#409CFF` | accent hover |
| `dark.selection` | `#0A84FF33` | selected content surface |
| `dark.selection.strong` | `#0A84FF52` | stronger selection state |
| `dark.danger` | `#FF453A` | destructive / high priority |
| `dark.warning` | `#FF9F0A` | medium priority |
| `dark.success` | `#30D158` | success |
| `dark.purple` | `#BF5AF2` | event accent |

### Interaction overlays

- Neutral hover: white 4–5%
- Neutral pressed: white 8–10%
- Sidebar selected: white ~7%
- Selected calendar cell: accent at ~20% alpha
- Focused input: accent stroke, no glow-heavy effect

## 3. Metrics — identical to Light mode

- Sidebar width: **220px**
- Integrated toolbar height: **52px**
- Sidebar item height: **30px**
- Schedule row height: **44px**
- Default glyph: **16px**
- Icon button: **30–32px**
- Main content inset: **24–28px**
- Schedule title: **13px / medium**
- Metadata: **10.5–11.5px**
- Page title: **22px / semibold**
- Standard control radius: **8px**
- Popover radius: **12px**
- Quick Create radius: **14–18px**

## 4. Layer model

Use this hierarchy from back to front:

```text
Window background         #17181B
  ├─ Content              #1C1D20
  ├─ Sidebar material     #181A1D / translucent variant
  ├─ Toolbar material     #23252A / translucent variant
  └─ Floating surface     #23252A / #25272B
       ├─ Menu
       ├─ Toast
       └─ Quick Create
```

Do not give ScheduleRow or CalendarCell their own elevated card shadow.

## 5. Component state mapping

### SidebarItem
- default: transparent
- hover: white overlay 4–5%
- selected: white overlay ~7%, primary text
- keyboard focus: selected/hover surface + subtle accent focus ring

### IconButton
- default: transparent
- hover: white overlay
- pressed: stronger white overlay
- accent: `#0A84FF`, white glyph

### ScheduleRow
- default: content background
- hover: white overlay ~3–4%
- selected: accent alpha surface
- completed: tertiary title/time; completion control uses accent
- separator: `#303238` or semantic separator token

### CalendarCell
- default: transparent/solid content
- hover: subtle white overlay
- selected: `#0A84FF33`
- today: blue circular date marker
- outside month: tertiary text

### Fields
- base: `#25272B`
- border: `#36383E`
- focus: accent border
- placeholder: tertiary

### Floating surfaces
- dark elevated material, thin elevated separator, stronger shadow than content
- never use the same shadow/material on normal content rows

## 6. SVG structure for Codex

The SVG contains named top-level groups:

- `section-foundations`
- `section-navigation`
- `section-controls`
- `section-schedule-components`
- `section-calendar-system`
- `section-floating-surfaces`
- `section-composed-screens`
  - `screen-today`
  - `screen-week`
  - `screen-month`

Icons also use `icon-*` IDs. Codex may inspect these groups to understand coordinates and component relationships.

## 7. JavaFX implementation

Recommended theme architecture:

```text
resources/css/
  tokens-base.css
  tokens-light.css
  tokens-dark.css
  typography.css
  controls.css
  sidebar.css
  schedule.css
  calendar.css
  floating.css
  app.css
```

Theme switching should replace a root style class, e.g. `.theme-light` / `.theme-dark`, or swap only the token stylesheet. Do not duplicate screen FXML for dark mode.

Use real JavaFX nodes/controls. Never embed the full UI Kit SVG as the application UI.

## 8. Fidelity rule

When Dark implementation conflicts with convenience, preserve in this order:

1. existing scheduler behavior
2. component dimensions from Light/Dark UI Kits
3. Dark semantic colors in this file
4. state behavior shown in the SVG
5. developer implementation preference
