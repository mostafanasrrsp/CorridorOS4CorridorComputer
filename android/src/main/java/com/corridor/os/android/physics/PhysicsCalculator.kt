package com.corridor.os.android.physics

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlin.math.*

/**
 * Android-compatible Physics Calculator implementing the unified decoder formula
 * 
 * Decoder Formula: Q(β) = m · a^(α·δ_β,2) · c^(α·(δ_β,1+δ_β,3))
 * Where:
 * - δ_β,i is the Kronecker delta (1 if β=i, else 0)
 * - Q(1) = p (momentum), Q(2) = F (force), Q(3) = E (energy)
 */
class PhysicsCalculator {
    
    companion object {
        // Physics constants
        const val SPEED_OF_LIGHT_M_S = 299_792_458.0
        const val PLANCK_CONSTANT_J_S = 6.62607015e-34
        const val PLANCK_REDUCED_H_BAR = PLANCK_CONSTANT_J_S / (2 * PI)
        const val ELEMENTARY_CHARGE_C = 1.602176634e-19
        const val ELECTRON_MASS_KG = 9.1093837015e-31
        const val PROTON_MASS_KG = 1.67262192369e-27
        const val AVOGADRO_NUMBER = 6.02214076e23
        const val BOLTZMANN_CONSTANT_J_K = 1.380649e-23
        
        // Optical computing constants
        const val PHOTON_ENERGY_1550NM_J = PLANCK_CONSTANT_J_S * SPEED_OF_LIGHT_M_S / 1550e-9
        const val OPTICAL_POWER_DENSITY_W_M2 = 1e12
    }
    
    /**
     * Kronecker delta function
     */
    private fun kroneckerDelta(i: Int, j: Int): Double = if (i == j) 1.0 else 0.0
    
    /**
     * Core decoder formula implementation
     * Q(β) = m · a^(α·δ_β,2) · c^(α·(δ_β,1+δ_β,3))
     * 
     * Optimized version with precomputed constants and minimal object allocation
     */
    fun calculatePhysicsQuantity(alpha: Double, beta: Int, mass: Double, acceleration: Double): PhysicsResult {
        require(beta in 1..3) { "Beta must be 1 (momentum), 2 (force), or 3 (energy)" }
        require(mass > 0) { "Mass must be positive" }
        require(alpha.isFinite() && mass.isFinite() && acceleration.isFinite()) { "All parameters must be finite numbers" }
        
        val startTime = System.nanoTime()
        
        // Optimized calculation using direct conditional logic instead of Kronecker deltas
        val result = when (beta) {
            1 -> { // Momentum: p = m * c^α
                mass * SPEED_OF_LIGHT_M_S.pow(alpha)
            }
            2 -> { // Force: F = m * a^α
                mass * acceleration.pow(alpha)
            }
            3 -> { // Energy: E = m * c^α
                mass * SPEED_OF_LIGHT_M_S.pow(alpha)
            }
            else -> throw IllegalArgumentException("Invalid beta value: $beta")
        }
        
        val calculationTime = System.nanoTime() - startTime
        
        val physicsType = when (beta) {
            1 -> PhysicsType.MOMENTUM
            2 -> PhysicsType.FORCE
            3 -> PhysicsType.ENERGY
            else -> PhysicsType.UNKNOWN
        }
        
        val unit = when (beta) {
            1 -> "kg⋅m/s"
            2 -> "N"
            3 -> "J"
            else -> "unknown"
        }
        
        return PhysicsResult(
            value = result,
            unit = unit,
            physicsType = physicsType,
            alpha = alpha,
            beta = beta,
            mass = mass,
            acceleration = acceleration,
            calculationTimeNs = calculationTime
        )
    }
    
    /**
     * Calculate momentum: Q(1) = m·c^α
     */
    fun calculateMomentum(alpha: Double, mass: Double): PhysicsResult {
        return calculatePhysicsQuantity(alpha, 1, mass, 0.0)
    }
    
    /**
     * Calculate force: Q(2) = m·a^α
     */
    fun calculateForce(alpha: Double, mass: Double, acceleration: Double): PhysicsResult {
        return calculatePhysicsQuantity(alpha, 2, mass, acceleration)
    }
    
    /**
     * Calculate energy: Q(3) = m·c^α
     */
    fun calculateEnergy(alpha: Double, mass: Double): PhysicsResult {
        return calculatePhysicsQuantity(alpha, 3, mass, 0.0)
    }
    
    /**
     * Calculate photon energy for given wavelength
     * E = hf = hc/λ
     */
    fun calculatePhotonEnergy(wavelengthNm: Double): Double {
        val wavelengthM = wavelengthNm * 1e-9
        return PLANCK_CONSTANT_J_S * SPEED_OF_LIGHT_M_S / wavelengthM
    }
    
    /**
     * Calculate optical power
     * P = N·E_photon where N is photon rate
     */
    fun calculateOpticalPower(photonRate: Double, wavelengthNm: Double): Double {
        val photonEnergy = calculatePhotonEnergy(wavelengthNm)
        return photonRate * photonEnergy
    }
    
