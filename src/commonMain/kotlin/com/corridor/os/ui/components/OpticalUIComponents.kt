package com.corridor.os.ui.components

import com.corridor.os.ui.*
import com.corridor.os.core.*

/**
 * Specialized UI Components for Optical Computing
 */

/**
 * Base Optical Window class
 */
open class OpticalWindow(
    val title: String,
    var bounds: Rectangle,
    var wavelength: Double?,
    var zIndex: Int = 0
) : OpticalComponent {
    
    override val id = "window_${System.currentTimeMillis()}"
    override var isVisible = true
    
    protected val components = mutableListOf<OpticalComponent>()
    protected var isMinimized = false
    protected var isMaximized = false
    protected var isDragging = false
    
    override fun render(context: RenderContext) {
        if (!isVisible || isMinimized) return
        
        // Draw window frame
        renderWindowFrame(context)
        
        // Draw title bar
        renderTitleBar(context)
        
        // Draw window content area
        renderContentArea(context)
    }
    
    protected open fun renderWindowFrame(context: RenderContext) {
        // Window border with optical glow effect
        val borderColor = OpticalColor(100, 150, 200, wavelength = wavelength)
        val shadowColor = OpticalColor(0, 0, 0, alpha = 100)
        
        // Draw shadow
        val shadowBounds = Rectangle(bounds.x + 3, bounds.y + 3, bounds.width, bounds.height)
        context.drawRectangle(shadowBounds, shadowColor)
        
        // Draw main window background
        val windowBg = OpticalColor(45, 50, 60, alpha = 240)
        context.drawRectangle(bounds, windowBg)
        
        // Draw border with optical enhancement
        if (wavelength != null) {
            context.enableOpticalOverlay(wavelength!!)
        }
        
        // Draw border lines
        val borderWidth = 2
        context.drawRectangle(Rectangle(bounds.x, bounds.y, bounds.width, borderWidth), borderColor)
        context.drawRectangle(Rectangle(bounds.x, bounds.y + bounds.height - borderWidth, bounds.width, borderWidth), borderColor)
        context.drawRectangle(Rectangle(bounds.x, bounds.y, borderWidth, bounds.height), borderColor)
        context.drawRectangle(Rectangle(bounds.x + bounds.width - borderWidth, bounds.y, borderWidth, bounds.height), borderColor)
        
        if (wavelength != null) {
            context.disableOpticalOverlay()
        }
    }
    
    protected open fun renderTitleBar(context: RenderContext) {
        val titleBarBounds = Rectangle(bounds.x, bounds.y, bounds.width, 30)
        val titleBarColor = OpticalColor(60, 70, 85, alpha = 220)
        context.drawRectangle(titleBarBounds, titleBarColor)
        
        // Window title
        val titleFont = OpticalFont("Arial", 12, FontWeight.MEDIUM, opticalEnhanced = true)
        val titleColor = OpticalColor(220, 230, 240, wavelength = wavelength)
        context.drawText(title, bounds.x + 10, bounds.y + 20, titleFont, titleColor)
        
        // Window controls (minimize, maximize, close)
        renderWindowControls(context)
    }
    
    protected open fun renderWindowControls(context: RenderContext) {
        val controlSize = 15
        val controlY = bounds.y + 7
        val spacing = 20
        
        // Close button
        val closeX = bounds.x + bounds.width - controlSize - 10
        val closeColor = OpticalColor(255, 100, 100)
        context.drawRectangle(Rectangle(closeX, controlY, controlSize, controlSize), closeColor)
        
        // Maximize button
        val maxX = closeX - spacing
        val maxColor = OpticalColor(100, 255, 100)
        context.drawRectangle(Rectangle(maxX, controlY, controlSize, controlSize), maxColor)
        
        // Minimize button
        val minX = maxX - spacing
        val minColor = OpticalColor(255, 255, 100)
        context.drawRectangle(Rectangle(minX, controlY, controlSize, controlSize), minColor)
    }
    
    protected open fun renderContentArea(context: RenderContext) {
        // Content area background
        val contentBounds = Rectangle(bounds.x + 2, bounds.y + 32, bounds.width - 4, bounds.height - 34)
        val contentBg = OpticalColor(35, 40, 50)
        context.drawRectangle(contentBounds, contentBg)
    }
    
    override fun handleInput(event: OpticalInputEvent): Boolean {
        // Handle window-specific input
        when (event) {
            is OpticalInputEvent.HolographicTouch -> {
                return handleWindowTouch(event)
            }
            else -> return false
        }
    }
    
    protected open fun handleWindowTouch(touch: OpticalInputEvent.HolographicTouch): Boolean {
        // Check if touch is in title bar (for dragging)
        if (touch.y >= bounds.y && touch.y <= bounds.y + 30) {
            if (touch.x >= bounds.x && touch.x <= bounds.x + bounds.width - 70) {
                // Start dragging
                isDragging = true
                return true
            }
            
            // Check window controls
            val controlY = bounds.y + 7
            val controlSize = 15
            
            // Close button
            val closeX = bounds.x + bounds.width - controlSize - 10
            if (touch.x >= closeX && touch.x <= closeX + controlSize &&
                touch.y >= controlY && touch.y <= controlY + controlSize) {
                close()
                return true
            }
            
            // Maximize button
            val maxX = closeX - 20
            if (touch.x >= maxX && touch.x <= maxX + controlSize &&
                touch.y >= controlY && touch.y <= controlY + controlSize) {
                toggleMaximize()
                return true
            }
            
            // Minimize button
            val minX = maxX - 20
            if (touch.x >= minX && touch.x <= minX + controlSize &&
                touch.y >= controlY && touch.y <= controlY + controlSize) {
                minimize()
                return true
            }
        }
        
        return false
    }
    
    override fun update(deltaTime: Double) {
        components.forEach { it.update(deltaTime) }
    }
    
    // Window management methods
    open fun show() { isVisible = true }
    open fun hide() { isVisible = false }
    open fun minimize() { isMinimized = true }
    open fun maximize() { 
        isMaximized = !isMaximized
        if (isMaximized) {
            // Store original bounds and maximize
            bounds = Rectangle(0, 0, 1920, 1080)
        }
    }
    open fun toggleMaximize() { maximize() }
    open fun close() { hide() }
}

