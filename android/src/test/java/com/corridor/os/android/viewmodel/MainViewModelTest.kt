package com.corridor.os.android.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.corridor.os.android.physics.PhysicsCalculator
import com.corridor.os.android.physics.PhysicsType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.*
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import org.junit.After
import org.junit.Rule

/**
 * Unit tests for MainViewModel
 * Tests state management, coroutine handling, and business logic
 */
@ExperimentalCoroutinesApi
class MainViewModelTest {
    
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: MainViewModel
    
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = MainViewModel()
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    // ============================================================================
    // INITIAL STATE TESTS
    // ============================================================================
    
    @Test
    fun `initial UI state should have correct defaults`() = runBlocking {
        val uiState = viewModel.uiState.first()
        
        assertFalse("Should not be calculating initially", uiState.isCalculating)
        assertFalse("Should not be validating initially", uiState.isValidating)
        assertFalse("Should not be benchmarking initially", uiState.isBenchmarking)
        assertNull("Should have no error initially", uiState.error)
    }
    
    @Test
    fun `initial physics results should be empty`() = runBlocking {
        val results = viewModel.physicsResults.first()
        assertTrue("Physics results should be empty initially", results.isEmpty())
    }
    
    @Test
    fun `validation should be performed on initialization`() = runBlocking {
        advanceUntilIdle()
        
        val validationResult = viewModel.validationResult.first()
        assertNotNull("Validation result should not be null", validationResult)
        assertTrue("Initial validation should pass", validationResult!!.isValid)
    }
    
    @Test
    fun `performance metrics should be calculated on initialization`() = runBlocking {
        advanceUntilIdle()
        
        val metrics = viewModel.performanceMetrics.first()
        assertNotNull("Performance metrics should not be null", metrics)
        assertTrue("Operations per second should be positive", metrics!!.operationsPerSecond > 0)
    }
    
    // ============================================================================
    // PHYSICS CALCULATION TESTS
    // ============================================================================
    
    @Test
    fun `calculatePhysics should update UI state correctly`() = runBlocking {
        // Start calculation
        viewModel.calculatePhysics(1.0, 2, 1.0, 9.81)
        
        // Should be calculating initially
        var uiState = viewModel.uiState.first()
        assertTrue("Should be calculating", uiState.isCalculating)
        
        // Complete the calculation
        advanceUntilIdle()
        
        // Should not be calculating after completion
        uiState = viewModel.uiState.first()
        assertFalse("Should not be calculating after completion", uiState.isCalculating)
        assertNull("Should have no error after successful calculation", uiState.error)
    }
    
    @Test
    fun `calculatePhysics should add result to physics results`() = runBlocking {
        viewModel.calculatePhysics(1.0, 2, 1.0, 9.81)
        advanceUntilIdle()
        
        val results = viewModel.physicsResults.first()
        assertEquals("Should have 1 result", 1, results.size)
        
        val result = results.first()
        assertEquals("Result should be force calculation", PhysicsType.FORCE, result.physicsType)
        assertEquals("Alpha should be 1.0", 1.0, result.alpha, 1e-10)
        assertEquals("Beta should be 2", 2, result.beta)
        assertEquals("Mass should be 1.0", 1.0, result.mass, 1e-10)
        assertEquals("Acceleration should be 9.81", 9.81, result.acceleration, 1e-10)
    }
    
    @Test
    fun `calculateMomentum should call calculatePhysics with correct parameters`() = runBlocking {
        viewModel.calculateMomentum(1.5, 2.0)
        advanceUntilIdle()
        
        val results = viewModel.physicsResults.first()
        assertEquals("Should have 1 result", 1, results.size)
        
        val result = results.first()
        assertEquals("Should be momentum calculation", PhysicsType.MOMENTUM, result.physicsType)
        assertEquals("Alpha should be 1.5", 1.5, result.alpha, 1e-10)
        assertEquals("Beta should be 1", 1, result.beta)
        assertEquals("Mass should be 2.0", 2.0, result.mass, 1e-10)
        assertEquals("Acceleration should be 0.0", 0.0, result.acceleration, 1e-10)
    }
    
