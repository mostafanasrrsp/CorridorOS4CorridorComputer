package com.corridor.os.android.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.*

@Composable
fun PulsingIcon(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.primary,
    isAnimating: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isAnimating) 1.2f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isAnimating) 0.7f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )
    
    Icon(
        imageVector = icon,
        contentDescription = contentDescription,
        modifier = modifier.scale(scale),
        tint = tint.copy(alpha = alpha)
    )
}

@Composable
fun SpinningIcon(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.primary,
    isSpinning: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "spin")
    
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (isSpinning) 360f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    
    Icon(
        imageVector = icon,
        contentDescription = contentDescription,
        modifier = modifier.rotate(rotation),
        tint = tint
    )
}

@Composable
fun OpticalWaveAnimation(
    isAnimating: Boolean = true,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary
) {
    val infiniteTransition = rememberInfiniteTransition(label = "optical_wave")
    
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (isAnimating) 2 * PI.toFloat() else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )
    
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
    ) {
        drawOpticalWave(phase, color)
    }
}

private fun DrawScope.drawOpticalWave(phase: Float, color: Color) {
    val width = size.width
    val height = size.height
    val centerY = height / 2
    val amplitude = height / 4
    
    // Draw multiple wavelengths with different colors (simulating optical channels)
    val wavelengths = listOf(
        Triple(1550f, color.copy(alpha = 0.8f), 1f),
        Triple(1540f, color.copy(alpha = 0.6f), 0.8f),
        Triple(1560f, color.copy(alpha = 0.4f), 0.6f)
    )
    
    wavelengths.forEach { (wavelength, waveColor, amplitudeScale) ->
        val points = mutableListOf<Offset>()
        val frequency = 2 * PI / (wavelength / 10) // Adjust frequency for visualization
        
        for (x in 0..width.toInt() step 2) {
            val y = centerY + (amplitude * amplitudeScale * sin(frequency * x + phase)).toFloat()
            points.add(Offset(x.toFloat(), y))
        }
        
        // Draw the wave
        for (i in 0 until points.size - 1) {
            drawLine(
                color = waveColor,
                start = points[i],
                end = points[i + 1],
                strokeWidth = 3.dp.toPx()
            )
        }
    }
}

@Composable
fun CalculationProgressIndicator(
    progress: Float,
    isAnimating: Boolean = true,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = if (isAnimating) progress else 0f,
        animationSpec = tween(1000, easing = EaseInOutCubic),
        label = "progress"
    )
    
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier.fillMaxSize(),
                strokeWidth = 8.dp,
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            
            Text(
                text = "${(animatedProgress * 100).toInt()}%",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Processing Physics Calculation...",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun PhysicsParticleEffect(
    isActive: Boolean = true,
    modifier: Modifier = Modifier
) {
    val particles = remember { 
        List(20) { 
            Particle(
                x = kotlin.random.Random.nextFloat(),
                y = kotlin.random.Random.nextFloat(),
                vx = (kotlin.random.Random.nextFloat() - 0.5f) * 0.002f,
                vy = (kotlin.random.Random.nextFloat() - 0.5f) * 0.002f,
                size = kotlin.random.Random.nextFloat() * 4 + 2
            )
        }.toMutableList()
    }
    
    LaunchedEffect(isActive) {
        while (isActive) {
            particles.forEach { particle ->
                particle.update()
            }
            delay(16) // ~60 FPS
        }
    }
    
    Canvas(
        modifier = modifier.fillMaxSize()
    ) {
        if (isActive) {
            particles.forEach { particle ->
                drawCircle(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                    radius = particle.size,
                    center = Offset(
                        particle.x * size.width,
                        particle.y * size.height
                    )
                )
            }
        }
    }
}

private data class Particle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    val size: Float
) {
    fun update() {
        x += vx
        y += vy
        
        // Bounce off edges
        if (x <= 0 || x >= 1) vx = -vx
        if (y <= 0 || y >= 1) vy = -vy
        
        // Keep in bounds
        x = x.coerceIn(0f, 1f)
        y = y.coerceIn(0f, 1f)
    }
}

@Composable
fun NumberCounterAnimation(
    targetValue: Double,
    modifier: Modifier = Modifier,
    formatPattern: String = "%.2f",
    animationDuration: Int = 1000
) {
    var currentValue by remember { mutableDoubleStateOf(0.0) }
    
    val animatedValue by animateDoubleAsState(
        targetValue = targetValue,
        animationSpec = tween(animationDuration, easing = EaseOutCubic),
        label = "number_counter"
    )
    
    LaunchedEffect(animatedValue) {
        currentValue = animatedValue
    }
    
    Text(
        text = String.format(formatPattern, currentValue),
        modifier = modifier,
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
fun SlideInCard(
    content: @Composable () -> Unit,
    isVisible: Boolean = true,
    delay: Int = 0,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            animationSpec = tween(600, delayMillis = delay, easing = EaseOutCubic),
            initialOffsetY = { it }
        ) + fadeIn(
            animationSpec = tween(600, delayMillis = delay)
        ),
        exit = slideOutVertically(
            animationSpec = tween(400, easing = EaseInCubic),
            targetOffsetY = { -it }
        ) + fadeOut(
            animationSpec = tween(400)
        ),
        modifier = modifier
    ) {
        content()
    }
}

@Composable
fun PhysicsTypeIndicator(
    physicsType: Int, // 1=momentum, 2=force, 3=energy
    modifier: Modifier = Modifier
) {
    val color = when (physicsType) {
        1 -> MaterialTheme.colorScheme.primary
        2 -> MaterialTheme.colorScheme.secondary  
        3 -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.outline
    }
    
    val icon = when (physicsType) {
        1 -> Icons.Default.Speed
        2 -> Icons.Default.Straighten
        3 -> Icons.Default.FlashOn
        else -> Icons.Default.Calculate
    }
    
    val label = when (physicsType) {
        1 -> "Momentum"
        2 -> "Force"
        3 -> "Energy"
        else -> "Unknown"
    }
    
    Row(
        modifier = modifier
            .background(
                color.copy(alpha = 0.1f),
                RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(16.dp),
            tint = color
        )
        
        Spacer(modifier = Modifier.width(6.dp))
        
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = color,
            fontWeight = FontWeight.Medium
        )
    }
}
