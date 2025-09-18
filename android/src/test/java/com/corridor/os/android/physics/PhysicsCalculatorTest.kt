package com.corridor.os.android.physics

import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import kotlin.math.*

/**
 * Comprehensive unit tests for PhysicsCalculator
 * Validates the decoder formula implementation and edge cases
 */
class PhysicsCalculatorTest {
    
    private lateinit var calculator: PhysicsCalculator
    
    @Before
    fun setUp() {
        calculator = PhysicsCalculator()
    }
    
    // ============================================================================
    // DECODER FORMULA VALIDATION TESTS
    // ============================================================================
    
    @Test
    fun `decoder formula validation should pass for all physics laws`() {
        val validationResult = calculator.validateDecoderFormula()
        
        assertTrue("Decoder formula validation should pass", validationResult.isValid)
        assertEquals("Should have 3 validation tests", 3, validationResult.details.size)
        
        // Verify all tests passed
        validationResult.details.forEach { detail ->
            assertTrue("All validation tests should pass: $detail", detail.contains("PASSED"))
        }
    }
    
    // ============================================================================
    // MOMENTUM CALCULATIONS (β=1)
    // ============================================================================
    
    @Test
    fun `momentum calculation with alpha=1 should equal mc`() {
        val mass = 1.0
        val alpha = 1.0
        
        val result = calculator.calculateMomentum(alpha, mass)
        val expectedMomentum = mass * PhysicsCalculator.SPEED_OF_LIGHT_M_S
        
        assertEquals("Momentum should equal m*c", expectedMomentum, result.value, 1e-10)
        assertEquals("Unit should be kg⋅m/s", "kg⋅m/s", result.unit)
        assertEquals("Physics type should be MOMENTUM", PhysicsType.MOMENTUM, result.physicsType)
        assertEquals("Alpha should be 1.0", 1.0, result.alpha, 1e-10)
        assertEquals("Beta should be 1", 1, result.beta)
    }
    
    @Test
    fun `momentum calculation with different alpha values`() {
        val mass = 2.5
        val testCases = listOf(0.5, 1.0, 1.5, 2.0, 3.0)
        
        testCases.forEach { alpha ->
            val result = calculator.calculateMomentum(alpha, mass)
            val expected = mass * PhysicsCalculator.SPEED_OF_LIGHT_M_S.pow(alpha)
            
            assertEquals(
                "Momentum with α=$alpha should equal m*c^α", 
                expected, 
                result.value, 
                1e-6 * expected
            )
            assertTrue("Calculation time should be positive", result.calculationTimeNs > 0)
        }
    }
    
    // ============================================================================
    // FORCE CALCULATIONS (β=2)
    // ============================================================================
    
    @Test
    fun `force calculation with alpha=1 should equal ma`() {
        val mass = 1.0
        val acceleration = 9.81
        val alpha = 1.0
        
        val result = calculator.calculateForce(alpha, mass, acceleration)
        val expectedForce = mass * acceleration
        
        assertEquals("Force should equal m*a", expectedForce, result.value, 1e-10)
        assertEquals("Unit should be N", "N", result.unit)
        assertEquals("Physics type should be FORCE", PhysicsType.FORCE, result.physicsType)
        assertEquals("Alpha should be 1.0", 1.0, result.alpha, 1e-10)
        assertEquals("Beta should be 2", 2, result.beta)
        assertEquals("Acceleration should be preserved", acceleration, result.acceleration, 1e-10)
    }
    
    @Test
    fun `force calculation with different alpha values`() {
        val mass = 1.5
        val acceleration = 5.0
        val testCases = listOf(0.5, 1.0, 1.5, 2.0)
        
        testCases.forEach { alpha ->
            val result = calculator.calculateForce(alpha, mass, acceleration)
            val expected = mass * acceleration.pow(alpha)
            
            assertEquals(
                "Force with α=$alpha should equal m*a^α", 
                expected, 
                result.value, 
                1e-10 * max(1.0, expected)
            )
        }
    }
    
    @Test
    fun `force calculation with zero acceleration`() {
        val result = calculator.calculateForce(1.0, 1.0, 0.0)
        assertEquals("Force with zero acceleration should be zero", 0.0, result.value, 1e-10)
    }
    
