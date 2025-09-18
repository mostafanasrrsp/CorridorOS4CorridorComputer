package com.corridor.os.android.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.corridor.os.android.physics.PhysicsResult
import com.corridor.os.android.physics.PhysicsType
import com.corridor.os.android.ui.components.*
import com.corridor.os.android.viewmodel.MainViewModel
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhysicsScreen(
    viewModel: MainViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val physicsResults by viewModel.physicsResults.collectAsStateWithLifecycle()
    val validationResult by viewModel.validationResult.collectAsStateWithLifecycle()
    
    var selectedPhysicsType by remember { mutableIntStateOf(0) }
    val physicsTypes = listOf("Momentum", "Force", "Energy")
    
    // Input parameters
    var alpha by remember { mutableStateOf("1.0") }
    var mass by remember { mutableStateOf("1.0") }
    var acceleration by remember { mutableStateOf("9.81") }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SlideInCard(
                content = { DecoderFormulaHeader() },
                delay = 0
            )
        }
        
        item {
            SlideInCard(
                content = {
                    ValidationStatusCard(validationResult, uiState.isValidating) {
                        viewModel.validateDecoderFormula()
                    }
                },
                delay = 100
            )
        }
        
        item {
            OpticalWaveAnimation(
                isAnimating = !uiState.isCalculating,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        
        item {
            PhysicsTypeSelector(
                selectedType = selectedPhysicsType,
                onTypeSelected = { selectedPhysicsType = it }
            )
        }
        
        item {
            InputParametersCard(
                alpha = alpha,
                mass = mass,
                acceleration = acceleration,
                selectedType = selectedPhysicsType,
                onAlphaChange = { alpha = it },
                onMassChange = { mass = it },
                onAccelerationChange = { acceleration = it }
            )
        }
        
        item {
            CalculateButton(
                isCalculating = uiState.isCalculating,
                selectedType = selectedPhysicsType
            ) {
                val alphaValue = alpha.toDoubleOrNull() ?: 1.0
                val massValue = mass.toDoubleOrNull() ?: 1.0
                val accelerationValue = acceleration.toDoubleOrNull() ?: 9.81
                
                when (selectedPhysicsType) {
                    0 -> viewModel.calculateMomentum(alphaValue, massValue)
                    1 -> viewModel.calculateForce(alphaValue, massValue, accelerationValue)
                    2 -> viewModel.calculateEnergy(alphaValue, massValue)
                }
            }
        }
        
        if (physicsResults.isNotEmpty()) {
            item {
                Text(
                    "Recent Calculations",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
        
        items(physicsResults) { result ->
            PhysicsResultCard(result)
        }
        
        if (physicsResults.isNotEmpty()) {
            item {
                OutlinedButton(
                    onClick = { viewModel.clearResults() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Clear Results")
                }
            }
        }
        
        // Add some bottom padding
        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
    
    // Error handling
    uiState.error?.let { error ->
        LaunchedEffect(error) {
            // Show error snackbar
        }
    }
}

@Composable
fun DecoderFormulaHeader() {
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
                Icons.Default.Functions,
                contentDescription = "Physics Formula",
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                isAnimating = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                "Physics Decoder Formula",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                "Q(β) = m · a^(α·δ_β,2) · c^(α·(δ_β,1+δ_β,3))",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                "One formula, three fundamental physics laws",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ValidationStatusCard(
    validationResult: com.corridor.os.android.physics.ValidationResult?,
    isValidating: Boolean,
    onValidate: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (validationResult?.isValid == true) 
                MaterialTheme.colorScheme.secondaryContainer
            else 
                MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isValidating) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    if (validationResult?.isValid == true) Icons.Default.CheckCircle else Icons.Default.Error,
                    contentDescription = "Validation Status",
                    modifier = Modifier.size(24.dp),
                    tint = if (validationResult?.isValid == true) 
                        MaterialTheme.colorScheme.onSecondaryContainer
                    else 
                        MaterialTheme.colorScheme.onErrorContainer
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    if (isValidating) "Validating..." else 
                        if (validationResult?.isValid == true) "Formula Validated" else "Validation Failed",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                if (!isValidating && validationResult != null) {
                    Text(
                        "${validationResult.details.size} tests completed",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            
            TextButton(onClick = onValidate) {
                Text("Revalidate")
            }
        }
    }
}

@Composable
fun PhysicsTypeSelector(
    selectedType: Int,
    onTypeSelected: (Int) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                "Select Physics Quantity",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    Triple("Momentum", "p = mc", "β = 1"),
                    Triple("Force", "F = ma", "β = 2"),
                    Triple("Energy", "E = mc²", "β = 3")
                ).forEachIndexed { index, (title, formula, beta) ->
                    FilterChip(
                        onClick = { onTypeSelected(index) },
                        label = { 
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(title, style = MaterialTheme.typography.labelMedium)
                                Text(formula, style = MaterialTheme.typography.labelSmall)
                                Text(beta, style = MaterialTheme.typography.labelSmall)
                            }
                        },
                        selected = selectedType == index,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputParametersCard(
    alpha: String,
    mass: String,
    acceleration: String,
    selectedType: Int,
    onAlphaChange: (String) -> Unit,
    onMassChange: (String) -> Unit,
    onAccelerationChange: (String) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                "Input Parameters",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Alpha parameter
            OutlinedTextField(
                value = alpha,
                onValueChange = onAlphaChange,
                label = { Text("Alpha (α)") },
                supportingText = { 
                    Text(when (selectedType) {
                        0 -> "α=1 for momentum (p = mc)"
                        1 -> "α=1 for force (F = ma)"
                        2 -> "α=2 for energy (E = mc²)"
                        else -> "Encoding parameter"
                    })
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Mass parameter
            OutlinedTextField(
                value = mass,
                onValueChange = onMassChange,
                label = { Text("Mass (m)") },
                supportingText = { Text("Mass in kilograms") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Acceleration parameter (only for force)
            AnimatedVisibility(
                visible = selectedType == 1,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                OutlinedTextField(
                    value = acceleration,
                    onValueChange = onAccelerationChange,
                    label = { Text("Acceleration (a)") },
                    supportingText = { Text("Acceleration in m/s²") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun CalculateButton(
    isCalculating: Boolean,
    selectedType: Int,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = !isCalculating,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (isCalculating) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        
        Text(
            if (isCalculating) "Calculating..." else 
                "Calculate ${listOf("Momentum", "Force", "Energy")[selectedType]}"
        )
    }
}

@Composable
fun PhysicsResultCard(result: PhysicsResult) {
    val formatter = DecimalFormat("#.###E0")
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (result.physicsType) {
                PhysicsType.MOMENTUM -> MaterialTheme.colorScheme.primaryContainer
                PhysicsType.FORCE -> MaterialTheme.colorScheme.secondaryContainer
                PhysicsType.ENERGY -> MaterialTheme.colorScheme.tertiaryContainer
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                when (result.physicsType) {
                    PhysicsType.MOMENTUM -> Icons.Default.Speed
                    PhysicsType.FORCE -> Icons.Default.Straighten
                    PhysicsType.ENERGY -> Icons.Default.FlashOn
                    else -> Icons.Default.Calculate
                },
                contentDescription = result.physicsType.name,
                modifier = Modifier.size(32.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    result.physicsType.name.lowercase().replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    "${formatter.format(result.value)} ${result.unit}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                
                Text(
                    "α=${result.alpha}, β=${result.beta}, m=${result.mass}kg" + 
                    if (result.acceleration != 0.0) ", a=${result.acceleration}m/s²" else "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Text(
                    "Calculated in ${result.calculationTimeNs / 1000}μs",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
