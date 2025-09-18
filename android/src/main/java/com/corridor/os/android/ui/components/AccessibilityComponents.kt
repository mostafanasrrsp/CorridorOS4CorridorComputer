package com.corridor.os.android.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.semantics.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/**
 * Accessible button with haptic feedback and semantic descriptions
 */
@Composable
fun AccessibleButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentDescription: String,
    hapticFeedback: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    val context = LocalContext.current
    
    Button(
        onClick = {
            if (hapticFeedback) {
                performHapticFeedback(context)
            }
            onClick()
        },
        modifier = modifier.semantics {
            this.contentDescription = contentDescription
            if (!enabled) {
                disabled()
            }
        },
        enabled = enabled,
        content = content
    )
}

/**
 * Accessible input field with enhanced semantic information
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccessibleTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    supportingText: String? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    keyboardOptions: androidx.compose.foundation.text.KeyboardOptions = androidx.compose.foundation.text.KeyboardOptions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = placeholder?.let { { Text(it) } },
        supportingText = {
            if (isError && errorMessage != null) {
                Text(errorMessage)
            } else if (supportingText != null) {
                Text(supportingText)
            }
        },
        isError = isError,
        keyboardOptions = keyboardOptions,
        modifier = modifier.semantics {
            contentDescription = buildString {
                append("$label input field")
                if (placeholder != null && value.isEmpty()) {
                    append(", placeholder: $placeholder")
                }
                if (supportingText != null) {
                    append(", $supportingText")
                }
                if (isError && errorMessage != null) {
                    append(", error: $errorMessage")
                }
                if (value.isNotEmpty()) {
                    append(", current value: $value")
                }
            }
        }
    )
}

/**
 * Accessible card with semantic role and state information
 */
@Composable
fun AccessibleCard(
    modifier: Modifier = Modifier,
    title: String,
    description: String? = null,
    isClickable: Boolean = false,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val context = LocalContext.current
    
    Card(
        modifier = modifier
            .then(
                if (isClickable && onClick != null) {
                    Modifier
                        .clickable {
                            performHapticFeedback(context)
                            onClick()
                        }
                        .semantics {
                            role = Role.Button
                            contentDescription = buildString {
                                append(title)
                                if (description != null) {
                                    append(", $description")
                                }
                                append(", double tap to activate")
                            }
                        }
                } else {
                    Modifier.semantics {
                        heading()
                        contentDescription = buildString {
                            append(title)
                            if (description != null) {
                                append(", $description")
                            }
                        }
                    }
                }
            ),
        content = content
    )
}

/**
 * Accessible progress indicator with spoken updates
 */
@Composable
fun AccessibleProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier,
    description: String = "Loading",
    announceProgress: Boolean = true
) {
    val progressPercent = (progress * 100).toInt()
    
    CircularProgressIndicator(
        progress = { progress },
        modifier = modifier.semantics {
            progressBarRangeInfo = ProgressBarRangeInfo(progress, 0f..1f)
            contentDescription = "$description, $progressPercent percent complete"
            if (announceProgress) {
                liveRegion = LiveRegionMode.Polite
            }
        }
    )
}

/**
 * Accessible result display with structured information
 */
@Composable
fun AccessibleResultCard(
    title: String,
    value: String,
    unit: String,
    calculationTime: String,
    physicsType: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.semantics {
            contentDescription = buildString {
                append("$physicsType calculation result: ")
                append("$title equals $value $unit, ")
                append("calculated in $calculationTime")
            }
            heading()
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.semantics {
                    heading()
                }
            )
            
            Text(
                text = "$value $unit",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.semantics {
                    contentDescription = "Result: $value $unit"
                }
            )
            
            Text(
                text = "Calculated in $calculationTime",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.semantics {
                    contentDescription = "Performance: calculated in $calculationTime"
                }
            )
        }
    }
}

/**
 * Accessible formula display with mathematical content description
 */
@Composable
fun AccessibleFormulaCard(
    formula: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.semantics {
            contentDescription = buildString {
                append("Physics formula: ")
                append(description)
                append(", written as: ")
                append(convertFormulaToSpeech(formula))
            }
            heading()
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = formula,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.semantics {
                    contentDescription = convertFormulaToSpeech(formula)
                }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Accessible navigation with clear labeling
 */
@Composable
fun AccessibleNavigationBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    NavigationBarItem(
        selected = selected,
        onClick = {
            performHapticFeedback(context)
            onClick()
        },
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = null // Description handled by the item itself
            )
        },
        label = { Text(label) },
        modifier = modifier.semantics {
            role = Role.Tab
            contentDescription = buildString {
                append(label)
                if (selected) {
                    append(", selected")
                } else {
                    append(", not selected, double tap to select")
                }
            }
            if (selected) {
                selected = true
            }
        }
    )
}

/**
 * Convert mathematical formula to speech-friendly text
 */
private fun convertFormulaToSpeech(formula: String): String {
    return formula
        .replace("Q(β)", "Q of beta")
        .replace("α", "alpha")
        .replace("β", "beta")
        .replace("δ", "delta")
        .replace("·", " times ")
        .replace("^", " to the power of ")
        .replace("=", " equals ")
        .replace("(", " open parenthesis ")
        .replace(")", " close parenthesis ")
        .replace("²", " squared")
        .replace("³", " cubed")
}

/**
 * Perform haptic feedback on supported devices
 */
private fun performHapticFeedback(context: Context) {
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            val vibrator = vibratorManager?.defaultVibrator
            vibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @Suppress("DEPRECATION")
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            vibrator?.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            vibrator?.vibrate(50)
        }
    } catch (e: Exception) {
        // Ignore haptic feedback errors
    }
}

/**
 * Accessible statistics display
 */
@Composable
fun AccessibleStatsCard(
    title: String,
    stats: Map<String, String>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.semantics {
            contentDescription = buildString {
                append("$title statistics: ")
                stats.forEach { (key, value) ->
                    append("$key: $value, ")
                }
            }
            heading()
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.semantics {
                    heading()
                }
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            stats.forEach { (key, value) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics {
                            contentDescription = "$key: $value"
                        },
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = key,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                if (stats.keys.last() != key) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}
