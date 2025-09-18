#!/bin/bash

# Corridor OS Android Build Script
# Builds the Android app with physics decoder formula

set -e  # Exit on any error

echo "🚀 Building Corridor OS Mobile..."
echo "=================================="

# Check if we're in the right directory
if [ ! -f "android/build.gradle.kts" ]; then
    echo "❌ Error: Please run this script from the CorridorComputerOS root directory"
    exit 1
fi

# Navigate to android directory
cd android

echo "📱 Setting up Android build environment..."

# Check for Java 17+
if ! command -v java &> /dev/null; then
    echo "❌ Error: Java is required but not installed"
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | grep version | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt "17" ]; then
    echo "❌ Error: Java 17+ is required (found Java $JAVA_VERSION)"
    exit 1
fi

echo "✅ Java $JAVA_VERSION detected"

# Make gradlew executable
chmod +x gradlew

echo "🔧 Cleaning previous builds..."
./gradlew clean

echo "📦 Building debug APK..."
./gradlew assembleDebug

# Check if build was successful
if [ -f "build/outputs/apk/debug/android-debug.apk" ]; then
    echo "✅ Build successful!"
    echo ""
    echo "📱 APK Location: android/build/outputs/apk/debug/android-debug.apk"
    echo ""
    echo "🔧 To install on connected device:"
    echo "   ./gradlew installDebug"
    echo ""
    echo "🧪 To run comprehensive test suite:"
    echo "   ./run_tests.sh"
    echo ""
    echo "📋 To run specific tests:"
    echo "   ./gradlew test                    # Unit tests only"
    echo "   ./gradlew connectedAndroidTest    # Integration tests"
    echo ""
    echo "🚀 To build release APK (requires signing):"
    echo "   ./gradlew assembleRelease"
    echo ""
    echo "🎉 Corridor OS Mobile is ready!"
    echo ""
    echo "✨ Enhanced Features:"
    echo "  ✓ Physics Decoder Formula Calculator"
    echo "  ✓ Interactive Material Design 3 UI"
    echo "  ✓ Real-time Performance Benchmarks"
    echo "  ✓ Optical Computing Visualizations"
    echo "  ✓ Mobile Hardware Analysis"
    echo "  ✓ Advanced Error Handling & User Feedback"
    echo "  ✓ Data Persistence & Calculation History"
    echo "  ✓ Smooth Animations & Visual Effects"
    echo "  ✓ Performance Optimizations"
    echo "  ✓ Full Accessibility Support"
    echo "  ✓ Settings & Preferences Management"
    echo ""
    echo "📊 App Statistics:"
    echo "  • Lines of Code: ~3,500+"
    echo "  • Components: 15+ custom UI components"
    echo "  • Screens: 4 main screens + settings"
    echo "  • Animations: Optical wave, particle effects, pulsing icons"
    echo "  • Accessibility: Full TalkBack support, haptic feedback"
    echo ""
else
    echo "❌ Build failed - check logs above"
    exit 1
fi
