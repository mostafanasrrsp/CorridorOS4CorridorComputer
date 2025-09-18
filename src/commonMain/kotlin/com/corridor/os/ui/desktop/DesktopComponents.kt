package com.corridor.os.ui.desktop

import com.corridor.os.ui.*
import com.corridor.os.core.*
import kotlinx.coroutines.*

/**
 * Desktop UI Components for Corridor Computer OS
 */

// Base desktop widget class
abstract class DesktopWidget(
    override val id: String,
    override var bounds: Rectangle,
    override var wavelength: Double? = null,
    override var isVisible: Boolean = true
) : OpticalComponent {
    
    abstract override fun render(context: RenderContext)
    
    override fun handleInput(event: OpticalInputEvent): Boolean = false
    
    override fun update(deltaTime: Double) {}
    
    open fun containsPoint(x: Int, y: Int): Boolean {
        return x >= bounds.x && x < bounds.x + bounds.width &&
               y >= bounds.y && y < bounds.y + bounds.height
    }
    
    open fun handleTouch(touch: OpticalInputEvent.HolographicTouch): Boolean = false
    open fun handleOpticalPointer(pointer: OpticalInputEvent.OpticalPointer): Boolean = false
}

/**
 * System Performance Widget - Shows optical system performance
 */
class SystemPerformanceWidget(
    position: Point2D,
    size: Size
) : DesktopWidget(
    "system_performance",
    Rectangle(position.x, position.y, size.width, size.height),
    wavelength = 1550.0
) {
    
    private var cpuUsage = 0.0
    private var memoryUsage = 0.0
    private var opticalEfficiency = 0.0
    private var wavelengthUtilization = 0.0
    private val performanceHistory = mutableListOf<PerformanceSnapshot>()
    
    override fun render(context: RenderContext) {
        // Draw widget background
        val bgColor = OpticalColor(40, 45, 55, alpha = 220)
        context.drawRectangle(bounds, bgColor)
        
        // Draw title
        val titleFont = OpticalFont("Arial", 14, FontWeight.BOLD, opticalEnhanced = true)
        val titleColor = OpticalColor(200, 220, 255)
        context.drawText("System Performance", bounds.x + 10, bounds.y + 20, titleFont, titleColor)
        
        // Draw performance metrics
        drawPerformanceMetrics(context)
        
        // Draw performance graph
        drawPerformanceGraph(context)
    }
    
    private fun drawPerformanceMetrics(context: RenderContext) {
        val font = OpticalFont("Arial", 11, FontWeight.NORMAL)
        val textColor = OpticalColor(180, 200, 220)
        val y = bounds.y + 45
        
        // CPU Usage
        context.drawText("CPU: ${String.format("%.1f", cpuUsage)}%", bounds.x + 10, y, font, textColor)
        drawProgressBar(context, bounds.x + 80, y - 5, 100, 12, cpuUsage / 100.0, OpticalColor(0, 150, 255))
        
        // Memory Usage
        context.drawText("Memory: ${String.format("%.1f", memoryUsage)}%", bounds.x + 10, y + 25, font, textColor)
        drawProgressBar(context, bounds.x + 80, y + 20, 100, 12, memoryUsage / 100.0, OpticalColor(0, 255, 150))
        
        // Optical Efficiency
        context.drawText("Optical: ${String.format("%.1f", opticalEfficiency)}%", bounds.x + 10, y + 50, font, textColor)
        drawProgressBar(context, bounds.x + 80, y + 45, 100, 12, opticalEfficiency / 100.0, OpticalColor(255, 150, 0))
        
        // Wavelength Utilization
        context.drawText("Wavelength: ${String.format("%.1f", wavelengthUtilization)}%", bounds.x + 10, y + 75, font, textColor)
        drawProgressBar(context, bounds.x + 80, y + 70, 100, 12, wavelengthUtilization / 100.0, OpticalColor(255, 0, 150))
    }
    
    private fun drawProgressBar(context: RenderContext, x: Int, y: Int, width: Int, height: Int, progress: Double, color: OpticalColor) {
        // Background
        val bgColor = OpticalColor(60, 65, 75)
        context.drawRectangle(Rectangle(x, y, width, height), bgColor)
        
        // Progress fill
        val fillWidth = (width * progress).toInt()
        context.drawRectangle(Rectangle(x, y, fillWidth, height), color)
    }
    
    private fun drawPerformanceGraph(context: RenderContext) {
        val graphBounds = Rectangle(bounds.x + 10, bounds.y + 130, bounds.width - 20, 60)
        
        // Draw graph background
        val graphBg = OpticalColor(30, 35, 45, alpha = 180)
        context.drawRectangle(graphBounds, graphBg)
        
        // Draw performance history as lines
        if (performanceHistory.size > 1) {
            drawHistoryLine(context, graphBounds, performanceHistory.map { it.cpuUsage }, OpticalColor(0, 150, 255))
            drawHistoryLine(context, graphBounds, performanceHistory.map { it.opticalEfficiency }, OpticalColor(255, 150, 0))
        }
    }
    
    private fun drawHistoryLine(context: RenderContext, bounds: Rectangle, values: List<Double>, color: OpticalColor) {
        // Implementation would draw connected line segments
        // Simplified for this example
    }
    
    override fun update(deltaTime: Double) {
        // Update performance metrics (would get real data from system)
        cpuUsage = 45.0 + 20.0 * kotlin.math.sin(System.currentTimeMillis() / 3000.0)
        memoryUsage = 60.0 + 15.0 * kotlin.math.cos(System.currentTimeMillis() / 4000.0)
        opticalEfficiency = 85.0 + 10.0 * kotlin.math.sin(System.currentTimeMillis() / 5000.0)
        wavelengthUtilization = 70.0 + 25.0 * kotlin.math.cos(System.currentTimeMillis() / 2500.0)
        
        // Add to history
        val snapshot = PerformanceSnapshot(cpuUsage, memoryUsage, opticalEfficiency, wavelengthUtilization)
        performanceHistory.add(snapshot)
        
        // Keep only last 60 samples
        if (performanceHistory.size > 60) {
            performanceHistory.removeAt(0)
        }
    }
}

