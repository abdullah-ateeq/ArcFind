# ArcFind ⌒

**ArcFind** is an intuitive, high-precision offline Android geometry utility designed to calculate missing circle arc parameters—including arc length, radius, and central angle—with support for multiple unit systems, step-by-step mathematical explanations, and dynamic visual diagrams.

---

## 🌟 Key Features

- 📐 **3 Solve Modes**:
  - **Find Arc Length ($s$)** given Radius ($r$) and Central Angle ($\theta$).
  - **Find Radius ($r$)** given Arc Length ($s$) and Central Angle ($\theta$).
  - **Find Central Angle ($\theta$)** given Arc Length ($s$) and Radius ($r$).
- 🔄 **Comprehensive Unit Support**:
  - Metric: Meters ($m$), Centimeters ($cm$), Millimeters ($mm$), Kilometers ($km$).
  - Imperial: Inches ($in$), Feet ($ft$), Yards ($yd$), Miles ($mi$).
  - Supports mixed input/output length units with automatic conversions.
- 📊 **Dynamic Circle & Arc Diagram**:
  - Interactive visual canvas representation of the calculated arc segment, central angle, and radius.
  - Automatic classification into Minor Arc, Major Arc, Semicircle, or Full Circle.
- 📝 **Step-by-Step Calculation Breakdown**:
  - Displays explicit mathematical formulas and intermediate substitution steps.
  - Shows radian conversion, arc percentage, diameter, and full circumference.
- 💾 **History & Management**:
  - Save, review, copy, share, and clear calculation history locally.
- 🎨 **Modern Material 3 Design**:
  - Jetpack Compose UI with adaptive Dark, Light, and System themes.
  - Support for Material You dynamic colors.
- 🔒 **100% Offline & Privacy First**:
  - Requires zero internet permissions. All computations and history remain on device.

---

## 📸 Screenshots & Preview

| Home Screen / Calculator | Calculation Details & Diagram | Settings & Theme |
|:---:|:---:|:---:|
| *(Arc length, radius & angle inputs)* | *(Interactive canvas & step breakdowns)* | *(Custom unit defaults & precision settings)* |

---

## 🛠️ Built With

- **Language**: [Kotlin](https://kotlinlang.org/)
- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material 3)
- **Architecture**: Clean Architecture / MVVM (`ViewModel`, `StateFlow`, `Coroutines`)
- **Navigation**: Jetpack Navigation Compose
- **Precision Math**: `java.math.BigDecimal` & `MathContext` for high accuracy without floating-point precision loss
- **Persistence**: Android `SharedPreferences` with JSON serialization
- **Target SDK**: Android 16 (API Level 37) / Min SDK: Android 8.0 (API Level 26)

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Ladybug (2024.2.1) or newer recommended
- JDK 11 or higher
- Android SDK 37

### Building from Source

1. Clone the repository:
   ```bash
   git clone https://github.com/abdullah-ateeq/ArcFind.git
   cd ArcFind
   ```

2. Open the project in **Android Studio**.

3. Sync Gradle project files and build the app:
   ```bash
   ./gradlew assembleDebug
   ```

4. Run unit test suite:
   ```bash
   ./gradlew test
   ```

---

## 🧮 Mathematical Principles

The calculator relies on fundamental circular geometry relations:

$$s = r \times \theta_{\text{rad}}$$

$$\theta_{\text{rad}} = \theta_{\text{deg}} \times \frac{\pi}{180}$$

Where:
- $s$ = Arc Length
- $r$ = Radius
- $\theta$ = Central Angle in radians

---

## 📄 License

```text
Copyright (2025) ArcFind.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
