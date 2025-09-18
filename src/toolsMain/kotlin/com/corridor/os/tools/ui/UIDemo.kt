package com.corridor.os.tools.ui

import com.corridor.os.ui.*
import com.corridor.os.ui.framework.*
import com.corridor.os.ui.desktop.*
import com.corridor.os.core.*
import kotlinx.coroutines.*
import kotlinx.cli.*

/**
 * UI Demo Application for Corridor Computer OS
 * Demonstrates the optical computing interface system
 */
class CorridorUIDemo {
    
    private val uiFramework = CorridorUIFramework()
    private var isRunning = false
    private var demoJob: Job? = null
    
    fun start() {
        println("=== Corridor Computer OS UI Demo ===")
        println("Initializing optical computing interface...")
        
        // Initialize UI framework
        uiFramework.initialize()
        
        // Start demo loop
        isRunning = true
        demoJob = CoroutineScope(Dispatchers.Default).launch {
            runDemoLoop()
        }
        
        // Start interactive demo
        runInteractiveDemo()
    }
    
    fun stop() {
        isRunning = false
        demoJob?.cancel()
        println("UI Demo stopped")
    }
    
    private suspend fun runDemoLoop() {
        var lastTime = System.nanoTime()
        
        while (isRunning) {
            val currentTime = System.nanoTime()
            val deltaTime = (currentTime - lastTime) / 1_000_000_000.0
            lastTime = currentTime
            
            // Update UI framework
            uiFramework.update(deltaTime)
            
            // Render frame
            uiFramework.render()
            
            // Target 60 FPS
            delay(16)
        }
    }
    
    private fun runInteractiveDemo() {
        println("\nUI Demo started. Available commands:")
        showHelp()
        
        while (isRunning) {
            print("ui-demo> ")
            val input = readLine()?.trim() ?: continue
            
            when (input.lowercase()) {
                "help" -> showHelp()
                "quit", "exit" -> {
                    stop()
                    break
                }
                "launch-ide" -> launchOpticalIDE()
                "launch-analyzer" -> launchWavelengthAnalyzer()
                "launch-viewer" -> launchHolographicViewer()
                "launch-monitor" -> launchSystemMonitor()
                "launch-rom" -> launchROMManager()
                "launch-emulator" -> launchOpticalEmulator()
                "demo-input" -> demonstrateOpticalInput()
                "demo-themes" -> demonstrateThemes()
                "demo-visualization" -> demonstrateOpticalVisualization()
                "show-stats" -> showUIStatistics()
                "simulate-wavelength" -> simulateWavelengthGesture()
                "simulate-holographic" -> simulateHolographicTouch()
                "test-performance" -> testUIPerformance()
                else -> println("Unknown command: $input")
            }
        }
    }
    
    private fun showHelp() {
        println("""
            Available commands:
              help                    Show this help
              launch-ide             Launch Optical IDE
              launch-analyzer        Launch Wavelength Analyzer
              launch-viewer          Launch Holographic Viewer
              launch-monitor         Launch System Monitor
              launch-rom             Launch ROM Manager
              launch-emulator        Launch Optical Emulator
              demo-input             Demonstrate optical input methods
              demo-themes            Cycle through UI themes
              demo-visualization     Show optical data visualization
              show-stats             Display UI system statistics
              simulate-wavelength    Simulate wavelength gesture input
              simulate-holographic   Simulate holographic touch input
              test-performance       Run UI performance test
              quit                   Exit demo
        """.trimIndent())
    }
    
    private fun launchOpticalIDE() {
        println("Launching Optical IDE...")
        if (uiFramework.launchApplication("optical-ide")) {
            println("✓ Optical IDE launched successfully")
            println("  Features:")
            println("  - Optical assembly syntax highlighting")
            println("  - Wavelength-aware code editor")
            println("  - Real-time optical instruction visualization")
            println("  - Integrated wavelength debugger")
        } else {
            println("✗ Failed to launch Optical IDE")
        }
    }
    
