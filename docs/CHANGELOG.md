# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).

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