data class PerformanceSnapshot(
    val cpuUsage: Double,
    val memoryUsage: Double,
    val opticalEfficiency: Double,
    val wavelengthUtilization: Double
)

/**
 * Wavelength Monitor Widget - Shows optical wavelength channels
 */
class WavelengthMonitorWidget(
    position: Point2D,
    size: Size
) : DesktopWidget(
    "wavelength_monitor",
    Rectangle(position.x, position.y, size.width, size.height),
    wavelength = 1551.0
) {
    
    private val channelData = mutableMapOf<Double, ChannelData>()
    
    init {
        // Initialize channel data
        for (i in 0 until 32) {
            val wavelength = 1530.0 + i * 1.25
            channelData[wavelength] = ChannelData(wavelength, 0.8, 0.5, 0.9, 25.0)
        }
    }
    
    override fun render(context: RenderContext) {
        // Draw widget background
        val bgColor = OpticalColor(35, 40, 50, alpha = 220)
        context.drawRectangle(bounds, bgColor)
        
        // Draw title
        val titleFont = OpticalFont("Arial", 14, FontWeight.BOLD, opticalEnhanced = true)
        val titleColor = OpticalColor(200, 220, 255)
        context.drawText("Wavelength Channels", bounds.x + 10, bounds.y + 20, titleFont, titleColor)
        
        // Draw wavelength spectrum
        drawWavelengthSpectrum(context)
        
        // Draw channel status indicators
        drawChannelIndicators(context)
    }
    
    private fun drawWavelengthSpectrum(context: RenderContext) {
        val spectrumBounds = Rectangle(bounds.x + 10, bounds.y + 35, bounds.width - 20, 40)
        
        // Create spectrum data from channel data
        val spectrumData = channelData.mapValues { it.value.utilization }
        context.drawWavelengthSpectrum(spectrumData, spectrumBounds)
    }
    
    private fun drawChannelIndicators(context: RenderContext) {
        val startY = bounds.y + 85
        val font = OpticalFont("Arial", 9, FontWeight.NORMAL)
        val textColor = OpticalColor(160, 180, 200)
        
        var y = startY
        channelData.values.take(8).forEach { channel ->
            // Channel wavelength
            context.drawText("${String.format("%.1f", channel.wavelength)}nm", bounds.x + 10, y, font, textColor)
            
            // Utilization bar
            val barColor = getWavelengthColor(channel.wavelength)
            drawProgressBar(context, bounds.x + 80, y - 5, 80, 8, channel.utilization, barColor)
            
            // Power level
            context.drawText("${String.format("%.2f", channel.power)}mW", bounds.x + 170, y, font, textColor)
            
            y += 15
        }
    }
    
    private fun getWavelengthColor(wavelength: Double): OpticalColor {
        // Convert wavelength to RGB color approximation
        return when {
            wavelength < 1535.0 -> OpticalColor(255, 0, 255) // Violet
            wavelength < 1540.0 -> OpticalColor(0, 0, 255)   // Blue
            wavelength < 1545.0 -> OpticalColor(0, 255, 255) // Cyan
            wavelength < 1550.0 -> OpticalColor(0, 255, 0)   // Green
            wavelength < 1555.0 -> OpticalColor(255, 255, 0) // Yellow
            wavelength < 1560.0 -> OpticalColor(255, 150, 0) // Orange
            else -> OpticalColor(255, 0, 0)                  // Red
        }
    }
    
    override fun update(deltaTime: Double) {
        // Update channel data with simulated values
        channelData.forEach { (wavelength, channel) ->
            val time = System.currentTimeMillis() / 1000.0
            val newUtilization = 0.3 + 0.6 * kotlin.math.sin(time + wavelength / 100.0)
            val newPower = 0.5 + 0.4 * kotlin.math.cos(time * 1.5 + wavelength / 50.0)
            
            channelData[wavelength] = channel.copy(
                utilization = newUtilization,
                power = newPower
            )
        }
    }
}