    private fun launchWavelengthAnalyzer() {
        println("Launching Wavelength Analyzer...")
        if (uiFramework.launchApplication("wavelength-analyzer")) {
            println("✓ Wavelength Analyzer launched successfully")
            println("  Features:")
            println("  - Real-time spectrum display (1530-1570nm)")
            println("  - 32-channel wavelength monitoring")
            println("  - Optical power measurement")
            println("  - Signal quality analysis")
        } else {
            println("✗ Failed to launch Wavelength Analyzer")
        }
    }
    
    private fun launchHolographicViewer() {
        println("Launching Holographic Viewer...")
        if (uiFramework.launchApplication("holographic-viewer")) {
            println("✓ Holographic Viewer launched successfully")
            println("  Features:")
            println("  - 3D holographic pattern visualization")
            println("  - Interactive interference pattern display")
            println("  - Coherence length analysis")
            println("  - Multi-wavelength hologram support")
        } else {
            println("✗ Failed to launch Holographic Viewer")
        }
    }
    
    private fun launchSystemMonitor() {
        println("Launching System Monitor...")
        if (uiFramework.launchApplication("system-monitor")) {
            println("✓ System Monitor launched successfully")
            println("  Features:")
            println("  - Optical CPU utilization (32 channels)")
            println("  - Holographic memory usage")
            println("  - Wavelength channel status")
            println("  - Thermal and power monitoring")
        } else {
            println("✗ Failed to launch System Monitor")
        }
    }
    
    private fun launchROMManager() {
        println("Launching ROM Manager...")
        if (uiFramework.launchApplication("rom-manager")) {
            println("✓ ROM Manager launched successfully")
            println("  Features:")
            println("  - 4-slot ejectable ROM management")
            println("  - Optical alignment control")
            println("  - Holographic disk browser")
            println("  - Real-time insertion/ejection monitoring")
        } else {
            println("✗ Failed to launch ROM Manager")
        }
    }
    
    private fun launchOpticalEmulator() {
        println("Launching Optical Emulator...")
        if (uiFramework.launchApplication("optical-emulator")) {
            println("✓ Optical Emulator launched successfully")
            println("  Features:")
            println("  - Full optical system simulation")
            println("  - Real-time wavelength visualization")
            println("  - Performance monitoring")
            println("  - Hardware debugging tools")
        } else {
            println("✗ Failed to launch Optical Emulator")
        }
    }
    
    private fun demonstrateOpticalInput() {
        println("Demonstrating Optical Input Methods...")
        println()
        
        // Simulate wavelength gesture
        println("1. Wavelength Gesture Input:")
        val wavelengthGesture = OpticalInputEvent.WavelengthGesture(1550.0, 0.85)
        uiFramework.handleOpticalInput(wavelengthGesture)
        println("   ✓ Simulated wavelength gesture at 1550nm with 85% intensity")
        
        // Simulate holographic touch
        println("2. Holographic Touch Input:")
        val holographicTouch = OpticalInputEvent.HolographicTouch(400, 300, 50, 0.7)
        uiFramework.handleOpticalInput(holographicTouch)
        println("   ✓ Simulated 3D holographic touch at (400,300,50) with 70% pressure")
        
        // Simulate optical pointer
        println("3. Optical Pointer Input:")
        val opticalPointer = OpticalInputEvent.OpticalPointer(1552.0, Point3D(500.0, 400.0, 0.0))
        uiFramework.handleOpticalInput(opticalPointer)
        println("   ✓ Simulated optical pointer at 1552nm wavelength")
        
        // Simulate voice command
        println("4. Voice Command Input:")
        val voiceCommand = OpticalInputEvent.VoiceCommand("show applications", 0.95)
        uiFramework.handleOpticalInput(voiceCommand)
        println("   ✓ Simulated voice command with 95% confidence")
        
        println()
        println("All optical input methods demonstrated successfully!")
    }
    
