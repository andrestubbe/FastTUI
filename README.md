# FastTUI 0.1.0 [ALPHA] — Native Windows TUI Framework for Java

[![Status](https://img.shields.io/badge/status-0.1.0-brightgreen.svg)](https://github.com/andrestubbe/FastTUI/releases/tag/0.1.0)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-17+-blue.svg)](https://www.java.com)
[![Platform](https://img.shields.io/badge/Platform-Windows%2010+-lightgrey.svg)]()
[![JitPack](https://img.shields.io/badge/JitPack-ready-green.svg)](https://jitpack.io/#andrestubbe/FastTUI)

**⚡ A blazing fast, dependency-free Text-based User Interface (TUI) toolkit for Java, designed to bring beautiful, responsive, and highly interactive graphical interfaces directly to the terminal.**

FastTUI is the high-level interactive user interface layer of the **FastJava** ecosystem. It discards the blocky aesthetics of the 90s and embraces modern design principles like True-Color gradients, smooth window shadows, and mouse-driven interactions.

To achieve a completely responsive, zero-latency desktop terminal experience, FastTUI is designed to pair natively with the underlying rendering and parsing modules of the **FastJava** ecosystem:

* ⚡ **[FastTerminal](https://github.com/andrestubbe/FastTerminal)** — Direct, low-latency, hardware-accelerated 24-bit True Color terminal rendering engine.
* ⚡ **[FastANSI](https://github.com/andrestubbe/FastANSI)** — Relies on FastANSI for byte-native escape sequence scanning.
* ⚡ **[FastASCII](https://github.com/andrestubbe/FastASCII)** — High-performance, zero-allocation byte processing library.

---

## 🎨 Key Features
- **✨ True-Color Aesthetics:** Full 24-bit RGB support for all components. Includes high-performance utilities for smooth color gradients and transitions.
- **🖱️ Native Mouse Interaction:** Drag, drop, resize, and click with full mouse support.
- **🪟 Window Management:** Support for overlapping windows (`Panel`) with alpha-blended drop shadows and z-index ordering.
- **🧩 Composable Widget System:** A rich library of ready-to-use form controls, buttons, and complex widgets.

## 🏗️ Architecture
FastTUI focuses entirely on **Component Logic, State, and Event Dispatching**. 
It delegates the actual cell-buffer drawing and native terminal hooks to its upstream engine, **FastTerminal**. This strict separation of concerns allows FastTUI to remain elegant and modular while inheriting FastTerminal's blistering 120 FPS rendering speeds.

## 📦 Available Components
FastTUI provides a comprehensive suite of UI primitives:
- `Panel`: Movable, resizable windows with title bars and drop shadows.
- `Button`: Interactive buttons with hover and active states.
- `Label`: Text displays with rich foreground/background color support.
- `Input`: Single-line text input fields.
- `Checkbox` & `Radio`: State-toggling controls.
- `Dropdown`: Expandable selection menus.
- `ProgressBar`: Smooth, gradient-filled progress tracking.
- `FileNavigator`: A complex widget for browsing the local filesystem.

## 🚀 Installation
Add FastTUI to your project's `pom.xml`:

```xml
<dependency>
    <groupId>com.github.andrestubbe</groupId>
    <artifactId>FastTUI</artifactId>
    <version>0.1.0</version>
</dependency>
```
*(Requires FastTerminal as a peer dependency)*

## 📖 Documentation
- [Philosophy](docs/PHILOSOPHY.md) - The design principles behind FastTUI.
- [Changelog](docs/CHANGELOG.md) - Release history and updates.
- [Reference](docs/REFERENCE.md) - Detailed API references for all components.
- [Roadmap](docs/ROADMAP.md) - Upcoming features and plans.

---

## License

MIT License — See [LICENSE](LICENSE) file for details.

---

## Related Projects

- [FastTerminal](https://github.com/andrestubbe/FastTerminal)
- [FastANSI](https://github.com/andrestubbe/FastANSI)
- [FastASCII](https://github.com/andrestubbe/FastASCII)
- [FastEmojis](https://github.com/andrestubbe/FastEmojis)
- [FastTUI](https://github.com/andrestubbe/FastTUI)
- [FastGrid](https://github.com/andrestubbe/FastGrid)
- [FastProportion](https://github.com/andrestubbe/FastProportion)
- [FastTheme](https://github.com/andrestubbe/FastTheme)
- [FastCore](https://github.com/andrestubbe/FastCore)

---
**Part of the FastJava Ecosystem** — *Making the JVM faster.*
