# 🧠 Brain Helper Android App (Obsidian UI)

> **"Think clearly. Live smarter."**  
> **"একটি পরিকল্পিত দিন, এক ধাপ করে।"**

Brain Helper is a production-grade personal AI assistant for Android designed with the **Obsidian UI Design System** and built natively with **Kotlin, Jetpack Compose, Material 3, and Room Database**. It provides bilingual support in **English and Bengali (বাংলা)** across all screens and user touchpoints.

---

## 📱 Visual Design System — Brain Helper Obsidian

The interface adheres strictly to the unified Obsidian Design System:

| Token | Hex Code | Role |
| :--- | :--- | :--- |
| **Obsidian Background** | `#101116` | Full-screen app background |
| **Primary Card Surface** | `#191C28` | Main hero, AI input, and content cards |
| **Secondary Surface** | `#22283A` | Metric cards, chip selectors, composer background |
| **Header Container** | `#202338` | Rounded-square logo container (~54dp) |
| **Obsidian Border** | `#2A2E40` | Subtle, crisp 1dp card borders |
| **Primary Text** | `#F4F5FC` | Headings, metrics, and primary labels |
| **Secondary Text** | `#B3B7C9` | Body text and list subtitles |
| **Muted Text** | `#9298AD` | Metadata, timestamps, and input placeholders |
| **Violet Primary** | `#7367F0` | Primary action buttons and selected tab indicators |
| **Violet Highlight** | `#A9A0FF` | Logo vector tint, badges, and accent glow |
| **Electric Blue** | `#83B8FF` | Task counts, system telemetry, and info highlights |
| **Mint Success** | `#6DD9C0` | Completed progress bar, active states, and scanner results |
| **Warm Accent** | `#FFBD85` | Memories, thought tags, and urgent priority alerts |

---

## 🌟 Implemented Native Android Screens

### 1. Home Dashboard
* **Header**: Rounded-square container (`#202338`) with violet neural circuit logo (`#A9A0FF`), title `"Brain Helper"`, tagline `"Think clearly. Live smarter."`, and trailing notification bell.
* **Hero Card (`#191C28`, 20dp radius)**:
  1. Violet uppercase badge: `"YOUR PERSONAL ASSISTANT"`
  2. Headline: `"Make today count."`
  3. Bengali subtitle: `"একটি পরিকল্পিত দিন, এক ধাপ করে।"`
  4. Two side-by-side metric cards bound to real Room database counts:
     - **Tasks / কাজ**: Electric-blue icon, dynamic count formatted with leading zeroes (e.g. `04`).
     - **Reminders**: Violet alarm icon, dynamic count (e.g. `02`).
* **AI Input Card (`#191C28`, 18dp radius)**:
  - Sparkle icon, title `"Ask Brain Helper"`, Bengali placeholder `"আজকের কাজগুলো সাজিয়ে দাও…"`.
  - Microphone button opening the voice assistant.
  - Prominent violet primary button: `"Start a conversation"`.
* **Quick Actions (2-Column Adaptive Grid)**:
  1. **Plan my day / দিনের পরিকল্পনা** (Calendar-clock, `#83B8FF`) → Daily Planner.
  2. **Smart alarm / অ্যালার্ম ও রিমাইন্ডার** (Alarm-clock, `#B7A5FF`) → Smart Alarm scheduler.
  3. **AI scanner / স্ক্যান ও বুঝুন** (Scan-text, `#6DD9C0`) → Camera document scanner.
  4. **My memory / নোট ও ব্যক্তিগত তথ্য** (Brain, `#FFBD85`) → Searchable personal memory.
* **Daily Progress Card**:
  - Live task completion ratio (`"%d of %d completed"`).
  - Material 3 progress bar (Mint / Electric blue).
  - Real repository-backed encouragement messages.
* **Bottom Navigation**: 5 primary destinations (Home, Tasks, AI Chat, Memory, Settings) with violet active pills and system insets padding.

### 2. AI Chat
* Interactive conversation stream with distinct user and assistant speech bubbles.
* **Action Proposal Cards**: Automatically generates executable actions (e.g. `ADD_TASK:title`, `SET_ALARM:time`, `SAVE_MEMORY:content`). Tapping **"Execute Action"** instantly commits the record to Room.
* Copy message to clipboard and clear chat history actions.

### 3. Voice Assistant
* Animated central AI orb with continuous pulsing scale responding to state: `IDLE`, `LISTENING`, `PROCESSING`, and `SPEAKING`.
* Integrated with Android `SpeechRecognizer` (`RECORD_AUDIO` permission flow).
* Spoken responses powered by Android `TextToSpeech` (with Bengali and English voice locale switching).

### 4. Tasks & Planner
* Tabs: **All**, **Today**, **Upcoming**, **Completed**.
* Priority badges: High (Red), Medium (Amber), Low (Blue).
* Real-time checkbox completion toggle with animated strikethrough and database updates.
* Dialog for adding new tasks with category and due times.

