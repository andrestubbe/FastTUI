# The Philosophy of FastTUI 🎨

> [!IMPORTANT]
> **"Beautiful. Modular. Composable. Uncompromising Aesthetics."**

FastTUI is built on the belief that command-line interfaces do not have to look like they were built in the 1980s. The terminal is a powerful canvas, and FastTUI exists to bring modern UI design principles to it.

## 1. Separation of Engine and Interface
FastTUI is purely a **User Interface Toolkit**. It does not concern itself with how ANSI codes are parsed, how raw memory is mapped to terminal buffers, or how Windows JNI hooks operate. All of the heavy lifting, raw input hooking, and 120 FPS rendering is offloaded to the **FastTerminal** engine. FastTUI's sole responsibility is routing UI events, calculating layouts, and managing state.

## 2. Uncompromising Aesthetics
A terminal application should feel like a premium native application.
- We reject 16-color constraints; every component uses True-Color 24-bit RGB.
- We embrace micro-interactions: buttons glow on hover, dropdowns slide open, and progress bars smoothly interpolate their colors.
- We support alpha-blending and overlapping windows with deep shadows (`Panel`).

## 3. Modular & Composable Architecture
Everything in FastTUI extends from the base `Component` class.
- **Hierarchy:** Components can nest inside containers (`Panel`), forming a strict tree structure.
- **Absolute vs Layout:** While absolute positioning (`x`, `y`) is supported, complex interfaces should compose elements hierarchically.
- **Event Bubbling:** Mouse clicks, drag events, and keystrokes are cleanly routed through a central `FocusManager` and bubbled down to the active component.

## 4. Zero Dependencies
Despite its rich feature set, FastTUI requires absolutely zero third-party libraries (other than its sister project, `FastTerminal`). We rely exclusively on the Java Standard Library. There are no bloated dependency graphs, no Spring injection, and no heavy logging frameworks.
