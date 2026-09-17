> [!WARNING]
> **🚧 WIP — Active UI Engine Construction & Layout Architecture Optimization in Progress.**

# FastTUI 0.1.3 [ALPHA-2026-07] — Native Windows TUI Framework for Java

[![Status](https://img.shields.io/badge/status-0.1.3-brightgreen.svg)](https://github.com/andrestubbe/FastTUI/releases/tag/0.1.3)
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

Watch Demo (YouTube) | Watch JMH Benchmark (YouTube)

---

## Table of Contents

- [Why FastTUI?](#why-fasttui)
- [Key Features](#key-features)
- [Architecture](#architecture)
- [Available Components](#available-components)
- [Installation](#installation)
- [Documentation](#documentation)
- [Platform Support](#platform-support)
- [License](#license)
- [Related Projects](#related-projects)

---

## Why FastTUI?

Building rich, interactive terminal user interfaces (TUIs) in Java using legacy libraries like Lanterna or Charva feels outdated and clunky:

- **1990s Aesthetics & 16-Color Limits** — Conventional Java TUI toolkits rely on restricted 16/256-color palettes and cannot render modern 24-bit True Color gradients or soft window drop shadows.
- **Clunky or Missing Mouse Interactions** — Standard text interfaces treat mouse input as an afterthought, lacking smooth 1:1 drag-and-drop window resizing and pixel-smooth scrollbars.
- **Tightly Coupled Blocking Rendering** — Mixing component state logic directly with terminal I/O causes UI thread lockups and severe screen tearing during rapid user inputs.
- **High Object Churn in Layout Trees** — Re-evaluating widget layout boxes and borders on terminal resize events instantiates thousands of transient objects on the JVM heap.

FastTUI solves this by decoupling high-level UI component logic (windows, buttons, dropdowns, tables) from terminal rendering. It routes all compositing through `FastTerminal`'s 60+ FPS zero-allocation ANSI blitter.

| Feature | Lanterna 3 | Charva (AWT for Text) | FastTUI |
|:---|:---|:---|:---|
| **Color Fidelity** | 16 / 256 Colors | 16 Colors (ANSI) | **24-bit True Color (RGB Gradients)** |
| **Window Compositing** | Flat character borders | Heavy AWT peer emulation | **Z-Index + Alpha Drop Shadows** |
| **Mouse Interaction** | Coarse click support | Basic text cursor clicks | **Full 1:1 Drag, Resize & Smooth Scroll** |
| **Render Architecture** | Heap cell buffer blit | Simulated AWT repaints | **Decoupled 60+ FPS via FastTerminal** |
| **Unicode / Emoji Safety**| Frequent width corruption | UTF-16 split issues | **Native UTF-32 Codepoint Grids** |
| **Dependencies** | Standalone JAR | Heavy native wrappers | **Pure Java 17+ backed by FastCore** |

---

## Key Features
- **✨ True-Color Aesthetics:** Full 24-bit RGB support for all components. Includes high-performance utilities for smooth color gradients and transitions.
- **🖱️ Native Mouse Interaction:** Drag, drop, resize, and click with full mouse support.
- **🪟 Window Management:** Support for overlapping windows (`Panel`) with alpha-blended drop shadows and z-index ordering.
- **🧩 Composable Widget System:** A rich library of ready-to-use form controls, buttons, and complex widgets.

## Architecture
FastTUI focuses entirely on **Component Logic, State, and Event Dispatching**. 
It delegates the actual cell-buffer drawing and native terminal hooks to its upstream engine, **FastTerminal**. This strict separation of concerns allows FastTUI to remain elegant and modular while inheriting FastTerminal's blistering 120 FPS rendering speeds.

## Available Components
FastTUI provides a comprehensive suite of UI primitives:
- `Panel`: Movable, resizable windows with title bars and drop shadows.
- `Button` & `ComplexButton`: Interactive buttons with hover, focus, press states, and ANSI text styles (`FastStyle`).
- `Label` & `PercentageLabel`: Text displays with rich foreground/background color support.
- `Input` & `MultilineTextBox`: Single-line and multi-line text input fields.
- `Dropdown`: Expandable selection menus.
- `ScrollVertical` & `ScrollHorizontal`: Smooth 1:1 mouse-draggable scrollbars with custom thumb & track behaviors.
- `TreeView` & `Table`: Hierarchical tree navigators and multi-column data tables.

## Installation

### Option 1: Maven (Recommended)

Add the JitPack repository and the dependency to your `pom.xml`:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
<dependencies>
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>FastTUI</artifactId>
        <version>0.1.2</version>
    </dependency>
    <!-- Required for rendering -->
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>FastTerminal</artifactId>
        <version>0.1.7</version>
    </dependency>
</dependencies>
```

### Option 2: Gradle (via JitPack)

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.andrestubbe:FastTUI:0.1.2'
    // Required for rendering
    implementation 'com.github.andrestubbe:FastTerminal:0.1.7'
}
```

### Option 3: Direct Download (No Build Tool)

Download the latest JAR directly to add it to your classpath:

1. 📥 **[FastTUI-0.1.2.jar](https://github.com/andrestubbe/FastTUI/releases/download/0.1.2/FastTUI-0.1.2.jar)** (The UI Toolkit)
2. 📥 **[FastTerminal-0.1.7.jar](https://github.com/andrestubbe/FastTerminal/releases/download/0.1.7/FastTerminal-0.1.7.jar)** (Required rendering engine)

---

## Documentation

* **[COMPILE.md](docs/COMPILE.md)**: Full compilation guide (Maven Build Setup).
* **[REFERENCE.md](docs/REFERENCE.md)**: Exhaustive catalog of UI widgets and layout managers.
* **[PHILOSOPHY.md](docs/PHILOSOPHY.md)**: Design principles for beautiful terminal interfaces.
* **[ROADMAP.md](docs/ROADMAP.md)**: Planned milestone features and new components.
* **[CHANGELOG.md](docs/CHANGELOG.md)**: Release history and updates.

---

## Platform Support

| Platform      | Status            |
|---------------|-------------------|
| Windows 10/11 | ✅ Fully Supported |
| Linux         | ⏳ Planned |
| macOS         | ⏳ Planned |

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
