# FastTUI Examples 🖼️

This directory contains functional, interactive demonstrations of the FastTUI framework. These demos are the best way to see the capabilities of the toolkit in action.

## Demos & Walkthroughs

### 1. The Core UI Demo (`Demo`)
Located in `examples/Demo`. This is the primary playground demonstrating the native windowing system.
- Spawns multiple interactive `Panel` windows.
- Demonstrates overlapping drop shadows and alpha blending.
- Showcases the `Button`, `Checkbox`, `Input`, and `ProgressBar` widgets.
- Demonstrates the real-time `FocusManager` and native mouse drag/drop capabilities.
**Run it:** `..\run-demo.bat` (from the FastTUI root)

### 2. The Color Palette Explorer (`Palette`)
Located in `examples/Palette`.
- **RunPalette:** A beautiful interactive Color Picker demonstrating the `ColorPicker` widget, Hue/Saturation/Lightness sliders, and True-Color feedback.
- **RunGradient:** A pure visual demonstration of the `Gradient` utility, showcasing mathematically perfect RGB linear interpolations over the terminal grid.
**Run them:** `..\run-palette.bat` or `..\run-gradient.bat`

### 3. Microbenchmarks (`Benchmark`)
A suite of JMH microbenchmarks measuring the throughput of UI component rendering and event dispatching.
**Run it:** `..\run-benchmark.bat`
