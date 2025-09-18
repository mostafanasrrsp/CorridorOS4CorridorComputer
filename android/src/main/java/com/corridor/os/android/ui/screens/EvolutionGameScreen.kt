package com.corridor.os.android.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.corridor.os.android.game.*
import com.corridor.os.android.ui.components.*
import kotlin.math.*

@Composable
fun EvolutionGameScreen() {
    var currentStage by remember { mutableStateOf(EvolutionStage.PRIMORDIAL_SOUP) }
    var gameTime by remember { mutableDoubleStateOf(0.0) }
    var isSimulating by remember { mutableStateOf(false) }
    var evolutionResults by remember { mutableStateOf<EvolutionSimulationResult?>(null) }
    
    // Player choices
    var populationGrowth by remember { mutableStateOf(0.1f) }
    var adaptationBonus by remember { mutableStateOf(0.1f) }
    var mutationRate by remember { mutableStateOf(1.0f) }
    var selectionPressure by remember { mutableStateOf(1.0f) }
    var intelligenceBonus by remember { mutableStateOf(1.0f) }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            EvolutionGameHeader(currentStage, gameTime)
        }
        
        item {
            EvolutionVisualization(currentStage, isSimulating)
        }
        
        item {
            DecoderFormulaGameCard()
        }
        
        item {
            PlayerControlsCard(
                populationGrowth = populationGrowth,
                adaptationBonus = adaptationBonus,
                mutationRate = mutationRate,
                selectionPressure = selectionPressure,
                intelligenceBonus = intelligenceBonus,
                onPopulationGrowthChange = { populationGrowth = it },
                onAdaptationBonusChange = { adaptationBonus = it },
                onMutationRateChange = { mutationRate = it },
                onSelectionPressureChange = { selectionPressure = it },
                onIntelligenceBonusChange = { intelligenceBonus = it }
            )
        }
        
        item {
            EvolutionActionButton(
                isSimulating = isSimulating,
                onSimulate = {
                    isSimulating = true
                    // TODO: Run evolution simulation
                    // evolutionResults = simulateEvolution(...)
                    gameTime += 1.0
                    isSimulating = false
                }
            )
        }
        
        evolutionResults?.let { results ->
            item {
                EvolutionResultsCard(results)
            }
        }
        
        item {
            EvolutionProgressCard(currentStage)
        }
        
        if (currentStage.unlocked.isNotEmpty()) {
            item {
                UnlockedTraitsCard(currentStage.unlocked)
            }
        }
    }
}

@Composable
fun EvolutionGameHeader(stage: EvolutionStage, gameTime: Double) {
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
                Icons.Default.Biotech,
                contentDescription = "Evolution",
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                isAnimating = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                "Evolution Decoder",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                stage.name,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                "Evolutionary Time: ${String.format("%.1f", gameTime)} Million Years",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun EvolutionVisualization(stage: EvolutionStage, isAnimating: Boolean) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                "Life Complexity Visualization",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Evolution visualization canvas
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                drawEvolutionVisualization(stage, isAnimating)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatChip("Complexity", "${stage.complexity}%", MaterialTheme.colorScheme.primary)
                StatChip("Intelligence", "${stage.intelligence}%", MaterialTheme.colorScheme.secondary)
                StatChip("Technology", "${stage.technology}%", MaterialTheme.colorScheme.tertiary)
            }
        }
    }
}