    private fun demonstrateThemes() {
        println("Demonstrating UI Themes...")
        
        val themes = listOf("default", "dark_optical", "light_optical", "spectrum")
        
        themes.forEach { theme ->
            println("Switching to theme: $theme")
            uiFramework.setTheme(theme)
            
            val currentTheme = uiFramework.getCurrentTheme()
            println("  Theme: ${currentTheme.name}")
            println("  Primary Color: RGB(${currentTheme.primaryColor.red}, ${currentTheme.primaryColor.green}, ${currentTheme.primaryColor.blue})")
            println("  Wavelength: ${currentTheme.primaryColor.wavelength}nm")
            println("  Optical Effects: ${currentTheme.animations.enableOpticalTransitions}")
            println()
            
            Thread.sleep(2000) // Pause to show theme change
        }
        
        println("Theme demonstration complete!")
    }
    
    private fun demonstrateOpticalVisualization() {
        println("Demonstrating Optical Data Visualization...")
        
        // Create sample optical data
        val wavelengthChannels = mutableMapOf<Double, ChannelData>()
        val powerLevels = mutableMapOf<Double, Double>()
        val coherenceMap = mutableMapOf<String, Double>()
        val parallelLanes = mutableListOf<LaneData>()
        
        // Generate wavelength channel data
        for (i in 0 until 32) {
            val wavelength = 1530.0 + i * 1.25
            val channelData = ChannelData(
                wavelength = wavelength,
                power = 0.5 + 0.4 * kotlin.math.sin(i * 0.2),
                utilization = 0.3 + 0.6 * kotlin.math.cos(i * 0.15),
                signalQuality = 0.85 + 0.1 * kotlin.math.sin(i * 0.3),
                temperature = 25.0 + 5.0 * kotlin.math.sin(i * 0.1)
            )
            wavelengthChannels[wavelength] = channelData
            powerLevels[wavelength] = channelData.power
            coherenceMap["channel_$i"] = 0.8 + 0.2 * kotlin.math.cos(i * 0.25)
        }
        
        // Generate parallel lane data
        for (i in 0 until 128) {
            val laneData = LaneData(
                laneId = i,
                isActive = i < 96, // 75% utilization
                throughput = if (i < 96) 100.0 + 50.0 * kotlin.math.random() else 0.0,
                errorRate = kotlin.math.random() * 0.01
            )
            parallelLanes.add(laneData)
        }
        
        val visualizationData = OpticalVisualizationData(
            wavelengthChannels = wavelengthChannels,
            powerLevels = powerLevels,
            coherenceMap = coherenceMap,
            parallelLanes = parallelLanes,
            timestamp = System.currentTimeMillis()
        )
        
        // Render optical visualization
        uiFramework.renderOpticalVisualization(visualizationData)
        
        println("✓ Optical visualization rendered:")
        println("  - 32 wavelength channels (1530-1570nm)")
        println("  - 128 parallel processing lanes")
        println("  - Real-time power and coherence data")
        println("  - Signal quality monitoring")
    }
    
    private fun showUIStatistics() {
        val stats = uiFramework.getUIStatistics()
        
        println("=== UI System Statistics ===")
        println("Active Windows: ${stats.activeWindows}")
        println("Running Applications: ${stats.runningApplications}")
        println("Render Frame Rate: ${String.format("%.1f", stats.renderFrameRate)} FPS")
        println("Optical Channels Active: ${stats.opticalChannelsActive}/32")
        println("UI Memory Usage: ${stats.memoryUsage}MB")
        
        // Display capabilities
        val displayCaps = uiFramework.getDisplayCapabilities()
        println()
        println("=== Display Capabilities ===")
        println("Resolution: ${displayCaps.resolution.width}x${displayCaps.resolution.height}")
        println("Color Gamut: ${displayCaps.colorGamut}")
        println("Optical Channels: ${displayCaps.opticalChannels}")
        println("Wavelength Range: ${displayCaps.wavelengthRange.startNm}-${displayCaps.wavelengthRange.endNm}nm")
        println("Max Refresh Rate: ${displayCaps.maxRefreshRate}Hz")
        println("HDR Support: ${displayCaps.hdrSupport}")
        println("Optical Overlay: ${displayCaps.opticalOverlaySupport}")
    }
    