/**
 * Optical Code Editor Component
 */
class OpticalCodeEditor : OpticalComponent {
    override val id = "optical_code_editor"
    override var bounds = Rectangle(0, 0, 800, 600)
    override var wavelength: Double? = 1551.0
    override var isVisible = true
    
    private var currentCode = """
        ; Optical Assembly Example
        OMOV OAX, 42        ; Load value into optical register
        OADD OAX, OBX       ; Optical addition with parallel lanes
        OWDM WDM0, OAX, 32  ; Wavelength division multiplex
        OSTORE [1000], WDM0 ; Store to holographic memory
        OHALT               ; Halt system
    """.trimIndent()
    
    private val syntaxHighlighter = OpticalSyntaxHighlighter()
    
    override fun render(context: RenderContext) {
        // Editor background
        val editorBg = OpticalColor(25, 30, 35)
        context.drawRectangle(bounds, editorBg)
        
        // Line numbers background
        val lineNumberBg = OpticalColor(35, 40, 45)
        val lineNumberWidth = 50
        context.drawRectangle(Rectangle(bounds.x, bounds.y, lineNumberWidth, bounds.height), lineNumberBg)
        
        // Render code with syntax highlighting
        renderCodeWithHighlighting(context)
        
        // Render optical instruction overlays
        renderOpticalInstructionOverlays(context)
    }
    
    private fun renderCodeWithHighlighting(context: RenderContext) {
        val font = OpticalFont("Consolas", 12, FontWeight.NORMAL, opticalEnhanced = true)
        val lines = currentCode.split('\n')
        
        lines.forEachIndexed { index, line ->
            val y = bounds.y + 20 + index * 18
            
            // Line number
            val lineNumberColor = OpticalColor(120, 130, 140)
            context.drawText("${index + 1}", bounds.x + 5, y, font, lineNumberColor)
            
            // Code line with syntax highlighting
            val tokens = syntaxHighlighter.tokenize(line)
            var x = bounds.x + 55
            
            tokens.forEach { token ->
                val color = syntaxHighlighter.getTokenColor(token)
                context.drawText(token.text, x, y, font, color)
                x += token.text.length * 8 // Approximate character width
            }
        }
    }
    
