import com.corridor.os.core.PhysicsCalculator

/**
 * Test program to verify the physics decoder formula implementation
 */
fun main() {
    println("=== Physics Decoder Formula Test ===")
    println("Testing: Q(β) = m·a^(α·δ_β,2)·c^(α·(δ_β,1+δ_β,3))")
    
    val calculator = PhysicsCalculator()
    val testMass = 1.0 // 1 kg
    val testAcceleration = 9.81 // m/s²
    
    println("\n--- Test Cases ---")
    
    // Test case 1: α=1, β=2 → F = ma
    println("Test 1: Force calculation (α=1, β=2)")
    val force = calculator.calculateForce(1.0, testMass, testAcceleration)
    val expectedForce = testMass * testAcceleration
    println("  Calculated: $force N")
    println("  Expected: $expectedForce N")
    println("  Match: ${kotlin.math.abs(force - expectedForce) < 1e-10}")
    
    // Test case 2: α=2, β=3 → E = mc²
    println("\nTest 2: Energy calculation (α=2, β=3)")
    val energy = calculator.calculateEnergy(2.0, testMass)
    val expectedEnergy = testMass * PhysicsCalculator.SPEED_OF_LIGHT_M_S * PhysicsCalculator.SPEED_OF_LIGHT_M_S
    println("  Calculated: ${String.format("%.3e", energy)} J")
    println("  Expected: ${String.format("%.3e", expectedEnergy)} J")
    println("  Match: ${kotlin.math.abs(energy - expectedEnergy) < 1e-6 * expectedEnergy}")
    
    // Test case 3: α=1, β=1 → p = mc
    println("\nTest 3: Momentum calculation (α=1, β=1)")
    val momentum = calculator.calculateMomentum(1.0, testMass)
    val expectedMomentum = testMass * PhysicsCalculator.SPEED_OF_LIGHT_M_S
    println("  Calculated: ${String.format("%.3e", momentum)} kg·m/s")
    println("  Expected: ${String.format("%.3e", expectedMomentum)} kg·m/s")
    println("  Match: ${kotlin.math.abs(momentum - expectedMomentum) < 1e-6 * expectedMomentum}")
    
    // Full validation
    println("\n--- Full Validation ---")
    val validation = calculator.validateDecoderFormula()
    println("Overall validation: ${if (validation.isValid) "PASSED" else "FAILED"}")
    validation.details.forEach { detail ->
        println("  $detail")
    }
    
    println("\n--- Optical Computing Calculations ---")
    
    // Photon energy for 1550nm (standard optical computing wavelength)
    val photonEnergy = calculator.calculatePhotonEnergy(1550.0)
    println("Photon energy (1550nm): ${String.format("%.3e", photonEnergy)} J")
    
    // Optical power for 1 THz photon rate
    val opticalPower = calculator.calculateOpticalPower(1e12, 1550.0)
    println("Optical power (1 THz rate): ${String.format("%.3f", opticalPower)} W")
    
    // Quantum efficiency
    val quantumEfficiency = calculator.calculateQuantumEfficiency(0.1, 1e9)
    println("Quantum efficiency: ${String.format("%.3e", quantumEfficiency)} ops/photon")
    
    println("\n=== Test Complete ===")
}
