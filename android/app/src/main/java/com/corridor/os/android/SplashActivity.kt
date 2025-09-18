package com.corridor.os.android

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.corridor.os.android.ui.theme.CorridorOSTheme
import kotlinx.coroutines.delay

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            CorridorOSTheme {
                SplashScreen {
                    // Navigate to main activity after loading
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                    finish()
                }
            }
        }
    }
}

@Composable
fun SplashScreen(onLoadingComplete: () -> Unit) {
    var loadingProgress by remember { mutableFloatStateOf(0f) }
    var showTitle by remember { mutableStateOf(false) }
    var showSubtitle by remember { mutableStateOf(false) }
    var showProgress by remember { mutableStateOf(false) }
    
    // Animation states
    val imageScale by animateFloatAsState(
        targetValue = if (showTitle) 1.0f else 1.2f,
        animationSpec = tween(2000, easing = EaseInOutCubic),
        label = "image_scale"
    )
    
    val imageAlpha by animateFloatAsState(
        targetValue = if (showTitle) 0.3f else 1.0f,
        animationSpec = tween(1500, easing = EaseInOutCubic),
        label = "image_alpha"
    )
    
    val progressAlpha by animateFloatAsState(
        targetValue = if (showProgress) 1.0f else 0.0f,
        animationSpec = tween(500),
        label = "progress_alpha"
    )
    
    // Loading sequence
    LaunchedEffect(Unit) {
        delay(500) // Initial pause
        showTitle = true
        delay(1000)
        showSubtitle = true
        delay(500)
        showProgress = true
        
        // Simulate loading progress
        for (i in 0..100) {
            loadingProgress = i / 100f
            delay(30) // 3 second total loading
        }
        
        delay(500) // Final pause
        onLoadingComplete()
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0D47A1), // Deep blue
                        Color(0xFF1565C0), // Medium blue
                        Color(0xFF1E88E5)  // Corridor blue
                    )
                )
            )
    ) {
        // Background corridor image
        Image(
            painter = painterResource(id = R.drawable.corridor_loading_screen),
            contentDescription = "Corridor Loading Screen",
            modifier = Modifier
                .fillMaxSize()
                .scale(imageScale)
                .alpha(imageAlpha),
            contentScale = ContentScale.Crop
        )
        
        // Overlay gradient for text readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.7f)
                        ),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
        )
        
        // Content overlay
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(1f))
            
            // Main title with animation
            AnimatedVisibility(
                visible = showTitle,
                enter = slideInVertically { it } + fadeIn(),
                exit = fadeOut()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Evolution Decoder",
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.displayMedium
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "The Physics of Life",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Subtitle with animation
            AnimatedVisibility(
                visible = showSubtitle,
                enter = slideInVertically { it } + fadeIn(),
                exit = fadeOut()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Guide life from primordial soup\nto space civilization",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.White.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Q(β) = m · a^(α·δ_β,2) · c^(α·(δ_β,1+δ_β,3))",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Cyan.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "Where β=1 (Survival), β=2 (Adaptation), β=3 (Intelligence)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.White.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Loading progress
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.alpha(progressAlpha)
            ) {
                LinearProgressIndicator(
                    progress = { loadingProgress },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(4.dp),
                    color = Color.Cyan,
                    trackColor = Color.White.copy(alpha = 0.3f)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = when {
                        loadingProgress < 0.3f -> "Initializing primordial soup..."
                        loadingProgress < 0.6f -> "Evolving cellular complexity..."
                        loadingProgress < 0.9f -> "Developing consciousness..."
                        else -> "Ready to evolve!"
                    },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Free • No Ads • Open Science",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Green.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// Create R.drawable reference (this would normally be auto-generated)
object R {
    object drawable {
        const val corridor_loading_screen = android.R.drawable.ic_menu_gallery // Placeholder
    }
}
