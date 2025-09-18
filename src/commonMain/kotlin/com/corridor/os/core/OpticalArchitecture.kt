package com.corridor.os.core

import kotlinx.serialization.Serializable

/**
 * Core optical architecture definitions for the Corridor Computer OS
 */

/**
 * Optical Processing Unit (OPU) specifications
 */
@Serializable
data class OpticalProcessingUnit(
    val wavelengthChannels: Int = 32,  // Number of wavelength channels for parallel processing
    val clockFrequencyTHz: Double = 1.0,  // Base optical clock frequency in THz
    val logicGateType: OpticalLogicGateType = OpticalLogicGateType.NONLINEAR_OPTICAL,
    val parallelismFactor: Int = 64,  // Parallel processing lanes
    val thermalDesignPowerWatts: Double = 15.0  // Much lower than traditional CPUs
)

enum class OpticalLogicGateType {
    NONLINEAR_OPTICAL,
    PHOTONIC_CRYSTAL,
    SILICON_PHOTONIC,
    HYBRID_ELECTRO_OPTICAL
}

/**
 * Optical Instruction Set Architecture (OISA)
 * Maps to x86/x64 for backwards compatibility
 */
@Serializable
data class OpticalInstruction(
    val opcode: UInt,
    val wavelength: Double,  // Operating wavelength in nm
    val parallelLanes: Int,
    val x86Equivalent: String? = null,  // For compatibility mapping
    val executionTimePs: Long  // Execution time in picoseconds
)

/**
 * Optical Register definitions
 */
enum class OpticalRegister(val wavelengthNm: Double, val capacity: Int) {
    // General purpose optical registers
    OAX(1550.0, 64),  // Primary accumulator
    OBX(1551.0, 64),  // Base register
    OCX(1552.0, 64),  // Counter register
    ODX(1553.0, 64),  // Data register
    
    // Wavelength division multiplexing registers
    WDM0(1540.0, 128),
    WDM1(1541.0, 128),
    WDM2(1542.0, 128),
    WDM3(1543.0, 128),
    
    // Control registers
    OPC(1560.0, 64),   // Optical Program Counter
    OSP(1561.0, 64),   // Optical Stack Pointer
    OSR(1562.0, 32)    // Optical Status Register
}

/**
 * Memory architecture for optical systems
 */
@Serializable
data class OpticalMemorySpec(
    val type: OpticalMemoryType,
    val capacityGB: Long,
    val accessTimePs: Long,  // Access time in picoseconds
    val bandwidthGbps: Double,
    val wavelengthRange: WavelengthRange
)

enum class OpticalMemoryType {
    HOLOGRAPHIC_RAM,
    OPTICAL_DELAY_LINE,
    PHASE_CHANGE_OPTICAL,
    HYBRID_ELECTRO_OPTICAL
}

@Serializable
data class WavelengthRange(
    val startNm: Double,
    val endNm: Double,
    val channels: Int
)

/**
 * Ejectable ROM specifications
 */
@Serializable
data class EjectableROMSpec(
    val type: ROMType,
    val capacityGB: Long,
    val readSpeedGbps: Double,
    val opticalInterface: OpticalInterface,
    val mechanicalInterface: MechanicalInterface
)

enum class ROMType {
    HOLOGRAPHIC_DISK,
    PHASE_CHANGE_OPTICAL,
    OPTICAL_CRYSTAL,
    LAYERED_OPTICAL_STORAGE
}

@Serializable
data class OpticalInterface(
    val wavelengthNm: Double,
    val couplingType: String,  // "waveguide", "free_space", "fiber"
    val alignmentToleranceUm: Double
)

@Serializable
data class MechanicalInterface(
    val connectorType: String,
    val insertionForceN: Double,
    val alignmentMechanism: String
)

/**
 * System architecture constants
 */
object OpticalConstants {
    const val SPEED_OF_LIGHT_M_S = 299_792_458.0
    const val PLANCK_CONSTANT_J_S = 6.62607015e-34
    const val DEFAULT_WAVELENGTH_NM = 1550.0  // C-band for telecom compatibility
    const val MAX_PARALLEL_LANES = 128
    const val MIN_CLOCK_FREQUENCY_THZ = 0.1
    const val MAX_CLOCK_FREQUENCY_THZ = 10.0
}