### 5. Smart Alarm & Reminders
* Separate tabs for **Smart Alarms** and **Reminders**.
* Real scheduling via Android `AlarmManager` (`setExactAndAllowWhileIdle`) and `PendingIntent`.
* Repeat schedules: Daily, Mon–Fri, Once.
* Wake challenges: Math Puzzle, Shake Challenge, Gentle Wake.
* Instant test notification trigger to test device alerts immediately.

### 6. Personal Memory
* Real-time keyword search across titles and note contents.
* Category filters: `IDEAS`, `PERSONAL`, `WORK`, `HEALTH`, `IMPORTANT`.
* Pinned notes support for high-priority reminders and knowledge.

### 7. AI Document Scanner
* Live CameraX preview with runtime camera permission handling.
* Zero-permission Photo Picker (`ActivityResultContracts.GetContent`) for importing documents.
* Extracted OCR text with one-tap Copy, Save to Memory, and AI Bullet Summarization.

### 8. Phone Control Center
* Live permission status cards for **Notifications**, **Exact Alarms**, **Microphone**, and **Camera**.
* Deep links to open relevant Android system settings directly.
* Live telemetry: Battery level (`BatteryManager`) and Network connectivity (`ConnectivityManager`).

### 9. Settings & Privacy
* In-app language toggle: **English** / **বাংলা (Bengali)**.
* Theme toggle: **Obsidian Dark** (Default) / **AMOLED Pitch Black**.
* AI configuration: Gemini 2.5 Flash / OpenRouter custom API key override.
* Clear chat history and full local storage reset controls.

---

## 🏗️ Technical Architecture & Stack

```
com.example
├── BrainHelperApp.kt            # Application subclass, notification channel setup
├── MainActivity.kt              # Root activity, edge-to-edge insets, navigation host
├── data/
│   ├── ai/
│   │   └── BrainAiService.kt    # Bilingual natural language & action parser engine
│   ├── alarm/
│   │   ├── AlarmScheduler.kt    # AlarmManager pending intent scheduler
│   │   └── AlarmReceiver.kt     # BroadcastReceiver for system alert notifications
│   └── local/
│       ├── TaskEntity.kt        # Room entity & DAO for tasks
│       ├── ReminderEntity.kt    # Room entity & DAO for smart alarms & reminders
│       ├── MemoryEntity.kt      # Room entity & DAO for notes and knowledge
│       ├── ChatMessageEntity.kt # Room entity & DAO for AI chat history
│       ├── AppDatabase.kt       # RoomDatabase with auto-seeding
│       └── BrainRepository.kt   # Repository data layer
├── ui/
│   ├── components/
│   │   ├── ObsidianComponents.kt # TopBar, Card, SummaryCard, PrimaryButton
│   │   └── ObsidianBottomBar.kt  # 5-destination Material 3 NavigationBar
│   ├── screens/
│   │   ├── HomeScreen.kt        # Exact Obsidian reference dashboard
│   │   ├── ChatScreen.kt        # AI conversation stream with action proposal cards
│   │   ├── VoiceScreen.kt       # Voice assistant with animated orb & speech recognition
│   │   ├── TasksScreen.kt       # Tasks & Planner with Room persistence
│   │   ├── AlarmsScreen.kt      # Smart Alarms & reminders with AlarmManager
│   │   ├── MemoryScreen.kt      # Personal memory with search and categories
│   │   ├── ScannerScreen.kt     # CameraX document scanner and OCR summarizer
│   │   ├── PhoneControlScreen.kt# Permission status, battery, and network telemetry
│   │   └── SettingsScreen.kt    # Language, theme, model config, and privacy
│   └── theme/
│       ├── Color.kt             # Centralized Obsidian design tokens
│       ├── Theme.kt             # Obsidian and AMOLED Dark color schemes
│       └── Type.kt              # Scalable typography with Bengali script fallbacks
└── viewmodel/
    └── BrainHelperViewModel.kt  # StateFlow-driven MVVM state manager
```

---

## 🚀 Building the Project

### Local Build with Gradle
Ensure Java 17 and Android SDK 36 are installed:

```bash
# Clean and assemble the real debug APK
./gradlew assembleDebug

# Output location:
# app/build/outputs/apk/debug/app-debug.apk
```

### Running Unit & Robolectric Tests
```bash
./gradlew testDebugUnitTest
```

---

## 🤖 Automated GitHub Actions APK Pipeline

Every commit pushed to `main` triggers `.github/workflows/android-build.yml`:
1. Sets up JDK 17 and Android SDK.
2. Executes `./gradlew assembleDebug`.
3. Verifies non-zero APK binary size and runs `unzip -t` structural validation.
4. Generates SHA256 checksums.
5. Saves the APK into:
   - `APK_DOWNLOAD/app-debug.apk`
   - `.build-outputs/app-debug.apk`
6. Uploads the workflow artifact (`app-debug-apk`).
7. Automatically commits the newly built `APK_DOWNLOAD/app-debug.apk` back to GitHub for easy browser downloads!