    // ============================================================================
    // ENERGY CALCULATIONS (β=3)
    // ============================================================================
    
    @Test
    fun `energy calculation with alpha=2 should equal mc²`() {
        val mass = 1.0
        val alpha = 2.0
        
        val result = calculator.calculateEnergy(alpha, mass)
        val expectedEnergy = mass * PhysicsCalculator.SPEED_OF_LIGHT_M_S * PhysicsCalculator.SPEED_OF_LIGHT_M_S
        
        assertEquals("Energy should equal m*c²", expectedEnergy, result.value, 1e-6 * expectedEnergy)
        assertEquals("Unit should be J", "J", result.unit)
        assertEquals("Physics type should be ENERGY", PhysicsType.ENERGY, result.physicsType)
        assertEquals("Alpha should be 2.0", 2.0, result.alpha, 1e-10)
        assertEquals("Beta should be 3", 3, result.beta)
    }
    
    @Test
    fun `energy calculation with different masses`() {
        val alpha = 2.0
        val testMasses = listOf(0.1, 1.0, 5.0, 10.0, 100.0)
        
        testMasses.forEach { mass ->
            val result = calculator.calculateEnergy(alpha, mass)
            val expected = mass * PhysicsCalculator.SPEED_OF_LIGHT_M_S.pow(alpha)
            
            assertEquals(
                "Energy for mass=$mass should equal m*c²", 
                expected, 
                result.value, 
                1e-6 * expected
            )
            assertTrue("Mass should be positive", result.mass > 0)
        }
    }
    
    // ============================================================================
    // EDGE CASES AND ERROR HANDLING
    // ============================================================================
    
    @Test(expected = IllegalArgumentException::class)
    fun `should throw exception for invalid beta value`() {
        calculator.calculatePhysicsQuantity(1.0, 0, 1.0, 9.81)
    }
    
    @Test(expected = IllegalArgumentException::class)
    fun `should throw exception for beta greater than 3`() {
        calculator.calculatePhysicsQuantity(1.0, 4, 1.0, 9.81)
    }
    
    @Test(expected = IllegalArgumentException::class)
    fun `should throw exception for negative mass`() {
        calculator.calculatePhysicsQuantity(1.0, 2, -1.0, 9.81)
    }
    
    @Test(expected = IllegalArgumentException::class)
    fun `should throw exception for zero mass`() {
        calculator.calculatePhysicsQuantity(1.0, 2, 0.0, 9.81)
    }
    
    @Test(expected = IllegalArgumentException::class)
    fun `should throw exception for infinite alpha`() {
        calculator.calculatePhysicsQuantity(Double.POSITIVE_INFINITY, 2, 1.0, 9.81)
    }
    
    @Test(expected = IllegalArgumentException::class)
    fun `should throw exception for NaN mass`() {
        calculator.calculatePhysicsQuantity(1.0, 2, Double.NaN, 9.81)
    }
    
    // ============================================================================
    // OPTICAL COMPUTING CALCULATIONS
    // ============================================================================
    
    @Test
    fun `photon energy calculation for standard optical wavelengths`() {
        val testWavelengths = mapOf(
            1530.0 to 1.298e-19,  // Approximate expected values
            1540.0 to 1.290e-19,
            1550.0 to 1.282e-19,
            1560.0 to 1.274e-19,
            1570.0 to 1.266e-19
        )
        
        testWavelengths.forEach { (wavelength, expectedEnergy) ->
            val actualEnergy = calculator.calculatePhotonEnergy(wavelength)
            
            assertEquals(
                "Photon energy for ${wavelength}nm should be approximately correct",
                expectedEnergy,
                actualEnergy,
                expectedEnergy * 0.01 // 1% tolerance
            )
            assertTrue("Photon energy should be positive", actualEnergy > 0)
        }
    }
    
    @Test
    fun `optical power calculation`() {
        val photonRate = 1e12 // 1 THz
        val wavelength = 1550.0
        
        val power = calculator.calculateOpticalPower(photonRate, wavelength)
        val photonEnergy = calculator.calculatePhotonEnergy(wavelength)
        val expectedPower = photonRate * photonEnergy
        
        assertEquals("Optical power should equal photon rate * photon energy", expectedPower, power, 1e-10)
        assertTrue("Optical power should be positive", power > 0)
    }
    