    private fun simulateWavelengthGesture() {
        println("Simulating wavelength gesture input...")
        
        val wavelengths = listOf(1550.0, 1551.0, 1552.0, 1553.0)
        val intensities = listOf(0.5, 0.7, 0.9, 0.6)
        
        wavelengths.zip(intensities).forEach { (wavelength, intensity) ->
            val gesture = OpticalInputEvent.WavelengthGesture(wavelength, intensity)
            uiFramework.handleOpticalInput(gesture)
            
            println("  Wavelength: ${wavelength}nm, Intensity: ${String.format("%.1f", intensity * 100)}%")
            Thread.sleep(500)
        }
        
        println("✓ Wavelength gesture simulation complete")
    }
    
    private fun simulateHolographicTouch() {
        println("Simulating holographic touch input...")
        
        val touchPoints = listOf(
            Triple(200, 150, 25),
            Triple(400, 300, 50),
            Triple(600, 450, 75),
            Triple(800, 200, 30)
        )
        
        touchPoints.forEach { (x, y, z) ->
            val pressure = 0.5 + 0.4 * kotlin.math.random()
            val touch = OpticalInputEvent.HolographicTouch(x, y, z, pressure)
            uiFramework.handleOpticalInput(touch)
            
            println("  Touch: (${x}, ${y}, ${z}) Pressure: ${String.format("%.2f", pressure)}")
            Thread.sleep(300)
        }
        
        println("✓ Holographic touch simulation complete")
    }
    
    private fun testUIPerformance() {
        println("Running UI Performance Test...")
        
        val testDuration = 5000L // 5 seconds
        val startTime = System.currentTimeMillis()
        var frameCount = 0
        var inputCount = 0
        
        println("Testing for ${testDuration / 1000} seconds...")
        
        while (System.currentTimeMillis() - startTime < testDuration) {
            // Simulate frame rendering
            uiFramework.render()
            frameCount++
            
            // Simulate input events
            if (frameCount % 10 == 0) {
                val randomGesture = OpticalInputEvent.WavelengthGesture(
                    1530.0 + kotlin.math.random() * 40.0,
                    kotlin.math.random()
                )
                uiFramework.handleOpticalInput(randomGesture)
                inputCount++
            }
            
            Thread.sleep(1) // Small delay
        }
        
        val actualDuration = System.currentTimeMillis() - startTime
        val avgFrameRate = frameCount * 1000.0 / actualDuration
        val avgInputRate = inputCount * 1000.0 / actualDuration
        
        println()
        println("=== Performance Test Results ===")
        println("Test Duration: ${actualDuration}ms")
        println("Total Frames: $frameCount")
        println("Average Frame Rate: ${String.format("%.1f", avgFrameRate)} FPS")
        println("Input Events: $inputCount")
        println("Input Rate: ${String.format("%.1f", avgInputRate)} events/sec")
        
        val stats = uiFramework.getUIStatistics()
        println("Current UI Memory: ${stats.memoryUsage}MB")
        println("Active Optical Channels: ${stats.opticalChannelsActive}")
        
        println("✓ Performance test complete")
    }
}

/**
 * Main entry point for UI demo
 */
fun main(args: Array<String>) {
    val parser = ArgParser("corridor-ui-demo")
    
    val autoMode by parser.option(ArgType.Boolean, shortName = "a", description = "Auto demo mode")
        .default(false)
    val duration by parser.option(ArgType.Int, shortName = "d", description = "Demo duration (seconds)")
        .default(30)
    
    parser.parse(args)
    
    val demo = CorridorUIDemo()
    
    if (autoMode) {
        println("Running auto demo for $duration seconds...")
        demo.start()
        
        // Auto demo sequence
        Thread.sleep(2000)
        
        // Launch applications in sequence
        val applications = listOf("optical-ide", "wavelength-analyzer", "holographic-viewer")
        applications.forEach { app ->
            println("Auto-launching $app...")
            Thread.sleep(3000)
        }
        
        Thread.sleep((duration * 1000).toLong())
        demo.stop()
    } else {
        demo.start()
    }
}




