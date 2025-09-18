package com.corridor.os.ui

import kotlinx.serialization.Serializable

/**
 * UI Architecture for Corridor Computer OS
 * Designed specifically for optical computing workflows and visualization
 */

/**
 * Main UI framework supporting both optical-native and traditional displays
 */
interface UIFramework {
    fun initialize()
    fun createWindow(config: WindowConfig): Window
    fun getDisplayCapabilities(): DisplayCapabilities
    fun renderOpticalVisualization(data: OpticalVisualizationData)
    fun handleOpticalInput(input: OpticalInputEvent)
}

/**
 * Window configuration for optical computing interfaces
 */
@Serializable
data class WindowConfig(
    val title: String,
    val width: Int,
    val height: Int,
    val type: WindowType,
    val opticalEnhanced: Boolean = true,
    val wavelengthChannels: List<Double> = emptyList(),
    val refreshRate: Int = 120, // Hz - higher for optical data
    val colorDepth: ColorDepth = ColorDepth.HDR_10BIT
)

enum class WindowType {
    DESKTOP,
    OPTICAL_MONITOR,
    DEVELOPMENT_IDE,
    SYSTEM_CONTROL,
    WAVELENGTH_ANALYZER,
    HOLOGRAPHIC_VIEWER,
    PERFORMANCE_DASHBOARD
}

enum class ColorDepth {
    STANDARD_8BIT,
    HDR_10BIT,
    OPTICAL_12BIT, // Extended for optical wavelength representation
    SCIENTIFIC_16BIT
}

/**
 * Display capabilities for optical-enhanced monitors
 */
@Serializable
data class DisplayCapabilities(
    val resolution: Resolution,
    val colorGamut: ColorGamut,
    val opticalChannels: Int,
    val wavelengthRange: WavelengthRange,
    val maxRefreshRate: Int,
    val hdrSupport: Boolean,
    val opticalOverlaySupport: Boolean
)

@Serializable
data class Resolution(
    val width: Int,
    val height: Int,
    val pixelDensity: Int // PPI
)

enum class ColorGamut {
    SRGB,
    DISPLAY_P3,
    REC2020,
    OPTICAL_EXTENDED // Custom gamut for optical wavelength visualization
}

/**
 * Optical visualization data structures
 */
@Serializable
data class OpticalVisualizationData(
    val wavelengthChannels: Map<Double, ChannelData>,
    val powerLevels: Map<Double, Double>,
    val coherenceMap: Map<String, Double>,
    val parallelLanes: List<LaneData>,
    val timestamp: Long
)

@Serializable
data class ChannelData(
    val wavelength: Double,
    val power: Double,
    val utilization: Double,
    val signalQuality: Double,
    val temperature: Double
)

@Serializable
data class LaneData(
    val laneId: Int,
    val isActive: Boolean,
    val throughput: Double,
    val errorRate: Double
)

/**
 * Optical input events (from specialized optical input devices)
 */
sealed class OpticalInputEvent {
    data class WavelengthGesture(val wavelength: Double, val intensity: Double) : OpticalInputEvent()
    data class HolographicTouch(val x: Int, val y: Int, val z: Int, val pressure: Double) : OpticalInputEvent()
    data class OpticalPointer(val wavelength: Double, val position: Point3D) : OpticalInputEvent()
    data class VoiceCommand(val command: String, val confidence: Double) : OpticalInputEvent()
}

@Serializable
data class Point3D(val x: Double, val y: Double, val z: Double)

/**
 * UI Theme system optimized for optical computing
 */
@Serializable
data class OpticalTheme(
    val name: String,
    val primaryColor: OpticalColor,
    val secondaryColor: OpticalColor,
    val accentColor: OpticalColor,
    val backgroundColor: OpticalColor,
    val textColor: OpticalColor,
    val wavelengthColors: Map<Double, OpticalColor>,
    val animations: AnimationConfig
)

@Serializable
data class OpticalColor(
    val red: Int,
    val green: Int,
    val blue: Int,
    val alpha: Int = 255,
    val wavelength: Double? = null, // For optical-specific colors
    val intensity: Double = 1.0
)

@Serializable
data class AnimationConfig(
    val enableOpticalTransitions: Boolean = true,
    val wavelengthAnimationSpeed: Double = 1.0,
    val holographicEffects: Boolean = true,
    val particleEffects: Boolean = true
)

/**
 * Layout system for optical computing interfaces
 */
abstract class OpticalLayout {
    abstract fun measureAndLayout(constraints: LayoutConstraints): LayoutResult
    abstract fun handleOpticalResize(newWavelengths: List<Double>)
}

@Serializable
data class LayoutConstraints(
    val minWidth: Int,
    val maxWidth: Int,
    val minHeight: Int,
    val maxHeight: Int,
    val availableWavelengths: List<Double>
)

@Serializable
data class LayoutResult(
    val width: Int,
    val height: Int,
    val wavelengthAllocation: Map<Double, Int>,
    val components: List<ComponentLayout>
)

@Serializable
data class ComponentLayout(
    val id: String,
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int,
    val wavelength: Double?,
    val zIndex: Int
)

/**
 * Component system for optical UI elements
 */
interface OpticalComponent {
    val id: String
    val bounds: Rectangle
    val wavelength: Double?
    val isVisible: Boolean
    
    fun render(context: RenderContext)
    fun handleInput(event: OpticalInputEvent): Boolean
    fun update(deltaTime: Double)
}

@Serializable
data class Rectangle(
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int
)

/**
 * Render context for optical-enhanced rendering
 */
interface RenderContext {
    fun drawRectangle(rect: Rectangle, color: OpticalColor)
    fun drawText(text: String, x: Int, y: Int, font: OpticalFont, color: OpticalColor)
    fun drawWavelengthSpectrum(spectrum: Map<Double, Double>, bounds: Rectangle)
    fun drawHolographicPattern(pattern: HolographicPattern, bounds: Rectangle)
    fun drawOpticalFlow(flow: OpticalFlowData, bounds: Rectangle)
    fun enableOpticalOverlay(wavelength: Double)
    fun disableOpticalOverlay()
}

@Serializable
data class OpticalFont(
    val family: String,
    val size: Int,
    val weight: FontWeight,
    val opticalEnhanced: Boolean = false,
    val wavelengthTint: Double? = null
)

enum class FontWeight {
    LIGHT, NORMAL, MEDIUM, BOLD, HEAVY
}

@Serializable
data class HolographicPattern(
    val interferenceData: ByteArray,
    val wavelength: Double,
    val coherenceLength: Double
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is HolographicPattern) return false
        return interferenceData.contentEquals(other.interferenceData) &&
               wavelength == other.wavelength &&
               coherenceLength == other.coherenceLength
    }
    
    override fun hashCode(): Int {
        return interferenceData.contentHashCode() * 31 + wavelength.hashCode() + coherenceLength.hashCode()
    }
}

@Serializable
data class OpticalFlowData(
    val flowVectors: List<FlowVector>,
    val intensity: Double,
    val wavelength: Double
)

@Serializable
data class FlowVector(
    val startX: Int,
    val startY: Int,
    val endX: Int,
    val endY: Int,
    val magnitude: Double
)




