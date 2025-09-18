package com.corridor.os.android.data

import android.content.Context
import android.content.SharedPreferences
import com.corridor.os.android.physics.PhysicsResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString

class PreferencesManager(context: Context) {
    
    private val preferences: SharedPreferences = context.getSharedPreferences(
        "corridor_os_prefs", 
        Context.MODE_PRIVATE
    )
    
    private val json = Json { 
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
    
    // User preferences
    private val _isDarkMode = MutableStateFlow(preferences.getBoolean(KEY_DARK_MODE, false))
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()
    
    private val _isAnimationsEnabled = MutableStateFlow(preferences.getBoolean(KEY_ANIMATIONS, true))
    val isAnimationsEnabled: StateFlow<Boolean> = _isAnimationsEnabled.asStateFlow()
    
    private val _defaultAlpha = MutableStateFlow(preferences.getString(KEY_DEFAULT_ALPHA, "1.0") ?: "1.0")
    val defaultAlpha: StateFlow<String> = _defaultAlpha.asStateFlow()
    
    private val _defaultMass = MutableStateFlow(preferences.getString(KEY_DEFAULT_MASS, "1.0") ?: "1.0")
    val defaultMass: StateFlow<String> = _defaultMass.asStateFlow()
    
    private val _defaultAcceleration = MutableStateFlow(preferences.getString(KEY_DEFAULT_ACCELERATION, "9.81") ?: "9.81")
    val defaultAcceleration: StateFlow<String> = _defaultAcceleration.asStateFlow()
    
    // Calculation history
    private val _calculationHistory = MutableStateFlow<List<PhysicsResult>>(loadCalculationHistory())
    val calculationHistory: StateFlow<List<PhysicsResult>> = _calculationHistory.asStateFlow()
    
    // Statistics
    private val _totalCalculations = MutableStateFlow(preferences.getInt(KEY_TOTAL_CALCULATIONS, 0))
    val totalCalculations: StateFlow<Int> = _totalCalculations.asStateFlow()
    
    private val _favoritePhysicsType = MutableStateFlow(preferences.getInt(KEY_FAVORITE_TYPE, 1)) // Default to Force
    val favoritePhysicsType: StateFlow<Int> = _favoritePhysicsType.asStateFlow()
    
    fun setDarkMode(enabled: Boolean) {
        _isDarkMode.value = enabled
        preferences.edit().putBoolean(KEY_DARK_MODE, enabled).apply()
    }
    
    fun setAnimationsEnabled(enabled: Boolean) {
        _isAnimationsEnabled.value = enabled
        preferences.edit().putBoolean(KEY_ANIMATIONS, enabled).apply()
    }
    
    fun setDefaultAlpha(alpha: String) {
        _defaultAlpha.value = alpha
        preferences.edit().putString(KEY_DEFAULT_ALPHA, alpha).apply()
    }
    
    fun setDefaultMass(mass: String) {
        _defaultMass.value = mass
        preferences.edit().putString(KEY_DEFAULT_MASS, mass).apply()
    }
    
    fun setDefaultAcceleration(acceleration: String) {
        _defaultAcceleration.value = acceleration
        preferences.edit().putString(KEY_DEFAULT_ACCELERATION, acceleration).apply()
    }
    
    fun addCalculationToHistory(result: PhysicsResult) {
        val currentHistory = _calculationHistory.value.toMutableList()
        currentHistory.add(0, result) // Add to beginning
        
        // Keep only last 50 calculations
        if (currentHistory.size > 50) {
            currentHistory.removeAt(currentHistory.size - 1)
        }
        
        _calculationHistory.value = currentHistory
        saveCalculationHistory(currentHistory)
        
        // Update statistics
        val newTotal = _totalCalculations.value + 1
        _totalCalculations.value = newTotal
        preferences.edit().putInt(KEY_TOTAL_CALCULATIONS, newTotal).apply()
        
        // Update favorite type based on usage
        updateFavoritePhysicsType(result.beta)
    }
    
    fun clearCalculationHistory() {
        _calculationHistory.value = emptyList()
        preferences.edit().remove(KEY_CALCULATION_HISTORY).apply()
    }
    
    fun getCalculationStats(): CalculationStats {
        val history = _calculationHistory.value
        val momentumCount = history.count { it.beta == 1 }
        val forceCount = history.count { it.beta == 2 }
        val energyCount = history.count { it.beta == 3 }
        
        val avgCalculationTime = if (history.isNotEmpty()) {
            history.map { it.calculationTimeNs }.average()
        } else 0.0
        
        val fastestCalculation = history.minByOrNull { it.calculationTimeNs }
        val slowestCalculation = history.maxByOrNull { it.calculationTimeNs }
        
        return CalculationStats(
            totalCalculations = _totalCalculations.value,
            momentumCalculations = momentumCount,
            forceCalculations = forceCount,
            energyCalculations = energyCount,
            averageCalculationTimeNs = avgCalculationTime,
            fastestCalculationTimeNs = fastestCalculation?.calculationTimeNs ?: 0L,
            slowestCalculationTimeNs = slowestCalculation?.calculationTimeNs ?: 0L,
            favoritePhysicsType = _favoritePhysicsType.value
        )
    }
    
    private fun updateFavoritePhysicsType(beta: Int) {
        val history = _calculationHistory.value
        val counts = mapOf(
            1 to history.count { it.beta == 1 }, // Momentum
            2 to history.count { it.beta == 2 }, // Force  
            3 to history.count { it.beta == 3 }  // Energy
        )
        
        val favorite = counts.maxByOrNull { it.value }?.key ?: 2
        _favoritePhysicsType.value = favorite
        preferences.edit().putInt(KEY_FAVORITE_TYPE, favorite).apply()
    }
    
    private fun loadCalculationHistory(): List<PhysicsResult> {
        return try {
            val historyJson = preferences.getString(KEY_CALCULATION_HISTORY, null)
            if (historyJson != null) {
                json.decodeFromString<List<PhysicsResult>>(historyJson)
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    private fun saveCalculationHistory(history: List<PhysicsResult>) {
        try {
            val historyJson = json.encodeToString(history)
            preferences.edit().putString(KEY_CALCULATION_HISTORY, historyJson).apply()
        } catch (e: Exception) {
            // Handle serialization error silently
        }
    }
    
    // App usage tracking
    fun recordAppLaunch() {
        val launches = preferences.getInt(KEY_APP_LAUNCHES, 0) + 1
        preferences.edit().putInt(KEY_APP_LAUNCHES, launches).apply()
        
        val firstLaunch = preferences.getLong(KEY_FIRST_LAUNCH, 0L)
        if (firstLaunch == 0L) {
            preferences.edit().putLong(KEY_FIRST_LAUNCH, System.currentTimeMillis()).apply()
        }
        
        preferences.edit().putLong(KEY_LAST_LAUNCH, System.currentTimeMillis()).apply()
    }
    
    fun getAppUsageStats(): AppUsageStats {
        return AppUsageStats(
            totalLaunches = preferences.getInt(KEY_APP_LAUNCHES, 0),
            firstLaunchDate = preferences.getLong(KEY_FIRST_LAUNCH, 0L),
            lastLaunchDate = preferences.getLong(KEY_LAST_LAUNCH, 0L),
            totalCalculations = _totalCalculations.value
        )
    }
    
    companion object {
        private const val KEY_DARK_MODE = "dark_mode"
        private const val KEY_ANIMATIONS = "animations_enabled"
        private const val KEY_DEFAULT_ALPHA = "default_alpha"
        private const val KEY_DEFAULT_MASS = "default_mass"
        private const val KEY_DEFAULT_ACCELERATION = "default_acceleration"
        private const val KEY_CALCULATION_HISTORY = "calculation_history"
        private const val KEY_TOTAL_CALCULATIONS = "total_calculations"
        private const val KEY_FAVORITE_TYPE = "favorite_physics_type"
        private const val KEY_APP_LAUNCHES = "app_launches"
        private const val KEY_FIRST_LAUNCH = "first_launch_date"
        private const val KEY_LAST_LAUNCH = "last_launch_date"
    }
}

data class CalculationStats(
    val totalCalculations: Int,
    val momentumCalculations: Int,
    val forceCalculations: Int,
    val energyCalculations: Int,
    val averageCalculationTimeNs: Double,
    val fastestCalculationTimeNs: Long,
    val slowestCalculationTimeNs: Long,
    val favoritePhysicsType: Int
)

data class AppUsageStats(
    val totalLaunches: Int,
    val firstLaunchDate: Long,
    val lastLaunchDate: Long,
    val totalCalculations: Int
)
