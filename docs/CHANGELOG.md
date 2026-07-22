# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).

## [0.1.2] - 2026-07-22

### Added
- **FastTerminal 0.1.7 Upstream Sync**: Synchronized rendering pipeline to target FastTerminal 0.1.7.
- **Stateful Text Styles**: Extended `Button` widget with `.setStyle(int style)` support for `FastStyle` bitmask styles (Bold, Italic, Underline, Strikethrough).
- **1:1 Vertical Scrollbar Dragging**: Enhanced `ScrollVertical` with precise thumb position tracking (`isClickOnThumb`, `getThumbTopCell`, `handleThumbDrag`) and grab offset relative calculations.
- **Scroll Hover & State Handling**: Added state-resets (`onMouseEnter`, `onMouseExit`) to ensure smooth mouse release dispatches outside component bounds.

## [0.1.1] - 2026-07-19

### Added
- **Scrollbar Components**: Added `ScrollVertical`, `ScrollHorizontal`, `BarVertical`, and `BarHorizontal`.
- **Advanced Controls**: Added `MultilineTextBox`, `ComplexButton`, `TreeView`, `Table`, and `PercentageLabel`.

## [0.1.0] - 2026-06-13

### Added
- **Initial Architecture Extraction:** Extracted pure UI logic (`Panel`, `Button`, `Label`, `FocusManager`, etc.) from `FastTerminal` into the independent `FastTUI` library.
- **Form Controls:** Added `Input`, `Checkbox`, `Radio`, and `Dropdown` widgets.
- **Advanced Widgets:** Added `FileNavigator`, `ColorPicker`, `ProgressBar`.
- **True-Color Support:** Full 24-bit RGB integrations across all components via `Gradient` utility.
- **Event Management:** Centralized `MouseDispatcher` and `FocusManager` for handling native events from the `FastTerminal` engine.

### Removed
- Removed all JNI, AVX2, and Native C++ bindings (these are now exclusively handled by `FastTerminal`).
- Removed `FastTerminalScene` and cell-buffering logic (handled by `FastTerminal`).