    @Test
    fun `calculateForce should call calculatePhysics with correct parameters`() = runBlocking {
        viewModel.calculateForce(2.0, 1.5, 10.0)
        advanceUntilIdle()
        
        val results = viewModel.physicsResults.first()
        assertEquals("Should have 1 result", 1, results.size)
        
        val result = results.first()
        assertEquals("Should be force calculation", PhysicsType.FORCE, result.physicsType)
        assertEquals("Alpha should be 2.0", 2.0, result.alpha, 1e-10)
        assertEquals("Beta should be 2", 2, result.beta)
        assertEquals("Mass should be 1.5", 1.5, result.mass, 1e-10)
        assertEquals("Acceleration should be 10.0", 10.0, result.acceleration, 1e-10)
    }
    
    @Test
    fun `calculateEnergy should call calculatePhysics with correct parameters`() = runBlocking {
        viewModel.calculateEnergy(2.0, 3.0)
        advanceUntilIdle()
        
        val results = viewModel.physicsResults.first()
        assertEquals("Should have 1 result", 1, results.size)
        
        val result = results.first()
        assertEquals("Should be energy calculation", PhysicsType.ENERGY, result.physicsType)
        assertEquals("Alpha should be 2.0", 2.0, result.alpha, 1e-10)
        assertEquals("Beta should be 3", 3, result.beta)
        assertEquals("Mass should be 3.0", 3.0, result.mass, 1e-10)
        assertEquals("Acceleration should be 0.0", 0.0, result.acceleration, 1e-10)
    }
    
    // ============================================================================
    // ERROR HANDLING TESTS
    // ============================================================================
    
    @Test
    fun `calculatePhysics should handle invalid parameters gracefully`() = runBlocking {
        // Try to calculate with invalid beta
        viewModel.calculatePhysics(1.0, 0, 1.0, 9.81)
        advanceUntilIdle()
        
        val uiState = viewModel.uiState.first()
        assertFalse("Should not be calculating after error", uiState.isCalculating)
        assertNotNull("Should have error message", uiState.error)
        assertTrue("Error should mention invalid beta", uiState.error!!.contains("Beta"))
        
        val results = viewModel.physicsResults.first()
        assertTrue("Should have no results after error", results.isEmpty())
    }
    
    @Test
    fun `calculatePhysics should handle negative mass gracefully`() = runBlocking {
        viewModel.calculatePhysics(1.0, 2, -1.0, 9.81)
        advanceUntilIdle()
        
        val uiState = viewModel.uiState.first()
        assertNotNull("Should have error message", uiState.error)
        assertTrue("Error should mention positive mass", uiState.error!!.contains("positive"))
    }
    
    @Test
    fun `clearError should reset error state`() = runBlocking {
        // Cause an error first
        viewModel.calculatePhysics(1.0, 0, 1.0, 9.81)
        advanceUntilIdle()
        
        var uiState = viewModel.uiState.first()
        assertNotNull("Should have error", uiState.error)
        
        // Clear the error
        viewModel.clearError()
        
        uiState = viewModel.uiState.first()
        assertNull("Error should be cleared", uiState.error)
    }
    
    // ============================================================================
    // RESULTS MANAGEMENT TESTS
    // ============================================================================
    
    @Test
    fun `multiple calculations should be added to results list`() = runBlocking {
        // Add multiple calculations
        viewModel.calculateMomentum(1.0, 1.0)
        advanceUntilIdle()
        viewModel.calculateForce(1.0, 1.0, 9.81)
        advanceUntilIdle()
        viewModel.calculateEnergy(2.0, 1.0)
        advanceUntilIdle()
        
        val results = viewModel.physicsResults.first()
        assertEquals("Should have 3 results", 3, results.size)
        
        // Most recent should be first
        assertEquals("First result should be energy", PhysicsType.ENERGY, results[0].physicsType)
        assertEquals("Second result should be force", PhysicsType.FORCE, results[1].physicsType)
        assertEquals("Third result should be momentum", PhysicsType.MOMENTUM, results[2].physicsType)
    }
    
