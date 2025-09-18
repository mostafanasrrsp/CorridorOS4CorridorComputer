package com.corridor.os.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corridor.os.android.physics.PhysicsCalculator
import com.corridor.os.android.physics.PhysicsResult
import com.corridor.os.android.physics.ValidationResult
import com.corridor.os.android.physics.MobilePerformanceMetrics
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    
    private val physicsCalculator = PhysicsCalculator()
    
    // UI State
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()
    
    // Physics calculation state
    private val _physicsResults = MutableStateFlow<List<PhysicsResult>>(emptyList())
    val physicsResults: StateFlow<List<PhysicsResult>> = _physicsResults.asStateFlow()
    
    // Validation state
    private val _validationResult = MutableStateFlow<ValidationResult?>(null)
    val validationResult: StateFlow<ValidationResult?> = _validationResult.asStateFlow()
    
    // Performance metrics
    private val _performanceMetrics = MutableStateFlow<MobilePerformanceMetrics?>(null)
    val performanceMetrics: StateFlow<MobilePerformanceMetrics?> = _performanceMetrics.asStateFlow()
    
    init {
        // Initialize with validation
        validateDecoderFormula()
        calculatePerformanceMetrics()
    }
    
    fun calculatePhysics(alpha: Double, beta: Int, mass: Double, acceleration: Double) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isCalculating = true, error = null)
                
                val result = physicsCalculator.calculatePhysicsQuantity(alpha, beta, mass, acceleration)
                
                // Add to results list
                val currentResults = _physicsResults.value.toMutableList()
                currentResults.add(0, result) // Add to beginning
                
                // Keep only last 10 results
                if (currentResults.size > 10) {
                    currentResults.removeAt(currentResults.size - 1)
                }
                
                _physicsResults.value = currentResults
                _uiState.value = _uiState.value.copy(isCalculating = false)
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isCalculating = false,
                    error = e.message ?: "Unknown error"
                )
            }
        }
    }
    
    fun calculateMomentum(alpha: Double, mass: Double) {
        calculatePhysics(alpha, 1, mass, 0.0)
    }
    
    fun calculateForce(alpha: Double, mass: Double, acceleration: Double) {
        calculatePhysics(alpha, 2, mass, acceleration)
    }
    
    fun calculateEnergy(alpha: Double, mass: Double) {
        calculatePhysics(alpha, 3, mass, 0.0)
    }
    
    fun validateDecoderFormula() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isValidating = true)
                
                val result = physicsCalculator.validateDecoderFormula()
                _validationResult.value = result
                
                _uiState.value = _uiState.value.copy(isValidating = false)
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isValidating = false,
                    error = e.message ?: "Validation error"
                )
            }
        }
    }
    
    fun calculatePerformanceMetrics() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isBenchmarking = true)
                
                val metrics = physicsCalculator.calculateMobilePerformanceMetrics()
                _performanceMetrics.value = metrics
                
                _uiState.value = _uiState.value.copy(isBenchmarking = false)
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isBenchmarking = false,
                    error = e.message ?: "Benchmark error"
                )
            }
        }
    }
    
    fun clearResults() {
        _physicsResults.value = emptyList()
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    // Optical computing calculations
    fun calculatePhotonEnergy(wavelengthNm: Double): Double {
        return physicsCalculator.calculatePhotonEnergy(wavelengthNm)
    }
    
    fun calculateOpticalPower(photonRate: Double, wavelengthNm: Double): Double {
        return physicsCalculator.calculateOpticalPower(photonRate, wavelengthNm)
    }
    
    fun calculateQuantumEfficiency(opticalPowerW: Double, throughputOps: Double): Double {
        return physicsCalculator.calculateQuantumEfficiency(opticalPowerW, throughputOps)
    }
    
    // Relativistic calculations
    fun calculateLorentzFactor(velocityFraction: Double): Double {
        val velocity = velocityFraction * PhysicsCalculator.SPEED_OF_LIGHT_M_S
        return physicsCalculator.calculateLorentzFactor(velocity)
    }
    
    fun calculateTimeDilation(properTimeNs: Double, velocityFraction: Double): Double {
        val velocity = velocityFraction * PhysicsCalculator.SPEED_OF_LIGHT_M_S
        val properTimeS = properTimeNs * 1e-9
        return physicsCalculator.calculateTimeDilation(properTimeS, velocity)
    }
}

data class MainUiState(
    val isCalculating: Boolean = false,
    val isValidating: Boolean = false,
    val isBenchmarking: Boolean = false,
    val error: String? = null
)
