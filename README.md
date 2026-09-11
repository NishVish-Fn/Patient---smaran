# Smaran — Assistive Sentinel & Cognitive Care Platform (Patient Tracker)

<p align="center">
  <img src="app/src/main/res/drawable/smaran_logo.png" alt="Smaran Logo" width="120" />
</p>

A state-of-the-art Android assistive application engineered with **Kotlin**, **Jetpack Compose**, and **OpenStreetMap (OSMDroid)**. Smaran pairs an ultra-reliable **Zero-Cloud GPS Sentinel & Safety Beacon** with an autonomous, on-device **Clinical Cognitive Health & Brain Exercise Engine**.

Tailored specifically for elder care, early memory support, and Alzheimer's/dementia assistance, Smaran combines an ergonomic **Apple iOS Assistive Access** senior-friendly interface with a soothing dark-whitish aesthetic, high-contrast typography, and artistic motifs inspired by Northeast India (Assam, Meghalaya, Manipur, Mizoram, Nagaland).

---

## 📦 Pre-Built Release APK (Tracker Mod)

The pre-built, production-ready Tracker APK is included directly within this repository:

* **Download Tracker APK**: [release/Smaran-Tracker-v1.0.apk](release/Smaran-Tracker-v1.0.apk)
* **Application Role**: Patient Device (Tracker & Cognitive Companion)
* **Package Name**: `net.kibotu.geofencerelay.tracker`
* **Version**: 1.0.0
* **Target Android Compatibility**: Android 8.0 (API 26) through Android 14 (API 34)

---

## 🌟 Core Features & Architecture

### 1. 🧠 Autonomous On-Device AI Cognitive Engine (CPS Engine)
- **Mathematical ML Model Port**: Emulates an ensemble XGBoost and Random Forest clinical scoring model in pure Kotlin (`CpsEngine.kt`), operating in $<1\text{ms}$ on-device with zero cloud latency.
- **Autonomous Difficulty Tuning**: The AI engine dynamically selects and scales game challenge across **Easy**, **Medium**, and **Hard** in real time based on player reaction latency, accuracy percentage, and error-recovery patterns.
- **Objective Clinical Telemetry**:
  - Memory Retention Index (0–100%)
  - Executive Function Index (0–100%)
  - Reaction Latency & Speed Score
  - Autobiographical Reminiscence Index
  - Error Recovery & Adaptive Resilience
  - Biomotor Fine-Jitter and Acoustic Speech Diagnostics
- **Lifetime Persistent Cognitive History**: All session results and cumulative CPS metrics are permanently tracked in local persistent storage (`CognitiveHistoryManager.kt`), surviving reboots and updates.
- **Daily Scorecards History**: Patients and clinicians can inspect past sessions with granular timestamps, game categories, AI difficulty levels, and personalized therapeutic guidance.
- **Dignity & Stigma Safeguards**: Avoids negative clinical labeling ("impaired", "failed"); framed constructively as uplifting memory journeys.

### 2. 🎮 Cognitive Brain Exercise Suite
- **Memory Card Match**: Senior-friendly card tiles with high touch targets, dynamic tactile feedback, and progressive pairs.
- **Pattern Recall**: Sequential visual pattern flash exercises that stimulate short-term working memory without cognitive exhaustion.
- **Color Stroop Challenge**: High-contrast chromatic Stroop conflict assessments testing cognitive inhibition and executive attention.
- **Ascending Trail Making Game**: Sequential numerical Trail Making Test (TMT-A) measuring processing speed, visual scanning, and motor coordination.

### 3. 🖼️ Autobiographical Reminiscence Memory Vault
- Interactive family reminiscence cards with high-contrast portraits, names, relationships, and voice recordings.
- Provides immediate autobiographical recall and emotional grounding during disorientation, sundowning, or memory lapses.

### 4. 📍 GPS Beacon Sentinel & Safe Zone Geofencing
- **Real-Time GPS Broadcaster (`TrackerForegroundService`)**: Securely broadcasts live patient coordinates over lightweight, decentralized MQTT topics to paired caregiver devices.
- **Wandering Geofence Sentinel**: Triggers immediate alerts when the patient exits pre-configured safe zones.
- **One-Tap "Take Me Home" Navigation**: Instantly opens navigation back to the patient's verified home address using native map routing or open turn-by-turn navigation.
- **Crash-Resistant Foreground Operation**: Battery-optimized foreground service with proactive runtime permission handling.

### 5. 🚨 Critical Safety Alarms & Wake-Up System
- **Full-Screen Wake-up Activity (`AlarmFullScreenActivity`)**: Overrides lock screens using `FLAG_SHOW_WHEN_LOCKED`, `FLAG_TURN_SCREEN_ON`, and `FLAG_KEEP_SCREEN_ON` for urgent safety checks.
- **Auditory & Haptic Pulses**: High-urgency pulsating alarm tones paired with continuous vibration patterns.
- **Boot Recovery (`BootReceiver`)**: Automatically reschedules alarms and resumes tracking immediately upon device reboot.

### 6. 🗣️ Multilingual Regional Support & TTS Voice Guidance
- **7 Regional Dialects Supported**:
  - English
  - Hindi (हिन्दी)
  - Assamese (অসমীয়া)
  - Mizo (Mizo ṭawng)
  - Khasi (Ka Ktien Khasi)
  - Manipuri (মৈতৈলোন্)
  - Nagamese
- Integrated Android `TextToSpeech` vocal guidance for every navigation action, prompt, and exercise instruction.

### 7. 🎨 Senior-Centric Design System
- **Dark-Whitish Ergonomics**: Ultra-clean cream and ivory backgrounds (`#FBF9F5` / `#F3EFEA`) paired with deep charcoal typography (`#1C1B1F`) for optimal contrast and reduced eye strain.
- **Accessibility**: Minimum touch targets of 48–56dp, oversized tactile buttons, clear status badges, and no text clipping.
- **Cultural Identity**: Authentic aesthetic accents inspired by Northeast India's heritage.

---

## 🔒 Privacy & Local Processing

- **100% Local Processing**: All cognitive calculations, game scores, and personal reminiscence data remain strictly on the local device.
- **Zero Cloud Account Mandate**: Does not lock patient data into proprietary subscription servers or commercial cloud APIs.

---

## 🛠️ Build & Development

### Requirements
- Android SDK 34 (Android 14)
- JDK 17 or JDK 21 (e.g., Android Studio JBR)
- Gradle 8.2+

### Building the Tracker App from Source

```powershell
# Set Java Home (adjust path as needed)
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"

# Build Tracker APK
.\gradlew assembleTrackerDebug

# Run Unit Tests
.\gradlew testTrackerDebugUnitTest
```

The compiled APK will be generated at:
`app/build/outputs/apk/tracker/debug/app-tracker-debug.apk`

And the distribution release APK is located at:
`release/Smaran-Tracker-v1.0.apk`