    private fun renderOpticalInstructionOverlays(context: RenderContext) {
        // Highlight optical instructions with wavelength-specific overlays
        val lines = currentCode.split('\n')
        
        lines.forEachIndexed { index, line ->
            if (line.trim().startsWith("O")) { // Optical instruction
                val y = bounds.y + 20 + index * 18
                val instructionWavelength = getInstructionWavelength(line)
                
                context.enableOpticalOverlay(instructionWavelength)
                
                // Draw subtle highlight
                val highlightColor = OpticalColor(100, 150, 255, alpha = 30, wavelength = instructionWavelength)
                context.drawRectangle(Rectangle(bounds.x + 55, y - 2, bounds.width - 55, 16), highlightColor)
                
                context.disableOpticalOverlay()
            }
        }
    }
    
    private fun getInstructionWavelength(line: String): Double {
        return when {
            line.contains("OADD") || line.contains("OSUB") -> 1550.0
            line.contains("OMUL") || line.contains("ODIV") -> 1551.0
            line.contains("OMOV") || line.contains("OLOAD") -> 1552.0
            line.contains("OWDM") -> 1553.0
            else -> 1550.0
        }
    }
    
    override fun handleInput(event: OpticalInputEvent): Boolean = false
    override fun update(deltaTime: Double) {}
}

/**
 * Spectrum Display Component
 */
class SpectrumDisplay : OpticalComponent {
    override val id = "spectrum_display"
    override var bounds = Rectangle(0, 0, 600, 300)
    override var wavelength: Double? = 1552.0
    override var isVisible = true
    
    private val spectrumData = mutableMapOf<Double, Double>()
    
    init {
        // Initialize spectrum data
        for (i in 0 until 64) {
            val wl = 1530.0 + i * 0.625
            spectrumData[wl] = 0.1 + 0.8 * Math.random()
        }
    }
    
    override fun render(context: RenderContext) {
        // Background
        val bg = OpticalColor(20, 25, 30)
        context.drawRectangle(bounds, bg)
        
        // Grid lines
        renderGrid(context)
        
        // Spectrum curve
        renderSpectrum(context)
        
        // Wavelength markers
        renderWavelengthMarkers(context)
        
        // Peak indicators
        renderPeakIndicators(context)
    }
    
    private fun renderGrid(context: RenderContext) {
        val gridColor = OpticalColor(60, 65, 70, alpha = 100)
        
        // Vertical grid lines (wavelength)
        for (i in 0..10) {
            val x = bounds.x + (bounds.width * i / 10)
            context.drawRectangle(Rectangle(x, bounds.y, 1, bounds.height), gridColor)
        }
        
        // Horizontal grid lines (power)
        for (i in 0..5) {
            val y = bounds.y + (bounds.height * i / 5)
            context.drawRectangle(Rectangle(bounds.x, y, bounds.width, 1), gridColor)
        }
    }
    
    private fun renderSpectrum(context: RenderContext) {
        // Draw spectrum as wavelength visualization
        context.drawWavelengthSpectrum(spectrumData, bounds)
    }
    
    private fun renderWavelengthMarkers(context: RenderContext) {
        val font = OpticalFont("Arial", 10, FontWeight.NORMAL)
        val textColor = OpticalColor(180, 190, 200)
        
        // Major wavelength markers
        val majorWavelengths = listOf(1530.0, 1540.0, 1550.0, 1560.0, 1570.0)
        
        majorWavelengths.forEach { wl ->
            val x = bounds.x + ((wl - 1530.0) / 40.0 * bounds.width).toInt()
            val y = bounds.y + bounds.height - 15
            
            context.drawText("${wl.toInt()}nm", x - 15, y, font, textColor)
        }
    }
    
    private fun renderPeakIndicators(context: RenderContext) {
        // Find and mark spectral peaks
        val peaks = findSpectralPeaks()
        val peakColor = OpticalColor(255, 200, 0)
        
        peaks.forEach { peak ->
            val x = bounds.x + ((peak.wavelength - 1530.0) / 40.0 * bounds.width).toInt()
            val y = bounds.y + (bounds.height * (1.0 - peak.intensity)).toInt()
            
            // Draw peak marker
            context.drawRectangle(Rectangle(x - 2, y - 2, 5, 5), peakColor)
        }
    }
    
    private fun findSpectralPeaks(): List<SpectralPeak> {
        val peaks = mutableListOf<SpectralPeak>()
        val sortedData = spectrumData.toList().sortedBy { it.first }
        
        for (i in 1 until sortedData.size - 1) {
            val (wl, intensity) = sortedData[i]
            val prevIntensity = sortedData[i - 1].second
            val nextIntensity = sortedData[i + 1].second
            
            if (intensity > prevIntensity && intensity > nextIntensity && intensity > 0.5) {
                peaks.add(SpectralPeak(wl, intensity))
            }
        }
        
        return peaks
    }
    
