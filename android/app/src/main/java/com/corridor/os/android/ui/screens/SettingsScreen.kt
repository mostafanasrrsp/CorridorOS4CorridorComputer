package com.corridor.os.android.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.corridor.os.android.data.CalculationStats
import com.corridor.os.android.ui.components.*
import com.corridor.os.android.viewmodel.MainViewModel

@Composable
fun SettingsScreen(
    viewModel: MainViewModel
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SettingsHeader()
        }
        
        item {
            DefaultParametersCard(viewModel)
        }
        
        item {
            AppPreferencesCard(viewModel)
        }
        
        item {
            CalculationStatsCard(viewModel)
        }
        
        item {
            AppUsageCard(viewModel)
        }
        
        item {
            DataManagementCard(viewModel)
        }
        
        item {
            AboutCard()
        }
        
        // Add some bottom padding
        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun SettingsHeader() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PulsingIcon(
                Icons.Default.Settings,
                contentDescription = "Settings",
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                isAnimating = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                "App Settings",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                "Customize your Corridor OS experience",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultParametersCard(viewModel: MainViewModel) {
    var defaultAlpha by remember { mutableStateOf("1.0") }
    var defaultMass by remember { mutableStateOf("1.0") }
    var defaultAcceleration by remember { mutableStateOf("9.81") }
    
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Tune,
                    contentDescription = "Default Parameters",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Default Parameters",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            AccessibleTextField(
                value = defaultAlpha,
                onValueChange = { 
                    defaultAlpha = it
                    // TODO: Save to preferences
                },
                label = "Default Alpha (α)",
                supportingText = "Default encoding parameter value",
                placeholder = "1.0"
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            AccessibleTextField(
                value = defaultMass,
                onValueChange = { 
                    defaultMass = it
                    // TODO: Save to preferences
                },
                label = "Default Mass (m)",
                supportingText = "Default mass value in kilograms",
                placeholder = "1.0"
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            AccessibleTextField(
                value = defaultAcceleration,
                onValueChange = { 
                    defaultAcceleration = it
                    // TODO: Save to preferences
                },
                label = "Default Acceleration (a)",
                supportingText = "Default acceleration value in m/s²",
                placeholder = "9.81"
            )
        }
    }
}

@Composable
fun AppPreferencesCard(viewModel: MainViewModel) {
    var animationsEnabled by remember { mutableStateOf(true) }
    var hapticFeedback by remember { mutableStateOf(true) }
    var autoValidation by remember { mutableStateOf(true) }
    
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Palette,
                    contentDescription = "App Preferences",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "App Preferences",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Animations toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Enable Animations",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        "Show visual animations and transitions",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = animationsEnabled,
                    onCheckedChange = { animationsEnabled = it }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Haptic feedback toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Haptic Feedback",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        "Vibration feedback for button presses",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = hapticFeedback,
                    onCheckedChange = { hapticFeedback = it }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Auto validation toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Auto Validation",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        "Automatically validate decoder formula on startup",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = autoValidation,
                    onCheckedChange = { autoValidation = it }
                )
            }
        }
    }
}

@Composable
fun CalculationStatsCard(viewModel: MainViewModel) {
    // Mock statistics for now
    val stats = mapOf(
        "Total Calculations" to "156",
        "Momentum Calculations" to "45",
        "Force Calculations" to "67",
        "Energy Calculations" to "44",
        "Average Calculation Time" to "1.2μs",
        "Fastest Calculation" to "0.8μs",
        "Favorite Physics Type" to "Force"
    )
    
    AccessibleStatsCard(
        title = "Calculation Statistics",
        stats = stats,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun AppUsageCard(viewModel: MainViewModel) {
    val usageStats = mapOf(
        "App Launches" to "23",
        "Days Used" to "5",
        "First Launch" to "Dec 19, 2024",
        "Total Session Time" to "2h 34m",
        "Favorite Feature" to "Physics Calculator"
    )
    
    AccessibleStatsCard(
        title = "App Usage",
        stats = usageStats,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun DataManagementCard(viewModel: MainViewModel) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Storage,
                    contentDescription = "Data Management",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Data Management",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Export data button
            OutlinedButton(
                onClick = { /* TODO: Export calculation history */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Download, contentDescription = "Export")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Export Calculation History")
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Clear history button
            OutlinedButton(
                onClick = { /* TODO: Clear history with confirmation */ },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Clear")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Clear All History")
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Reset settings button
            OutlinedButton(
                onClick = { /* TODO: Reset to defaults */ },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(Icons.Default.Restore, contentDescription = "Reset")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Reset to Defaults")
            }
        }
    }
}

@Composable
fun AboutCard() {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Info,
                    contentDescription = "About",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "About Corridor OS Mobile",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                "Version 1.0.0-ALPHA",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                "A demonstration of the physics decoder formula and optical computing concepts from the Corridor Computer OS project.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                "Built with Kotlin & Jetpack Compose",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedButton(
                onClick = { /* TODO: Open source code or website */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Code, contentDescription = "Source")
                Spacer(modifier = Modifier.width(8.dp))
                Text("View Source Code")
            }
        }
    }
}
