# Tempo Development Rules

## Source of Truth Priority

1. docs/PRODUCT_SPEC.md
2. User's explicit instructions
3. design/docs/*
4. design/light/* and design/dark/*
5. Existing implementation

## Product vs Design

Product specification defines WHAT exists and WHERE it appears.

Design Kit defines HOW reusable UI components look.

Do not copy the example screen layouts from the Design Kit when they
conflict with PRODUCT_SPEC.md.

## Scope

Do not add features that are not explicitly specified.

Do not redesign screens without explicit instruction.

## UI

Reuse components based on the provided Design Kit.

Do not expose default JavaFX visual styling when a Tempo component exists.

Do not render entire UI Kit SVG files as application screens.

SVG UI Kits are design references.

SVG files under src/main/resources/icons are runtime assets.

## Architecture

UI components must not execute SQL.

Controllers must not contain persistence logic.

Repository access must be separated from presentation code.

## Development Process

Work one phase at a time.

After each phase:

- build
- run tests
- summarize changed files
- stop before the next phase unless explicitly instructed