    /**
     * Calculate quantum efficiency
     */
    fun calculateQuantumEfficiency(opticalPowerW: Double, computationalThroughputOps: Double): Double {
        val photonsPerSecond = opticalPowerW / PHOTON_ENERGY_1550NM_J
        return computationalThroughputOps / photonsPerSecond
    }
    
    /**
     * Calculate Lorentz factor for relativistic effects
     * γ = 1/√(1 - v²/c²)
     */
    fun calculateLorentzFactor(velocity: Double): Double {
        require(abs(velocity) < SPEED_OF_LIGHT_M_S) { "Velocity must be less than speed of light" }
        val beta = velocity / SPEED_OF_LIGHT_M_S
        return 1.0 / sqrt(1.0 - beta * beta)
    }
    
    /**
     * Calculate time dilation
     * Δt' = γ·Δt
     */
    fun calculateTimeDilation(properTime: Double, velocity: Double): Double {
        val gamma = calculateLorentzFactor(velocity)
        return gamma * properTime
    }
    
    /**
     * Validate decoder formula against known physics equations
     */
    fun validateDecoderFormula(): ValidationResult {
        val mass = 1.0
        val acceleration = 9.81
        val tolerance = 1e-10
        
        val results = mutableListOf<String>()
        var allValid = true
        
        // Test F = ma
        val forceResult = calculateForce(1.0, mass, acceleration)
        val expectedForce = mass * acceleration
        if (abs(forceResult.value - expectedForce) < tolerance) {
            results.add("✓ Force calculation (F = ma): PASSED")
        } else {
            results.add("✗ Force calculation (F = ma): FAILED")
            allValid = false
        }
        
        // Test E = mc²
        val energyResult = calculateEnergy(2.0, mass)
        val expectedEnergy = mass * SPEED_OF_LIGHT_M_S * SPEED_OF_LIGHT_M_S
        if (abs(energyResult.value - expectedEnergy) < tolerance * expectedEnergy) {
            results.add("✓ Energy calculation (E = mc²): PASSED")
        } else {
            results.add("✗ Energy calculation (E = mc²): FAILED")
            allValid = false
        }
        
        // Test p = mc
        val momentumResult = calculateMomentum(1.0, mass)
        val expectedMomentum = mass * SPEED_OF_LIGHT_M_S
        if (abs(momentumResult.value - expectedMomentum) < tolerance * expectedMomentum) {
            results.add("✓ Momentum calculation (p = mc): PASSED")
        } else {
            results.add("✗ Momentum calculation (p = mc): FAILED")
            allValid = false
        }
        
        return ValidationResult(allValid, results)
    }
    
    /**
     * Calculate theoretical mobile performance metrics
     */
    fun calculateMobilePerformanceMetrics(): MobilePerformanceMetrics {
        val startTime = System.nanoTime()
        
        // Run performance test
        val iterations = 100000
        repeat(iterations) {
            calculatePhysicsQuantity(1.0, 2, 1.0, 9.81)
        }
        
        val totalTime = System.nanoTime() - startTime
        val operationsPerSecond = (iterations * 1_000_000_000L) / totalTime
        val averageLatencyNs = totalTime.toDouble() / iterations
        
        // Estimate mobile capabilities
        val estimatedOpticalSpeedup = 100.0 // Theoretical optical advantage
        val mobileCpuCores = Runtime.getRuntime().availableProcessors()
        val memoryMB = Runtime.getRuntime().maxMemory() / (1024 * 1024)
        
        return MobilePerformanceMetrics(
            operationsPerSecond = operationsPerSecond,
            averageLatencyNs = averageLatencyNs,
            cpuCores = mobileCpuCores,
            availableMemoryMB = memoryMB,
            theoreticalOpticalSpeedup = estimatedOpticalSpeedup,
            calculationTimeMs = totalTime / 1_000_000.0
        )
    }
}

/**
 * Physics calculation result with Android Parcelable support
 */
@Parcelize
data class PhysicsResult(
    val value: Double,
    val unit: String,
    val physicsType: PhysicsType,
    val alpha: Double,
    val beta: Int,
    val mass: Double,
    val acceleration: Double,
    val calculationTimeNs: Long
) : Parcelable

/**
 * Physics types enumeration
 */
enum class PhysicsType {
    MOMENTUM, FORCE, ENERGY, UNKNOWN
}

/**
 * Validation result
 */
data class ValidationResult(
    val isValid: Boolean,
    val details: List<String>
)

/**
 * Mobile performance metrics
 */
@Parcelize
data class MobilePerformanceMetrics(
    val operationsPerSecond: Long,
    val averageLatencyNs: Double,
    val cpuCores: Int,
    val availableMemoryMB: Long,
    val theoreticalOpticalSpeedup: Double,
    val calculationTimeMs: Double
) : Parcelable