    override fun handleInput(event: OpticalInputEvent): Boolean = false
    
    override fun update(deltaTime: Double) {
        // Update spectrum data with animation
        val time = System.currentTimeMillis() / 1000.0
        
        spectrumData.keys.forEach { wavelength ->
            val baseIntensity = spectrumData[wavelength] ?: 0.0
            val variation = 0.1 * kotlin.math.sin(time + wavelength / 100.0)
            spectrumData[wavelength] = kotlin.math.max(0.0, kotlin.math.min(1.0, baseIntensity + variation))
        }
    }
}

data class SpectralPeak(val wavelength: Double, val intensity: Double)

/**
 * Holographic Display Component
 */
class HolographicDisplay : OpticalComponent {
    override val id = "holographic_display"
    override var bounds = Rectangle(0, 0, 800, 600)
    override var wavelength: Double? = 1553.0
    override var isVisible = true
    
    private val holographicPatterns = mutableListOf<HolographicPattern>()
    private var rotationAngle = 0.0
    private var zoomLevel = 1.0
    
    init {
        // Initialize with sample patterns
        generateSamplePatterns()
    }
    
    private fun generateSamplePatterns() {
        for (i in 0 until 3) {
            val pattern = HolographicPattern(
                interferenceData = generateInterferencePattern(i),
                wavelength = 1553.0 + i * 0.5,
                coherenceLength = 1.0 + i * 0.3
            )
            holographicPatterns.add(pattern)
        }
    }
    
    private fun generateInterferencePattern(seed: Int): ByteArray {
        val size = 128
        val data = ByteArray(size * size)
        
        for (y in 0 until size) {
            for (x in 0 until size) {
                val dx = x - size / 2.0
                val dy = y - size / 2.0
                val r = kotlin.math.sqrt(dx * dx + dy * dy)
                val angle = kotlin.math.atan2(dy, dx)
                
                val interference = kotlin.math.sin(r * 0.1 + seed) * kotlin.math.cos(angle * 3 + seed)
                data[y * size + x] = ((interference + 1.0) * 127.5).toInt().toByte()
            }
        }
        
        return data
    }
    
    override fun render(context: RenderContext) {
        // Background
        val bg = OpticalColor(10, 15, 25)
        context.drawRectangle(bounds, bg)
        
        // Render holographic patterns
        renderHolographicPatterns(context)
        
        // Render 3D visualization overlay
        render3DOverlay(context)
        
        // Render controls overlay
        renderControlsOverlay(context)
    }
    
    private fun renderHolographicPatterns(context: RenderContext) {
        holographicPatterns.forEachIndexed { index, pattern ->
            val patternBounds = Rectangle(
                bounds.x + 50 + index * 200,
                bounds.y + 50,
                150, 150
            )
            
            context.drawHolographicPattern(pattern, patternBounds)
        }
    }
    
    private fun render3DOverlay(context: RenderContext) {
        // Render 3D coordinate system
        val centerX = bounds.x + bounds.width / 2
        val centerY = bounds.y + bounds.height / 2
        
        val axisColor = OpticalColor(100, 150, 200, alpha = 150)
        
        // X axis
        val xEnd = (centerX + 100 * kotlin.math.cos(rotationAngle)).toInt()
        val yEnd = (centerY + 100 * kotlin.math.sin(rotationAngle)).toInt()
        drawLine(context, centerX, centerY, xEnd, yEnd, axisColor)
        
        // Y axis
        val xEnd2 = (centerX + 100 * kotlin.math.cos(rotationAngle + 1.57)).toInt()
        val yEnd2 = (centerY + 100 * kotlin.math.sin(rotationAngle + 1.57)).toInt()
        drawLine(context, centerX, centerY, xEnd2, yEnd2, axisColor)
        
        // Z axis (perspective)
        val xEnd3 = centerX - 50
        val yEnd3 = centerY - 50
        drawLine(context, centerX, centerY, xEnd3, yEnd3, axisColor)
    }
    