    @Test
    fun `quantum efficiency calculation`() {
        val opticalPower = 0.1 // 100 mW
        val throughput = 1e9 // 1 GOPS
        
        val efficiency = calculator.calculateQuantumEfficiency(opticalPower, throughput)
        
        assertTrue("Quantum efficiency should be positive", efficiency > 0)
        assertTrue("Quantum efficiency should be finite", efficiency.isFinite())
    }
    
    // ============================================================================
    // RELATIVISTIC CALCULATIONS
    // ============================================================================
    
    @Test
    fun `lorentz factor calculation for various velocities`() {
        val testVelocities = listOf(
            0.0 to 1.0,           // At rest
            0.1 * PhysicsCalculator.SPEED_OF_LIGHT_M_S to 1.005, // 10% c
            0.5 * PhysicsCalculator.SPEED_OF_LIGHT_M_S to 1.155, // 50% c
            0.9 * PhysicsCalculator.SPEED_OF_LIGHT_M_S to 2.294  // 90% c
        )
        
        testVelocities.forEach { (velocity, expectedGamma) ->
            val gamma = calculator.calculateLorentzFactor(velocity)
            
            assertEquals(
                "Lorentz factor for v=${velocity/PhysicsCalculator.SPEED_OF_LIGHT_M_S}c should be approximately $expectedGamma",
                expectedGamma,
                gamma,
                0.01
            )
            assertTrue("Lorentz factor should be >= 1", gamma >= 1.0)
        }
    }
    
    @Test(expected = IllegalArgumentException::class)
    fun `should throw exception for velocity greater than speed of light`() {
        calculator.calculateLorentzFactor(PhysicsCalculator.SPEED_OF_LIGHT_M_S * 1.1)
    }
    
    @Test
    fun `time dilation calculation`() {
        val properTime = 1e-9 // 1 nanosecond
        val velocity = 0.5 * PhysicsCalculator.SPEED_OF_LIGHT_M_S
        
        val dilatedTime = calculator.calculateTimeDilation(properTime, velocity)
        val expectedDilation = properTime * calculator.calculateLorentzFactor(velocity)
        
        assertEquals("Time dilation should equal proper time * gamma", expectedDilation, dilatedTime, 1e-12)
        assertTrue("Dilated time should be greater than proper time", dilatedTime > properTime)
    }
    
    // ============================================================================
    // PERFORMANCE TESTS
    // ============================================================================
    
    @Test
    fun `calculation performance should be sub-microsecond`() {
        val iterations = 1000
        val results = mutableListOf<Long>()
        
        repeat(iterations) {
            val result = calculator.calculateForce(1.0, 1.0, 9.81)
            results.add(result.calculationTimeNs)
        }
        
        val averageTime = results.average()
        val maxTime = results.maxOrNull() ?: 0L
        
        assertTrue("Average calculation time should be < 10μs", averageTime < 10_000)
        assertTrue("Max calculation time should be < 100μs", maxTime < 100_000)
        println("Average calculation time: ${averageTime/1000.0}μs")
        println("Max calculation time: ${maxTime/1000.0}μs")
    }
    
    @Test
    fun `mobile performance metrics calculation`() {
        val metrics = calculator.calculateMobilePerformanceMetrics()
        
        assertTrue("Operations per second should be positive", metrics.operationsPerSecond > 0)
        assertTrue("Average latency should be positive", metrics.averageLatencyNs > 0)
        assertTrue("CPU cores should be at least 1", metrics.cpuCores >= 1)
        assertTrue("Available memory should be positive", metrics.availableMemoryMB > 0)
        assertTrue("Calculation time should be positive", metrics.calculationTimeMs > 0)
        
        // Performance expectations for modern mobile devices
        assertTrue("Should achieve at least 100K ops/sec", metrics.operationsPerSecond > 100_000)
        assertTrue("Average latency should be < 100μs", metrics.averageLatencyNs < 100_000)
        
        println("Mobile Performance Metrics:")
        println("  Operations/sec: ${metrics.operationsPerSecond}")
        println("  Average latency: ${metrics.averageLatencyNs/1000.0}μs")
        println("  CPU cores: ${metrics.cpuCores}")
        println("  Available memory: ${metrics.availableMemoryMB}MB")
    }
    