    @Test
    fun `results list should be limited to 10 items`() = runBlocking {
        // Add 12 calculations
        repeat(12) { index ->
            viewModel.calculateForce(1.0, 1.0, index.toDouble())
            advanceUntilIdle()
        }
        
        val results = viewModel.physicsResults.first()
        assertEquals("Should be limited to 10 results", 10, results.size)
        
        // Most recent should be first (index 11), oldest should be last (index 2)
        assertEquals("Most recent should be first", 11.0, results.first().acceleration, 1e-10)
        assertEquals("Oldest should be last", 2.0, results.last().acceleration, 1e-10)
    }
    
    @Test
    fun `clearResults should empty the results list`() = runBlocking {
        // Add some results first
        viewModel.calculateForce(1.0, 1.0, 9.81)
        advanceUntilIdle()
        
        var results = viewModel.physicsResults.first()
        assertFalse("Should have results before clearing", results.isEmpty())
        
        // Clear results
        viewModel.clearResults()
        
        results = viewModel.physicsResults.first()
        assertTrue("Should have no results after clearing", results.isEmpty())
    }
    
    // ============================================================================
    // VALIDATION TESTS
    // ============================================================================
    
    @Test
    fun `validateDecoderFormula should update validation state`() = runBlocking {
        viewModel.validateDecoderFormula()
        
        // Should be validating initially
        var uiState = viewModel.uiState.first()
        assertTrue("Should be validating", uiState.isValidating)
        
        advanceUntilIdle()
        
        // Should not be validating after completion
        uiState = viewModel.uiState.first()
        assertFalse("Should not be validating after completion", uiState.isValidating)
        
        val validationResult = viewModel.validationResult.first()
        assertNotNull("Validation result should not be null", validationResult)
        assertTrue("Validation should pass", validationResult!!.isValid)
        assertEquals("Should have 3 validation details", 3, validationResult.details.size)
    }
    
    // ============================================================================
    // PERFORMANCE METRICS TESTS
    // ============================================================================
    
    @Test
    fun `calculatePerformanceMetrics should update benchmarking state`() = runBlocking {
        viewModel.calculatePerformanceMetrics()
        
        // Should be benchmarking initially
        var uiState = viewModel.uiState.first()
        assertTrue("Should be benchmarking", uiState.isBenchmarking)
        
        advanceUntilIdle()
        
        // Should not be benchmarking after completion
        uiState = viewModel.uiState.first()
        assertFalse("Should not be benchmarking after completion", uiState.isBenchmarking)
        
        val metrics = viewModel.performanceMetrics.first()
        assertNotNull("Performance metrics should not be null", metrics)
        assertTrue("Operations per second should be positive", metrics!!.operationsPerSecond > 0)
        assertTrue("Average latency should be positive", metrics.averageLatencyNs > 0)
        assertTrue("CPU cores should be at least 1", metrics.cpuCores >= 1)
    }
    
    // ============================================================================
    // OPTICAL COMPUTING CALCULATIONS TESTS
    // ============================================================================
    
    @Test
    fun `calculatePhotonEnergy should return correct values`() {
        val energy1550 = viewModel.calculatePhotonEnergy(1550.0)
        val energy1540 = viewModel.calculatePhotonEnergy(1540.0)
        
        assertTrue("Photon energy should be positive", energy1550 > 0)
        assertTrue("Shorter wavelength should have higher energy", energy1540 > energy1550)
        
        // Check approximate values (E = hc/λ)
        val expectedEnergy1550 = PhysicsCalculator.PLANCK_CONSTANT_J_S * PhysicsCalculator.SPEED_OF_LIGHT_M_S / (1550e-9)
        assertEquals("1550nm photon energy should be correct", expectedEnergy1550, energy1550, expectedEnergy1550 * 0.01)
    }
    