/**
 * Optical System Status Widget
 */
class OpticalSystemStatusWidget(
    position: Point2D,
    size: Size
) : DesktopWidget(
    "optical_status",
    Rectangle(position.x, position.y, size.width, size.height),
    wavelength = 1552.0
) {
    
    private var laserStatus = "Online"
    private var amplifierStatus = "Active"
    private var detectorStatus = "Operational"
    private var alignmentStatus = "Aligned"
    private var temperatureC = 25.0
    private var powerConsumptionW = 15.2
    
    override fun render(context: RenderContext) {
        // Draw widget background
        val bgColor = OpticalColor(45, 35, 55, alpha = 220)
        context.drawRectangle(bounds, bgColor)
        
        // Draw title
        val titleFont = OpticalFont("Arial", 14, FontWeight.BOLD, opticalEnhanced = true)
        val titleColor = OpticalColor(220, 200, 255)
        context.drawText("Optical System", bounds.x + 10, bounds.y + 20, titleFont, titleColor)
        
        // Draw status items
        drawStatusItems(context)
        
        // Draw system diagram
        drawSystemDiagram(context)
    }
    
    private fun drawStatusItems(context: RenderContext) {
        val font = OpticalFont("Arial", 11, FontWeight.NORMAL)
        val labelColor = OpticalColor(180, 160, 200)
        val valueColor = OpticalColor(200, 255, 180)
        val y = bounds.y + 45
        
        // Status items
        val statusItems = listOf(
            "Laser" to laserStatus,
            "Amplifier" to amplifierStatus,
            "Detector" to detectorStatus,
            "Alignment" to alignmentStatus,
            "Temp" to "${String.format("%.1f", temperatureC)}°C",
            "Power" to "${String.format("%.1f", powerConsumptionW)}W"
        )
        
        statusItems.forEachIndexed { index, (label, value) ->
            val itemY = y + index * 20
            context.drawText("$label:", bounds.x + 10, itemY, font, labelColor)
            
            val statusColor = when (value) {
                "Online", "Active", "Operational", "Aligned" -> OpticalColor(100, 255, 100)
                else -> valueColor
            }
            context.drawText(value, bounds.x + 80, itemY, font, statusColor)
            
            // Status indicator dot
            val dotColor = when (value) {
                "Online", "Active", "Operational", "Aligned" -> OpticalColor(0, 255, 0)
                else -> OpticalColor(255, 255, 0)
            }
            context.drawRectangle(Rectangle(bounds.x + 180, itemY - 3, 6, 6), dotColor)
        }
    }
    
    private fun drawSystemDiagram(context: RenderContext) {
        // Simple optical system diagram
        val diagramBounds = Rectangle(bounds.x + 200, bounds.y + 45, 70, 120)
        
        // Draw laser
        context.drawRectangle(Rectangle(diagramBounds.x, diagramBounds.y, 15, 8), OpticalColor(255, 100, 100))
        context.drawText("Laser", diagramBounds.x - 5, diagramBounds.y + 20, 
            OpticalFont("Arial", 8, FontWeight.NORMAL), OpticalColor(200, 200, 200))
        
        // Draw optical path
        context.drawRectangle(Rectangle(diagramBounds.x + 15, diagramBounds.y + 3, 40, 2), OpticalColor(255, 255, 0))
        
        // Draw detector
        context.drawRectangle(Rectangle(diagramBounds.x + 55, diagramBounds.y, 15, 8), OpticalColor(100, 100, 255))
        context.drawText("Detector", diagramBounds.x + 50, diagramBounds.y + 20,
            OpticalFont("Arial", 8, FontWeight.NORMAL), OpticalColor(200, 200, 200))
    }
    
    override fun update(deltaTime: Double) {
        // Update system status (would get real data from hardware)
        val time = System.currentTimeMillis() / 1000.0
        temperatureC = 25.0 + 5.0 * kotlin.math.sin(time / 30.0)
        powerConsumptionW = 15.0 + 2.0 * kotlin.math.cos(time / 20.0)
    }
}

