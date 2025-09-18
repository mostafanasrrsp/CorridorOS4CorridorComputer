package com.corridor.os.android.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.corridor.os.android.physics.MobilePerformanceMetrics
import com.corridor.os.android.viewmodel.MainViewModel
import java.text.DecimalFormat

@Composable
fun BenchmarkScreen(
    viewModel: MainViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val performanceMetrics by viewModel.performanceMetrics.collectAsStateWithLifecycle()
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            BenchmarkHeader()
        }
        
        item {
            SystemInfoCard()
        }
        
        performanceMetrics?.let { metrics ->
            item {
                PerformanceMetricsCard(metrics)
            }
            
            item {
                TheoreticalComparisonCard(metrics)
            }
            
            item {
                OpticalComputingProjectionsCard(metrics)
            }
        }
        
        item {
            BenchmarkControls(
                isBenchmarking = uiState.isBenchmarking,
                onRunBenchmark = { viewModel.calculatePerformanceMetrics() }
            )
        }
        
        item {
            PhysicsCalculationBenchmarks(viewModel)
        }
        
        // Add some bottom padding
        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun BenchmarkHeader() {
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
            Icon(
                Icons.Default.Speed,
                contentDescription = "Benchmarks",
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                "Performance Benchmarks",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                "Mobile hardware vs theoretical optical computing",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun SystemInfoCard() {
    val cpuCores = Runtime.getRuntime().availableProcessors()
    val maxMemoryMB = Runtime.getRuntime().maxMemory() / (1024 * 1024)
    val freeMemoryMB = Runtime.getRuntime().freeMemory() / (1024 * 1024)
    val usedMemoryMB = maxMemoryMB - freeMemoryMB
    
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
                    Icons.Default.Memory,
                    contentDescription = "System Info",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Mobile System Information",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // CPU Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "CPU Cores",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "$cpuCores",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Column {
                    Text(
                        "Memory Usage",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "${usedMemoryMB}MB / ${maxMemoryMB}MB",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Memory usage bar
            LinearProgressIndicator(
                progress = { usedMemoryMB.toFloat() / maxMemoryMB.toFloat() },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
fun PerformanceMetricsCard(metrics: MobilePerformanceMetrics) {
    val formatter = DecimalFormat("#,###")
    val scientificFormatter = DecimalFormat("#.##E0")
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Analytics,
                    contentDescription = "Performance",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Mobile Performance Metrics",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Performance metrics grid
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricRow(
                    label = "Operations/Second",
                    value = formatter.format(metrics.operationsPerSecond),
                    icon = Icons.Default.Speed
                )
                
                MetricRow(
                    label = "Average Latency",
                    value = "${String.format("%.2f", metrics.averageLatencyNs)}ns",
                    icon = Icons.Default.Timer
                )
                
                MetricRow(
                    label = "Calculation Time",
                    value = "${String.format("%.2f", metrics.calculationTimeMs)}ms",
                    icon = Icons.Default.Schedule
                )
                
                MetricRow(
                    label = "CPU Cores",
                    value = "${metrics.cpuCores}",
                    icon = Icons.Default.Memory
                )
                
                MetricRow(
                    label = "Available Memory",
                    value = "${metrics.availableMemoryMB}MB",
                    icon = Icons.Default.Storage
                )
            }
        }
    }
}

@Composable
fun MetricRow(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                icon,
                contentDescription = label,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun TheoreticalComparisonCard(metrics: MobilePerformanceMetrics) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Compare,
                    contentDescription = "Comparison",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onTertiaryContainer
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Theoretical vs Mobile",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Comparison metrics
            ComparisonRow(
                label = "Processing Speed",
                mobileValue = "Current",
                theoreticalValue = "${String.format("%.0f", metrics.theoreticalOpticalSpeedup)}x faster",
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            ComparisonRow(
                label = "Parallel Channels",
                mobileValue = "${metrics.cpuCores} cores",
                theoreticalValue = "32 wavelengths",
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            ComparisonRow(
                label = "Memory Access",
                mobileValue = "~50 GB/s",
                theoreticalValue = "1000 GB/s",
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            ComparisonRow(
                label = "Power Efficiency",
                mobileValue = "Standard",
                theoreticalValue = "5x better",
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
        }
    }
}

@Composable
fun ComparisonRow(
    label: String,
    mobileValue: String,
    theoreticalValue: String,
    color: Color
) {
    Column {
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = color,
            fontWeight = FontWeight.Bold
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Mobile: $mobileValue",
                style = MaterialTheme.typography.bodySmall,
                color = color
            )
            Text(
                "Optical: $theoreticalValue",
                style = MaterialTheme.typography.bodySmall,
                color = color,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun OpticalComputingProjectionsCard(metrics: MobilePerformanceMetrics) {
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
                    Icons.Default.Lightbulb,
                    contentDescription = "Projections",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Optical Computing Projections",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                "If this mobile device had optical computing capabilities:",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            val projectedOps = metrics.operationsPerSecond * metrics.theoreticalOpticalSpeedup.toLong()
            val projectedLatency = metrics.averageLatencyNs / metrics.theoreticalOpticalSpeedup
            
            MetricRow(
                label = "Projected Operations/sec",
                value = DecimalFormat("#.##E0").format(projectedOps),
                icon = Icons.Default.Rocket
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            MetricRow(
                label = "Projected Latency",
                value = "${String.format("%.2f", projectedLatency)}ns",
                icon = Icons.Default.FlashOn
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            MetricRow(
                label = "Decoder Formula Rate",
                value = "${DecimalFormat("#,###").format(projectedOps / 3)} formulas/sec",
                icon = Icons.Default.Functions
            )
        }
    }
}

@Composable
fun BenchmarkControls(
    isBenchmarking: Boolean,
    onRunBenchmark: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                "Benchmark Controls",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Button(
                onClick = onRunBenchmark,
                enabled = !isBenchmarking,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isBenchmarking) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Running Benchmark...")
                } else {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Run")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Run Performance Benchmark")
                }
            }
        }
    }
}

@Composable
fun PhysicsCalculationBenchmarks(viewModel: MainViewModel) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                "Physics Decoder Benchmarks",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                "Real-time performance testing of the decoder formula:",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Text(
                "Q(β) = m · a^(α·δ_β,2) · c^(α·(δ_β,1+δ_β,3))",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Quick benchmark buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.calculateMomentum(1.0, 1.0) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Momentum", style = MaterialTheme.typography.labelMedium)
                }
                
                OutlinedButton(
                    onClick = { viewModel.calculateForce(1.0, 1.0, 9.81) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Force", style = MaterialTheme.typography.labelMedium)
                }
                
                OutlinedButton(
                    onClick = { viewModel.calculateEnergy(2.0, 1.0) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Energy", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}
