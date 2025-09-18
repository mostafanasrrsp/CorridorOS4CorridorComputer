# 📱 Android Studio Setup for Evolution Decoder

## 🎯 **Quick Setup Guide**

### **1. Open Project in Android Studio**
1. **Launch Android Studio**
2. **Select "Open"** (not "Import")
3. **Navigate to**: `/Users/mnasr/Desktop/CorridorComputerOS/android`
4. **Click "Open"**

### **2. Configure Project Settings**

When you see the "Edit Configuration" dialog:

#### **Basic Settings:**
- **Name**: `EvolutionDecoder` (or keep `CorridorGame`)
- **Module**: Select `app` from dropdown
- **Allow multiple instances**: ✅ Checked
- **Store as project file**: ✅ Checked

#### **Installation Options:**
- **Deploy**: `Nothing` (for now)
- **Install for all users**: ❌ Unchecked (optional)
- **Always install with package manager**: ❌ Unchecked
- **Clear app storage before deployment**: ❌ Unchecked

#### **Launch Options:**
- **Launch**: `Default Activity`
- **Launch Flags**: Leave empty

### **3. Sync Project**
1. **Wait for Gradle sync** to complete automatically
2. If prompted, **"Trust Project"**
3. **Accept any SDK licenses** if prompted

### **4. Build Configuration**
The project is configured as:
- **App Name**: Evolution Decoder
- **Package**: `com.corridor.evolutiondecoder`
- **Min SDK**: Android 7.0 (API 24)
- **Target SDK**: Android 14 (API 34)
- **Language**: Kotlin 100%
- **UI**: Jetpack Compose with Material Design 3

## 🔧 **If You Encounter Issues**

### **"Module not specified" Error:**
1. **Click the dropdown** next to "Module"
2. **Select "app"** from the list
3. **Click "Apply"**

### **Gradle Sync Issues:**
1. **File → Sync Project with Gradle Files**
2. **Build → Clean Project**
3. **Build → Rebuild Project**

### **SDK Issues:**
1. **Tools → SDK Manager**
2. **Install Android 14 (API 34)** if not installed
3. **Install Build Tools 34.0.0**

### **Java/Kotlin Issues:**
1. **File → Project Structure**
2. **Project → Project SDK**: Select Java 17 or 21
3. **Modules → app → Dependencies**: Verify Kotlin stdlib

## 🚀 **Build & Run**

### **To Build:**
1. **Build → Make Project** (Ctrl+F9)
2. **Or use terminal**: `./gradlew assembleDebug`

### **To Run on Device:**
1. **Connect your Redmi phone** via USB
2. **Enable Developer Options** and **USB Debugging**
3. **Click the green "Run" button** (▶️)
4. **Select your device** from the list

### **To Run in Emulator:**
1. **Tools → AVD Manager**
2. **Create Virtual Device**
3. **Select a phone model** (Pixel 7 recommended)
4. **Download Android 14 system image**
5. **Click "Run"** to start emulator

## 🎮 **Evolution Decoder Features**

Your game includes:
- **🧬 Evolution Simulation**: Physics-based life development
- **🎨 Beautiful Loading Screen**: Your corridor image
- **📱 Material Design 3**: Modern Android interface
- **🔬 Educational Content**: Learn real evolutionary biology
- **♿ Full Accessibility**: Screen reader support
- **🆓 Completely Free**: No ads, no purchases

## 📊 **Project Structure**

```
android/
├── app/
│   ├── src/main/
│   │   ├── java/com/corridor/os/android/
│   │   │   ├── MainActivity.kt
│   │   │   ├── SplashActivity.kt
│   │   │   ├── game/
│   │   │   │   ├── EvolutionEngine.kt
│   │   │   │   └── GameState.kt
│   │   │   ├── physics/
│   │   │   │   └── PhysicsCalculator.kt
│   │   │   └── ui/
│   │   │       ├── screens/
│   │   │       ├── components/
│   │   │       └── theme/
│   │   ├── res/
│   │   │   ├── drawable/
│   │   │   │   └── corridor_loading_screen.png
│   │   │   └── values/
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── build.gradle.kts
└── settings.gradle.kts
```

## 🎯 **What to Do in Android Studio**

1. **Click "Run"** in the Edit Configuration dialog
2. **Wait for Gradle sync** (first time may take a few minutes)
3. **Build the project** (Build → Make Project)
4. **Run on your device** or emulator
5. **Enjoy your Evolution Decoder game!**

## 🏆 **Success Indicators**

You'll know it's working when:
- ✅ **Gradle sync completes** without errors
- ✅ **Build succeeds** (green checkmark)
- ✅ **App installs** on device/emulator
- ✅ **Loading screen appears** with your corridor image
- ✅ **Evolution game launches** with physics calculator

Your physics decoder formula is now a complete mobile evolution game! 🧬📱⚡