/**
 * Holographic Memory Widget
 */
class HolographicMemoryWidget(
    position: Point2D,
    size: Size
) : DesktopWidget(
    "holographic_memory",
    Rectangle(position.x, position.y, size.width, size.height),
    wavelength = 1553.0
) {
    
    private var totalMemoryGB = 64.0
    private var usedMemoryGB = 28.5
    private var coherenceTime = 1.2 // nanoseconds
    private var refreshRate = 1000 // Hz
    
    override fun render(context: RenderContext) {
        // Draw widget background
        val bgColor = OpticalColor(35, 55, 45, alpha = 220)
        context.drawRectangle(bounds, bgColor)
        
        // Draw title
        val titleFont = OpticalFont("Arial", 14, FontWeight.BOLD, opticalEnhanced = true)
        val titleColor = OpticalColor(200, 255, 220)
        context.drawText("Holographic Memory", bounds.x + 10, bounds.y + 20, titleFont, titleColor)
        
        // Draw memory usage
        drawMemoryUsage(context)
        
        // Draw holographic pattern visualization
        drawHolographicPattern(context)
    }
    
    private fun drawMemoryUsage(context: RenderContext) {
        val font = OpticalFont("Arial", 11, FontWeight.NORMAL)
        val textColor = OpticalColor(180, 220, 200)
        val y = bounds.y + 45
        
        // Memory usage
        val usagePercent = (usedMemoryGB / totalMemoryGB) * 100
        context.drawText("Usage: ${String.format("%.1f", usedMemoryGB)}GB / ${String.format("%.0f", totalMemoryGB)}GB", 
            bounds.x + 10, y, font, textColor)
        
        // Usage bar
        drawProgressBar(context, bounds.x + 10, y + 10, bounds.width - 20, 15, usagePercent / 100.0, OpticalColor(0, 255, 150))
        
        // Coherence time
        context.drawText("Coherence: ${String.format("%.2f", coherenceTime)}ns", 
            bounds.x + 10, y + 35, font, textColor)
        
        // Refresh rate
        context.drawText("Refresh: ${refreshRate}Hz", 
            bounds.x + 10, y + 55, font, textColor)
    }
    
    private fun drawHolographicPattern(context: RenderContext) {
        // Draw simplified holographic interference pattern
        val patternBounds = Rectangle(bounds.x + bounds.width - 80, bounds.y + 45, 70, 70)
        
        // Create simple interference pattern
        val pattern = HolographicPattern(
            interferenceData = ByteArray(100) { (it * 17).toByte() },
            wavelength = 1553.0,
            coherenceLength = coherenceTime * 0.3 // c * t approximation
        )
        
        context.drawHolographicPattern(pattern, patternBounds)
    }
    
    override fun update(deltaTime: Double) {
        // Update memory statistics
        val time = System.currentTimeMillis() / 1000.0
        usedMemoryGB = 25.0 + 10.0 * kotlin.math.sin(time / 60.0)
        coherenceTime = 1.0 + 0.4 * kotlin.math.cos(time / 45.0)
    }
}

