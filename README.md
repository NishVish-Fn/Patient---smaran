# Smaran â€” Assistive Sentinel & Cognitive Care Platform (Patient App)

<p align="center">
  <img src="app/src/main/res/drawable/smaran_logo.png" alt="Smaran Logo" width="120" />
</p>

A modern Android application built in Kotlin, Jetpack Compose, and OpenStreetMap combining a **Zero-Cloud GPS Sentinel & Safety Beacon** with an on-device clinical cognitive health engine. 

Designed specifically for elder care and dementia/Alzheimer's patients, Smaran combines an authentic **Apple iOS Assistive Access** senior-friendly ergonomic UI with vibrant, high-contrast visual cues and artistic cultural motifs from Northeast India (Assam, Meghalaya, Manipur, Mizoram, Nagaland).

---

## ðŸ“¦ Pre-Built Release APK

The pre-built, production-ready APK is included directly within this repository:

* **Download APK**: [release/Smaran-Tracker-v1.0.apk](release/Smaran-Tracker-v1.0.apk)
* **Package Name**: `net.kibotu.geofencerelay.tracker`
* **Version**: 1.0.0
* **Target Android Version**: Android 8.0 (API 26) through Android 14 (API 34)

---

## ðŸŒŸ Key Features & Architecture

### 1. ðŸš¨ Critical Safety Alarms & Wake-Up Screen
- **Full-Screen Wake-up Activity (`AlarmFullScreenActivity`)**: Triggers high-priority alerts with `FLAG_KEEP_SCREEN_ON`, `FLAG_SHOW_WHEN_LOCKED`, and `FLAG_TURN_SCREEN_ON`. Wakes the device screen even when locked or asleep.
- **Ringtone & Vibration Engine**: High-urgency pulsating audio alerts using system alarm streams combined with continuous tactile vibration pulses.
- **High-Contrast Google-Style Floating Animations**: Dark-themed screen with pulsating, vibrant Google-style glowing orbs and quick-action acknowledgment buttons.
- **Boot Recovery (`BootReceiver`)**: Automatically reschedules active patient alarms immediately upon device reboot.

### 2. ðŸ§  100% On-Device Cognitive Performance Scoring (CPS Engine)
- **Mathematical Clinical Port**: Emulates an XGBoost + Random Forest ensemble model in pure Kotlin (`CpsEngine.kt`) executing in $<1\text{ms}$ directly in memory.
- **Multi-Domain Assessment**:
  - Functional Cognitive Age vs. Biological Age
  - Memory Retention Index (0â€“100%)
  - Executive Function Index (0â€“100%)
  - Reaction Latency & Error Recovery Metrics
  - 30-Day & 90-Day Cognitive Trajectory Forecasts
- **Zero Stigma Safeguards**: Completely eliminates clinical stigma tags ("Impaired", "Dementia", "Failure"). All exercises are presented encouragingly as *"Daily Memory Journeys"*.

### 3. ðŸŽ® Cognitive Training Games
- **Memory Card Match**: Large, high-contrast card tiles designed for high touch targets and elder dexterity. Dynamic card shuffling and instant feedback.
- **Pattern Recall**: Sequential cognitive flash exercises that build short-term working memory without frustrating difficulty spikes.
- **Adaptive Difficulty**: Dynamically adapts grid size and timing based on real-time latency without exposing difficulty levels.

### 4. ðŸ–¼ï¸ Autobiographical Reminiscence Memory Vault
- Interactive family reminiscence cards with high-contrast imagery, personal names, relationships, and voice prompts.
- Promotes autobiographical recall and emotional grounding during periods of disorientation or agitation.

### 5. ðŸ“ GPS Beacon & Safe Zone Sentinel
- **Real-Time GPS Broadcaster (`TrackerForegroundService`)**: Broadcasts accurate coordinates over a lightweight MQTT protocol to authorized caregivers.
- **Safe Zone Geofencing**: Alerts caregivers if patient wanders outside of designated safety boundaries.
- **"Show Directions Home"**: One-tap navigation taking the patient directly back to their saved home coordinates via native navigation apps or open maps.
- **Crash-Resistant Foreground Service**: Safe permission and location service checks preventing unexpected closures.

### 6. ðŸ—£ï¸ Multilingual Regional Support & TTS Audio
- **Full UI & Audio Guidance in 7 Regional Dialects**:
  - English
  - Hindi (à¤¹à¤¿à¤¨à¥à¤¦à¥€)
  - Assamese (à¦…à¦¸à¦®à§€à¦¯à¦¼à¦¾)
  - Mizo (Mizo á¹­awng)
  - Khasi (Ka Ktien Khasi)
  - Manipuri (à¦®à§ˆà¦¤à§ˆà¦²à§‹à¦¨à§)
  - Nagamese
- Integrated Android `TextToSpeech` audio announcements across all tabs and exercises.

### 7. ðŸŽ¨ Northeast India Regional Artistic Motifs
- Curated cultural motifs and traditional artistic styling representing the rich heritage of Northeast India:
  - **Assam**: Golden Muga silk and iconic Japi motifs.
  - **Meghalaya**: Living Root Bridges and sacred Khasi hill patterns.
  - **Manipur**: Elegant Pung Cholom drum rhythms and Loktak lake phumdis.
  - **Mizoram**: Intricate Puan textile geometric weaves.
  - **Nagaland**: Vibrant warrior shawls and ceremonial motifs.

---

## ðŸ› ï¸ Build & Development

### Requirements
- Android SDK 34
- JDK 17 or JDK 21
- Gradle 8.2+

### Building from Source

```powershell
# Build Patient Tracker APK
.\gradlew.bat assembleTrackerDebug

# Build Guardian APK
.\gradlew.bat assembleGuardianDebug

# Run Unit Tests
.\gradlew.bat testTrackerDebugUnitTest
```

The output APK will be placed at:
`app/build/outputs/apk/tracker/debug/app-tracker-debug.apk`

---

## ðŸ”’ Privacy & Local Processing
- **Zero Cloud API Billing / Zero Vendor Lock-in**: Coordinates relay through lightweight MQTT brokers (`broker.hivemq.com`) and open map tiles.
- **Strictly Local Patient Telemetry**: All memory game scores, cognitive calculations, and personal family vault details remain 100% on the local device.