    @Test
    fun `calculateOpticalPower should return correct values`() {
        val photonRate = 1e12 // 1 THz
        val wavelength = 1550.0
        
        val power = viewModel.calculateOpticalPower(photonRate, wavelength)
        
        assertTrue("Optical power should be positive", power > 0)
        
        val expectedPower = photonRate * viewModel.calculatePhotonEnergy(wavelength)
        assertEquals("Optical power should equal rate * energy", expectedPower, power, expectedPower * 0.01)
    }
    
    @Test
    fun `calculateQuantumEfficiency should return reasonable values`() {
        val opticalPower = 0.1 // 100 mW
        val throughput = 1e9 // 1 GOPS
        
        val efficiency = viewModel.calculateQuantumEfficiency(opticalPower, throughput)
        
        assertTrue("Quantum efficiency should be positive", efficiency > 0)
        assertTrue("Quantum efficiency should be finite", efficiency.isFinite())
    }
    
    // ============================================================================
    // RELATIVISTIC CALCULATIONS TESTS
    // ============================================================================
    
    @Test
    fun `calculateLorentzFactor should return correct values`() {
        val testCases = mapOf(
            0.0 to 1.0,   // At rest
            0.1 to 1.005, // 10% speed of light
            0.5 to 1.155, // 50% speed of light
            0.9 to 2.294  // 90% speed of light
        )
        
        testCases.forEach { (velocityFraction, expectedGamma) ->
            val gamma = viewModel.calculateLorentzFactor(velocityFraction)
            assertEquals(
                "Lorentz factor for ${velocityFraction}c should be approximately $expectedGamma",
                expectedGamma,
                gamma,
                0.01
            )
        }
    }
    
    @Test
    fun `calculateTimeDilation should return correct values`() {
        val properTimeNs = 1000.0 // 1 microsecond
        val velocityFraction = 0.5 // 50% speed of light
        
        val dilatedTime = viewModel.calculateTimeDilation(properTimeNs, velocityFraction)
        val expectedGamma = viewModel.calculateLorentzFactor(velocityFraction)
        val expectedDilation = properTimeNs * expectedGamma * 1e-9 // Convert to seconds
        
        assertEquals("Time dilation should be correct", expectedDilation, dilatedTime, expectedDilation * 0.01)
        assertTrue("Dilated time should be greater than proper time", dilatedTime > properTimeNs * 1e-9)
    }
    
    // ============================================================================
    // CONCURRENT OPERATIONS TESTS
    // ============================================================================
    
    @Test
    fun `concurrent calculations should be handled properly`() = runBlocking {
        // Start multiple calculations simultaneously
        viewModel.calculateMomentum(1.0, 1.0)
        viewModel.calculateForce(1.0, 1.0, 9.81)
        viewModel.calculateEnergy(2.0, 1.0)
        
        advanceUntilIdle()
        
        val results = viewModel.physicsResults.first()
        assertEquals("Should have 3 results from concurrent calculations", 3, results.size)
        
        val uiState = viewModel.uiState.first()
        assertFalse("Should not be calculating after all complete", uiState.isCalculating)
        assertNull("Should have no errors from concurrent calculations", uiState.error)
    }
    
    @Test
    fun `validation and calculation should not interfere`() = runBlocking {
        // Start validation and calculation simultaneously
        viewModel.validateDecoderFormula()
        viewModel.calculateForce(1.0, 1.0, 9.81)
        
        advanceUntilIdle()
        
        val uiState = viewModel.uiState.first()
        assertFalse("Should not be validating after completion", uiState.isValidating)
        assertFalse("Should not be calculating after completion", uiState.isCalculating)
        
        val validationResult = viewModel.validationResult.first()
        assertNotNull("Validation should complete", validationResult)
        assertTrue("Validation should pass", validationResult!!.isValid)
        
        val results = viewModel.physicsResults.first()
        assertEquals("Calculation should complete", 1, results.size)
    }
}