@Composable
fun StatChip(label: String, value: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(
                color.copy(alpha = 0.1f),
                RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}

@Composable
fun DecoderFormulaGameCard() {
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
                    Icons.Default.Functions,
                    contentDescription = "Evolution Formula",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Evolution Decoder Formula",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                "Q(β) = m · a^(α·δ_β,2) · c^(α·(δ_β,1+δ_β,3))",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                "Where β=1 (Survival), β=2 (Adaptation), β=3 (Intelligence)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun PlayerControlsCard(
    populationGrowth: Float,
    adaptationBonus: Float,
    mutationRate: Float,
    selectionPressure: Float,
    intelligenceBonus: Float,
    onPopulationGrowthChange: (Float) -> Unit,
    onAdaptationBonusChange: (Float) -> Unit,
    onMutationRateChange: (Float) -> Unit,
    onSelectionPressureChange: (Float) -> Unit,
    onIntelligenceBonusChange: (Float) -> Unit
) {
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
                    contentDescription = "Evolution Controls",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Evolution Parameters",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Population Growth Rate
            ParameterSlider(
                label = "Population Growth",
                value = populationGrowth,
                onValueChange = onPopulationGrowthChange,
                range = 0f..1f,
                description = "How fast your species reproduces"
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Adaptation Bonus
            ParameterSlider(
                label = "Adaptation Speed",
                value = adaptationBonus,
                onValueChange = onAdaptationBonusChange,
                range = 0f..2f,
                description = "How quickly you adapt to changes"
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Mutation Rate (α for survival)
            ParameterSlider(
                label = "Mutation Rate (α)",
                value = mutationRate,
                onValueChange = onMutationRateChange,
                range = 0.1f..3f,
                description = "Genetic variation intensity"
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Selection Pressure (α for adaptation)
            ParameterSlider(
                label = "Selection Pressure (α)",
                value = selectionPressure,
                onValueChange = onSelectionPressureChange,
                range = 0.1f..3f,
                description = "Environmental selection intensity"
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Intelligence Bonus (α for intelligence)
            ParameterSlider(
                label = "Intelligence Focus (α)",
                value = intelligenceBonus,
                onValueChange = onIntelligenceBonusChange,
                range = 0.1f..3f,
                description = "Brain development investment"
            )
        }
    }
}

@Composable
fun ParameterSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    range: ClosedFloatingPointRange<Float>,
    description: String
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = String.format("%.2f", value),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            modifier = Modifier.fillMaxWidth()
        )
        
        Text(
            text = description,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun EvolutionActionButton(
    isSimulating: Boolean,
    onSimulate: () -> Unit
) {
    Button(
        onClick = onSimulate,
        enabled = !isSimulating,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (isSimulating) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Evolving...")
        } else {
            Icon(Icons.Default.PlayArrow, contentDescription = "Evolve")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Advance Evolution")
        }
    }
}

@Composable
fun EvolutionResultsCard(results: EvolutionSimulationResult) {
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
                    Icons.Default.Science,
                    contentDescription = "Evolution Results",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onTertiaryContainer
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Evolution Results",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Evolution type results
            EvolutionTypeResult("Survival", results.survivalResult, Icons.Default.Favorite)
            Spacer(modifier = Modifier.height(8.dp))
            EvolutionTypeResult("Adaptation", results.adaptationResult, Icons.Default.Transform)
            Spacer(modifier = Modifier.height(8.dp))
            EvolutionTypeResult("Intelligence", results.intelligenceResult, Icons.Default.Psychology)
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                "Total Fitness: ${String.format("%.2e", results.totalFitness)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
            
            if (results.randomEvents.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "Random Events:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
                results.randomEvents.forEach { event ->
                    Text(
                        "• ${event.displayName}: ${event.description}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }
        }
    }
}

@Composable
fun EvolutionTypeResult(
    name: String,
    result: EvolutionResult,
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
                contentDescription = name,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onTertiaryContainer
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                name,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
        }
        
        Column(horizontalAlignment = Alignment.End) {
            Text(
                String.format("%.2e", result.fitnessScore),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
            Text(
                "${(result.survivalProbability * 100).toInt()}% survival",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
        }
    }
}

@Composable
fun EvolutionProgressCard(stage: EvolutionStage) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                "Evolution Progress",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Progress bars for different aspects
            ProgressBar("Complexity", stage.complexity, MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            ProgressBar("Intelligence", stage.intelligence, MaterialTheme.colorScheme.secondary)
            Spacer(modifier = Modifier.height(8.dp))
            ProgressBar("Technology", stage.technology, MaterialTheme.colorScheme.tertiary)
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                "Population: ${String.format("%.2e", stage.populationMass)} organisms",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                "Adaptation Rate: ${String.format("%.2f", stage.adaptationRate)} units/time",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun ProgressBar(label: String, progress: Int, color: Color) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "$progress%",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
        
        LinearProgressIndicator(
            progress = { progress / 100f },
            modifier = Modifier.fillMaxWidth(),
            color = color
        )
    }
}

@Composable
fun UnlockedTraitsCard(traits: List<EvolutionTrait>) {
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
                    Icons.Default.EmojiEvents,
                    contentDescription = "Unlocked Traits",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Unlocked Evolutionary Traits",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            traits.forEach { trait ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "Unlocked",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            trait.displayName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            trait.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawEvolutionVisualization(stage: EvolutionStage, isAnimating: Boolean) {
    val width = size.width
    val height = size.height
    val centerY = height / 2
    
    // Draw evolutionary tree/complexity visualization
    val complexity = stage.complexity / 100f
    val intelligence = stage.intelligence / 100f
    val technology = stage.technology / 100f
    
    // Draw base line (representing time/evolution)
    drawLine(
        color = Color.Gray,
        start = Offset(0f, centerY),
        end = Offset(width, centerY),
        strokeWidth = 2.dp.toPx()
    )
    
    // Draw complexity curve
    val points = mutableListOf<Offset>()
    for (x in 0..width.toInt() step 10) {
        val progress = x / width
        val y = centerY - (complexity * progress * height / 3) * sin(progress * PI).toFloat()
        points.add(Offset(x.toFloat(), y))
    }
    
    // Draw the evolution curve
    for (i in 0 until points.size - 1) {
        drawLine(
            color = Color.Blue,
            start = points[i],
            end = points[i + 1],
            strokeWidth = 3.dp.toPx()
        )
    }
    
    // Draw intelligence markers
    if (intelligence > 0.1f) {
        val intelligenceX = width * 0.8f
        val intelligenceY = centerY - intelligence * height / 4
        drawCircle(
            color = Color.Red,
            radius = 8.dp.toPx(),
            center = Offset(intelligenceX, intelligenceY)
        )
    }
    
    // Draw technology markers
    if (technology > 0.1f) {
        val techX = width * 0.9f
        val techY = centerY - technology * height / 4
        drawCircle(
            color = Color.Green,
            radius = 6.dp.toPx(),
            center = Offset(techX, techY)
        )
    }
}