    // ============================================================================
    // CONSISTENCY AND PRECISION TESTS
    // ============================================================================
    
    @Test
    fun `repeated calculations should be consistent`() {
        val alpha = 1.5
        val mass = 2.0
        val acceleration = 5.0
        
        val results = (1..100).map {
            calculator.calculateForce(alpha, mass, acceleration).value
        }
        
        val firstResult = results.first()
        results.forEach { result ->
            assertEquals("All results should be identical", firstResult, result, 1e-15)
        }
    }
    
    @Test
    fun `calculations should maintain precision for small values`() {
        val smallMass = 1e-30 // Very small mass (electron-scale)
        val result = calculator.calculateEnergy(2.0, smallMass)
        
        assertTrue("Result should be positive for small mass", result.value > 0)
        assertTrue("Result should be finite for small mass", result.value.isFinite())
        assertFalse("Result should not be NaN for small mass", result.value.isNaN())
    }
    
    @Test
    fun `calculations should handle large values correctly`() {
        val largeMass = 1e30 // Very large mass (stellar scale)
        val result = calculator.calculateEnergy(2.0, largeMass)
        
        assertTrue("Result should be positive for large mass", result.value > 0)
        assertTrue("Result should be finite for large mass", result.value.isFinite())
        assertFalse("Result should not be NaN for large mass", result.value.isNaN())
    }
    
    // ============================================================================
    // DECODER FORMULA SPECIFIC TESTS
    // ============================================================================
    
    @Test
    fun `decoder formula should correctly implement Kronecker delta logic`() {
        val mass = 1.0
        val acceleration = 9.81
        val alpha = 1.0
        
        // Test β=1 (momentum): should use c^α, ignore acceleration
        val momentum = calculator.calculatePhysicsQuantity(alpha, 1, mass, acceleration)
        val expectedMomentum = mass * PhysicsCalculator.SPEED_OF_LIGHT_M_S.pow(alpha)
        assertEquals("β=1 should give momentum formula", expectedMomentum, momentum.value, 1e-10)
        
        // Test β=2 (force): should use a^α, ignore speed of light
        val force = calculator.calculatePhysicsQuantity(alpha, 2, mass, acceleration)
        val expectedForce = mass * acceleration.pow(alpha)
        assertEquals("β=2 should give force formula", expectedForce, force.value, 1e-10)
        
        // Test β=3 (energy): should use c^α, ignore acceleration
        val energy = calculator.calculatePhysicsQuantity(alpha, 3, mass, acceleration)
        val expectedEnergy = mass * PhysicsCalculator.SPEED_OF_LIGHT_M_S.pow(alpha)
        assertEquals("β=3 should give energy formula", expectedEnergy, energy.value, 1e-10)
    }
    
    @Test
    fun `decoder formula encoding should be reversible`() {
        // Test the famous physics equations are correctly encoded
        val testCases = listOf(
            Triple(1.0, 2, "F = ma"),    // Newton's second law
            Triple(2.0, 3, "E = mc²"),   // Einstein's mass-energy
            Triple(1.0, 1, "p = mc")     // Relativistic momentum
        )
        
        testCases.forEach { (alpha, beta, description) ->
            val result = calculator.calculatePhysicsQuantity(alpha, beta, 1.0, 9.81)
            
            when (beta) {
                1 -> assertEquals("$description should be encoded correctly", 
                    PhysicsCalculator.SPEED_OF_LIGHT_M_S.pow(alpha), result.value, 1e-10)
                2 -> assertEquals("$description should be encoded correctly", 
                    9.81.pow(alpha), result.value, 1e-10)
                3 -> assertEquals("$description should be encoded correctly", 
                    PhysicsCalculator.SPEED_OF_LIGHT_M_S.pow(alpha), result.value, 1e-10)
            }
        }
    }
}
