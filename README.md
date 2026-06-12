# FastTUI 🎨

**A blazing fast, dependency-free Text-based User Interface (TUI) toolkit for Java.**

[![Build](https://img.shields.io/github/actions/workflow/status/andrestubbe/FastTUI/maven.yml?branch=main)](https://github.com/andrestubbe/FastTUI/actions)
[![Java](https://img.shields.io/badge/Java-17+-blue.svg)](https://www.java.com)
[![Platform](https://img.shields.io/badge/Platform-Windows%2010+-lightgrey.svg)]()
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

FastTUI is a modern UI toolkit designed to bring beautiful, responsive, and highly interactive graphical interfaces directly to the terminal. Built on top of the ultra-low-latency `FastTerminal` engine, it completely discards the blocky aesthetics of the 90s and embraces modern design principles like True-Color gradients, smooth window shadows, and mouse-driven interactions.

```java
// Quick Start — Example
import fasttui.component.Panel;
import fasttui.composable.Button;
import fasttui.composable.Label;

public class DemoApp {
    public static void main(String[] args) {
        Panel window = new Panel(10, 5, 40, 15, "Welcome to FastTUI");
        
        Label text = new Label(2, 2, "This is a True-Color UI!");
        Button btn = new Button(2, 4, 15, 3, "Click Me");
        
        window.addChild(text);
        window.addChild(btn);
        
        // Render via FastTerminal
    }
}
```

## Table of Contents
- [Key Features](#key-features)
- [Architecture](#architecture)
- [Available Components](#available-components)
- [Installation](#installation)
- [Documentation](#documentation)

## Key Features
- **✨ True-Color Aesthetics:** Full 24-bit RGB support for all components. Includes high-performance utilities for smooth color gradients and transitions.
- **🖱️ Native Mouse Interaction:** Drag, drop, resize, and click with full mouse support.
- **🪟 Window Management:** Support for overlapping windows (`Panel`) with alpha-blended drop shadows and z-index ordering.
- **🧩 Composable Widget System:** A rich library of ready-to-use form controls, buttons, and complex widgets.
- **⚡ Zero Dependencies:** FastTUI relies entirely on the Java Standard Library and `FastTerminal` for rendering. No heavy third-party UI abstractions.

## Architecture
FastTUI focuses entirely on **Component Logic, State, and Event Dispatching**. 
It delegates the actual cell-buffer drawing and native terminal hooks to its upstream engine, **FastTerminal**. This strict separation of concerns allows FastTUI to remain elegant and modular while inheriting FastTerminal's blistering 120 FPS rendering speeds.

## Available Components
FastTUI provides a comprehensive suite of UI primitives:
- `Panel`: Movable, resizable windows with title bars and drop shadows.
- `Button`: Interactive buttons with hover and active states.
- `Label`: Text displays with rich foreground/background color support.
- `Input`: Single-line text input fields.
- `Checkbox` & `Radio`: State-toggling controls.
- `Dropdown`: Expandable selection menus.
- `ProgressBar`: Smooth, gradient-filled progress tracking.
- `FileNavigator`: A complex widget for browsing the local filesystem.

## Installation
Add FastTUI to your project's `pom.xml`:

```xml
<dependency>
    <groupId>com.github.andrestubbe</groupId>
    <artifactId>FastTUI</artifactId>
    <version>0.1.0</version>
</dependency>
```
*(Requires FastTerminal as a peer dependency)*

## Documentation
- [Philosophy](docs/PHILOSOPHY.md) - The design principles behind FastTUI.
- [Reference](docs/REFERENCE.md) - Detailed API references for all components.
- [Roadmap](docs/ROADMAP.md) - Upcoming features and plans.