    private fun renderControlsOverlay(context: RenderContext) {
        val font = OpticalFont("Arial", 10, FontWeight.NORMAL)
        val textColor = OpticalColor(200, 220, 240)
        
        context.drawText("Rotation: ${String.format("%.1f", rotationAngle * 57.3)}°", 
            bounds.x + 10, bounds.y + bounds.height - 40, font, textColor)
        context.drawText("Zoom: ${String.format("%.2f", zoomLevel)}x", 
            bounds.x + 10, bounds.y + bounds.height - 20, font, textColor)
    }
    
    private fun drawLine(context: RenderContext, x1: Int, y1: Int, x2: Int, y2: Int, color: OpticalColor) {
        // Simple line drawing (would be more sophisticated in real implementation)
        val steps = kotlin.math.max(kotlin.math.abs(x2 - x1), kotlin.math.abs(y2 - y1))
        for (i in 0..steps) {
            val x = x1 + (x2 - x1) * i / steps
            val y = y1 + (y2 - y1) * i / steps
            context.drawRectangle(Rectangle(x, y, 1, 1), color)
        }
    }
    
    override fun handleInput(event: OpticalInputEvent): Boolean {
        when (event) {
            is OpticalInputEvent.HolographicTouch -> {
                if (containsPoint(event.x, event.y)) {
                    // Rotate based on touch position
                    val centerX = bounds.x + bounds.width / 2
                    val centerY = bounds.y + bounds.height / 2
                    rotationAngle = kotlin.math.atan2((event.y - centerY).toDouble(), (event.x - centerX).toDouble())
                    return true
                }
            }
            else -> return false
        }
        return false
    }
    
    private fun containsPoint(x: Int, y: Int): Boolean {
        return x >= bounds.x && x < bounds.x + bounds.width &&
               y >= bounds.y && y < bounds.y + bounds.height
    }
    
    override fun update(deltaTime: Double) {
        // Auto-rotate if not being controlled
        rotationAngle += deltaTime * 0.5
        
        // Update holographic patterns
        holographicPatterns.forEachIndexed { index, pattern ->
            // Animate interference patterns
            val newData = generateInterferencePattern(index + (System.currentTimeMillis() / 1000).toInt())
            holographicPatterns[index] = pattern.copy(interferenceData = newData)
        }
    }
}

/**
 * Optical Syntax Highlighter
 */
class OpticalSyntaxHighlighter {
    
    fun tokenize(line: String): List<SyntaxToken> {
        val tokens = mutableListOf<SyntaxToken>()
        val words = line.split(Regex("\\s+"))
        
        words.forEach { word ->
            val tokenType = classifyToken(word)
            tokens.add(SyntaxToken(word, tokenType))
        }
        
        return tokens
    }
    
    private fun classifyToken(word: String): TokenType {
        return when {
            word.startsWith(";") -> TokenType.COMMENT
            word.matches(Regex("O[A-Z]+")) -> TokenType.OPTICAL_INSTRUCTION
            word.matches(Regex("WDM[0-9]")) -> TokenType.WDM_REGISTER
            word.matches(Regex("O[A-Z]X")) -> TokenType.OPTICAL_REGISTER
            word.matches(Regex("[0-9]+")) -> TokenType.NUMBER
            word.matches(Regex("\\[.*\\]")) -> TokenType.MEMORY_ADDRESS
            word.endsWith(":") -> TokenType.LABEL
            else -> TokenType.IDENTIFIER
        }
    }
    
    fun getTokenColor(token: SyntaxToken): OpticalColor {
        return when (token.type) {
            TokenType.COMMENT -> OpticalColor(100, 150, 100)
            TokenType.OPTICAL_INSTRUCTION -> OpticalColor(100, 200, 255, wavelength = 1551.0)
            TokenType.WDM_REGISTER -> OpticalColor(255, 200, 100, wavelength = 1553.0)
            TokenType.OPTICAL_REGISTER -> OpticalColor(200, 100, 255, wavelength = 1552.0)
            TokenType.NUMBER -> OpticalColor(255, 150, 150)
            TokenType.MEMORY_ADDRESS -> OpticalColor(150, 255, 150)
            TokenType.LABEL -> OpticalColor(255, 255, 100)
            TokenType.IDENTIFIER -> OpticalColor(200, 200, 200)
        }
    }
}

data class SyntaxToken(val text: String, val type: TokenType)

enum class TokenType {
    COMMENT,
    OPTICAL_INSTRUCTION,
    WDM_REGISTER,
    OPTICAL_REGISTER,
    NUMBER,
    MEMORY_ADDRESS,
    LABEL,
    IDENTIFIER
}




