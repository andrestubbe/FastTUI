# FastTUI Reference

This document provides a high-level overview of the available classes, modules, and lifecycle management within the FastTUI architecture.

## 1. Core Architecture

*   **`Component`** (`fasttui.component.Component`)
    The base class for all UI elements. It handles standard properties like position (`x`, `y`), dimensions (`width`, `height`), visibility, active focus state, and lifecycle events.
    *Methods:* `render()`, `onMouseClick()`, `onMouseMove()`, `onKeyPress()`.
*   **`FocusManager`** (`fasttui.FocusManager`)
    Manages the globally focused component to direct keyboard events appropriately. Supports focus cycling via `Tab` / `Shift+Tab`.
*   **`UIEvent`** (`fasttui.UIEvent`)
    A standardized event wrapper that normalizes the native input hooks from `FastTerminal` into easily consumable UI events (Mouse clicks, dragging, keyboard strokes).

## 2. Containers and Windows

*   **`Panel`** (`fasttui.component.Panel`)
    The primary window container. Panels support titles, nested children, absolute positioning, transparency (alpha channel shadow rendering), and drag-to-move capabilities out-of-the-box.

## 3. Composable Widgets

All widgets reside in `fasttui.composable.*`.

*   **`Button`**: Clickable action trigger with active hover states.
*   **`Label`**: Read-only text display supporting True-Color formatting.
*   **`Input`**: A single-line text field allowing keyboard input, cursor movement, and backspacing.
*   **`Checkbox`**: A boolean toggle state control.
*   **`Radio`**: A mutually exclusive selector (typically used in groups).
*   **`Dropdown`**: A collapsible select menu.
*   **`ProgressBar`**: A smooth, horizontally-filling progress indicator supporting gradient color spans.
*   **`FileNavigator`**: A fully functional directory browser providing expandable folder trees and file icons.

## 4. Utilities

*   **`Gradient`** (`fasttui.util.Gradient`)
    Provides static zero-allocation lerping (linear interpolation) across 24-bit RGB space. Supports generating rich horizontal, vertical, or diagonal color blends over terminal character spans.
*   **`ColorPicker`** (`fasttui.component.ColorPicker`)
    An interactive UI widget allowing the user to select HSL-based True Colors via a visual palette.