/**
 * Desktop Shortcut
 */
class DesktopShortcut(
    private val title: String,
    private val applicationId: String,
    position: Point2D
) : DesktopWidget(
    "shortcut_$applicationId",
    Rectangle(position.x, position.y, 80, 100),
    wavelength = 1554.0
) {
    
    private var isSelected = false
    
    override fun render(context: RenderContext) {
        // Draw icon background
        val iconBounds = Rectangle(bounds.x + 10, bounds.y + 10, 60, 60)
        val bgColor = if (isSelected) OpticalColor(100, 150, 255, alpha = 100) else OpticalColor(60, 70, 80, alpha = 150)
        context.drawRectangle(iconBounds, bgColor)
        
        // Draw application icon (simplified)
        val iconColor = getApplicationColor(applicationId)
        val innerIconBounds = Rectangle(iconBounds.x + 10, iconBounds.y + 10, 40, 40)
        context.drawRectangle(innerIconBounds, iconColor)
        
        // Draw title
        val font = OpticalFont("Arial", 10, FontWeight.NORMAL)
        val textColor = OpticalColor(220, 220, 220)
        context.drawText(title, bounds.x + 5, bounds.y + 85, font, textColor)
    }
    
    private fun getApplicationColor(appId: String): OpticalColor {
        return when (appId) {
            "corridor-ide" -> OpticalColor(100, 200, 100)
            "wavelength-analyzer" -> OpticalColor(255, 200, 0)
            "holographic-viewer" -> OpticalColor(200, 100, 255)
            "system-monitor" -> OpticalColor(100, 150, 255)
            "rom-manager" -> OpticalColor(255, 150, 100)
            "optical-emulator" -> OpticalColor(150, 255, 200)
            else -> OpticalColor(150, 150, 150)
        }
    }
    
    override fun handleTouch(touch: OpticalInputEvent.HolographicTouch): Boolean {
        if (containsPoint(touch.x, touch.y)) {
            if (touch.pressure > 0.8) {
                // Launch application on firm press
                println("[SHORTCUT] Launching $applicationId")
                return true
            } else {
                // Select on light touch
                isSelected = true
                return true
            }
        }
        return false
    }
    
    override fun update(deltaTime: Double) {
        // Fade selection after time
        if (isSelected && System.currentTimeMillis() % 2000 < 100) {
            isSelected = false
        }
    }
}


