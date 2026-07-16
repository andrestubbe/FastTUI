# FastTUI Roadmap 🚀

**Vision:** To provide the most aesthetically pleasing, highly responsive, and robust UI component framework for the terminal without ever sacrificing the 120 FPS performance of the underlying engine.

## ✨ v0.1.0: Initial Release (Current)
*   **Architecture Split:** Successfully decoupled from `FastTerminal` to form an independent UI layer.
*   **Core Components:** `Panel`, `Button`, `Label`, `Checkbox`, `Radio`, `Dropdown`, `ProgressBar`, `Input`.
*   **Window Management:** Resizable/Movable `Panel` with true drop shadows.
*   **Event Routing:** Fully functional `FocusManager` integrating native Keyboard and Mouse events.

## 🔜 v0.2.0: Advanced Layouts & Modals
*   **Flexbox/Grid Layout Managers:** Instead of absolute cell coordinates, introduce `HBox`, `VBox`, and `Grid` classes to auto-align child components dynamically.
*   **Modal Dialogs:** First-class support for blocking popup dialogs (Alerts, Confirmations) that gray out the background panels.
*   **Scrollbars & Clipping:** Implementing `ScrollView` containers with overflow clipping so panels can host arbitrarily large lists or long textarea.

## 🌟 v0.3.0: Theming & Styling Engine
*   **Style Tokens:** Move away from hardcoded hex constants toward a themeable token engine (e.g., `Theme.setPrimaryColor()`, `Theme.toggleDarkMode()`).
*   **Animations:** Smooth interpolation for UI state changes (e.g., buttons expanding when hovered, or windows smoothly sliding into view).
*   **Rich Text Area:** A fully functional multi-line textarea editor component with syntax highlighting support.

## 🚀 v1.0.0: Maturity
*   **Full Accessibility:** Screen-reader support via terminal standard textarea outputs where possible.
*   **Extensive Widget Library:** Datatables, Tree-views, and interactive Charts out of the box.
