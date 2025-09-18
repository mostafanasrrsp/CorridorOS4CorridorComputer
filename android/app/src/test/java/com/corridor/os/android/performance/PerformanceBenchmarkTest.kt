package com.corridor.os.android.performance

import com.corridor.os.android.physics.PhysicsCalculator
import com.corridor.os.android.physics.PhysicsType
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import kotlin.system.measureTimeMillis
import kotlin.system.measureNanoTime

/**
 * Performance benchmark tests for Corridor OS Mobile
 * Ensures the app meets performance requirements on mobile devices
 */
class PerformanceBenchmarkTest {
    
    private lateinit var calculator: PhysicsCalculator
    
    @Before
    fun setUp() {
        calculator = PhysicsCalculator()
    }
    
    // ============================================================================
    // PHYSICS CALCULATION PERFORMANCE TESTS
    // ============================================================================
    
    @Test
    fun `physics calculations should complete in under 10 microseconds`() {
        val testCases = listOf(
            Triple(1.0, 1, "momentum"),
            Triple(1.0, 2, "force"),
            Triple(2.0, 3, "energy")
        )
        
        testCases.forEach { (alpha, beta, name) ->
            val times = mutableListOf<Long>()
            
            // Warm up JVM
            repeat(1000) {
                calculator.calculatePhysicsQuantity(alpha, beta, 1.0, 9.81)
            }
            
            // Measure performance
            repeat(10000) {
                val time = measureNanoTime {
                    calculator.calculatePhysicsQuantity(alpha, beta, 1.0, 9.81)
                }
                times.add(time)
            }
            
            val averageTime = times.average()
            val maxTime = times.maxOrNull() ?: 0L
            val minTime = times.minOrNull() ?: 0L
            
            println("$name calculation performance:")
            println("  Average: ${averageTime / 1000.0}μs")
            println("  Min: ${minTime / 1000.0}μs")
            println("  Max: ${maxTime / 1000.0}μs")
            println("  95th percentile: ${times.sorted()[(times.size * 0.95).toInt()] / 1000.0}μs")
            
            assertTrue("$name calculation should average < 10μs", averageTime < 10_000)
            assertTrue("$name calculation should max < 100μs", maxTime < 100_000)
        }
    }
    
    @Test
    fun `batch calculations should scale linearly`() {
        val batchSizes = listOf(1, 10, 100, 1000, 10000)
        val results = mutableMapOf<Int, Double>()
        
        batchSizes.forEach { batchSize ->
            val totalTime = measureNanoTime {
                repeat(batchSize) {
                    calculator.calculatePhysicsQuantity(1.0, 2, 1.0, 9.81)
                }
            }
            
            val timePerCalculation = totalTime.toDouble() / batchSize
            results[batchSize] = timePerCalculation
            
            println("Batch size $batchSize: ${timePerCalculation / 1000.0}μs per calculation")
        }
        
        // Check that performance doesn't degrade significantly with batch size
        val smallBatchTime = results[1] ?: 0.0
        val largeBatchTime = results[10000] ?: 0.0
        
        val degradationRatio = largeBatchTime / smallBatchTime
        assertTrue("Performance shouldn't degrade > 2x with large batches", degradationRatio < 2.0)
    }
    
    @Test
    fun `concurrent calculations should not significantly impact performance`() {
        val sequentialTime = measureTimeMillis {
            repeat(1000) {
                calculator.calculatePhysicsQuantity(1.0, 2, 1.0, 9.81)
            }
        }
        
        val concurrentTime = measureTimeMillis {
            val threads = (1..4).map {
                Thread {
                    repeat(250) {
                        calculator.calculatePhysicsQuantity(1.0, 2, 1.0, 9.81)
                    }
                }
            }
            
            threads.forEach { it.start() }
            threads.forEach { it.join() }
        }
        
        println("Sequential time: ${sequentialTime}ms")
        println("Concurrent time: ${concurrentTime}ms")
        
        val speedupRatio = sequentialTime.toDouble() / concurrentTime
        println("Speedup ratio: ${String.format("%.2f", speedupRatio)}x")
        
        assertTrue("Concurrent execution should be faster or similar", speedupRatio >= 0.8)
    }
    
    // ============================================================================
    // MEMORY PERFORMANCE TESTS
    // ============================================================================
    
    @Test
    fun `calculations should not create excessive garbage`() {
        val runtime = Runtime.getRuntime()
        
        // Force garbage collection and measure initial memory
        System.gc()
        Thread.sleep(100)
        val initialMemory = runtime.totalMemory() - runtime.freeMemory()
        
        // Perform many calculations
        repeat(100000) {
            calculator.calculatePhysicsQuantity(1.0, 2, 1.0, 9.81)
        }
        
        // Measure memory after calculations (before GC)
        val memoryAfterCalculations = runtime.totalMemory() - runtime.freeMemory()
        
        // Force garbage collection
        System.gc()
        Thread.sleep(100)
        val memoryAfterGC = runtime.totalMemory() - runtime.freeMemory()
        
        val memoryIncrease = memoryAfterGC - initialMemory
        val temporaryMemory = memoryAfterCalculations - memoryAfterGC
        
        println("Memory usage:")
        println("  Initial: ${initialMemory / 1024}KB")
        println("  After calculations: ${memoryAfterCalculations / 1024}KB")
        println("  After GC: ${memoryAfterGC / 1024}KB")
        println("  Permanent increase: ${memoryIncrease / 1024}KB")
        println("  Temporary garbage: ${temporaryMemory / 1024}KB")
        
        assertTrue("Permanent memory increase should be minimal", memoryIncrease < 1024 * 1024) // < 1MB
    }
    
    @Test
    fun `result objects should be lightweight`() {
        val results = mutableListOf<Any>()
        
        val initialMemory = Runtime.getRuntime().let { it.totalMemory() - it.freeMemory() }
        
        // Create many result objects
        repeat(10000) {
            val result = calculator.calculatePhysicsQuantity(1.0, 2, 1.0, 9.81)
            results.add(result)
        }
        
        val finalMemory = Runtime.getRuntime().let { it.totalMemory() - it.freeMemory() }
        val memoryPerResult = (finalMemory - initialMemory) / 10000.0
        
        println("Memory per result object: ${memoryPerResult}bytes")
        
        assertTrue("Result objects should be < 1KB each", memoryPerResult < 1024)
    }
    
    // ============================================================================
    // OPTICAL COMPUTING PERFORMANCE TESTS
    // ============================================================================
    
    @Test
    fun `photon energy calculations should be fast`() {
        val wavelengths = listOf(1530.0, 1540.0, 1550.0, 1560.0, 1570.0)
        val iterations = 100000
        
        wavelengths.forEach { wavelength ->
            val time = measureNanoTime {
                repeat(iterations) {
                    calculator.calculatePhotonEnergy(wavelength)
                }
            }
            
            val timePerCalculation = time.toDouble() / iterations
            println("Photon energy (${wavelength}nm): ${timePerCalculation / 1000.0}μs per calculation")
            
            assertTrue("Photon energy calculation should be < 1μs", timePerCalculation < 1000)
        }
    }
    
    @Test
    fun `optical power calculations should scale with photon rate`() {
        val photonRates = listOf(1e9, 1e10, 1e11, 1e12, 1e13)
        val wavelength = 1550.0
        
        photonRates.forEach { rate ->
            val time = measureNanoTime {
                repeat(1000) {
                    calculator.calculateOpticalPower(rate, wavelength)
                }
            }
            
            val timePerCalculation = time.toDouble() / 1000
            println("Optical power (${rate} photons/s): ${timePerCalculation / 1000.0}μs per calculation")
            
            assertTrue("Optical power calculation should be < 5μs", timePerCalculation < 5000)
        }
    }
    
    // ============================================================================
    // RELATIVISTIC CALCULATION PERFORMANCE TESTS
    // ============================================================================
    
    @Test
    fun `lorentz factor calculations should handle extreme velocities efficiently`() {
        val velocityFractions = listOf(0.1, 0.3, 0.5, 0.7, 0.9, 0.99, 0.999, 0.9999)
        val iterations = 10000
        
        velocityFractions.forEach { fraction ->
            val velocity = fraction * PhysicsCalculator.SPEED_OF_LIGHT_M_S
            
            val time = measureNanoTime {
                repeat(iterations) {
                    calculator.calculateLorentzFactor(velocity)
                }
            }
            
            val timePerCalculation = time.toDouble() / iterations
            println("Lorentz factor (${fraction}c): ${timePerCalculation / 1000.0}μs per calculation")
            
            assertTrue("Lorentz factor calculation should be < 2μs", timePerCalculation < 2000)
        }
    }
    
    // ============================================================================
    // VALIDATION PERFORMANCE TESTS
    // ============================================================================
    
    @Test
    fun `formula validation should complete quickly`() {
        val times = mutableListOf<Long>()
        
        repeat(100) {
            val time = measureNanoTime {
                calculator.validateDecoderFormula()
            }
            times.add(time)
        }
        
        val averageTime = times.average()
        val maxTime = times.maxOrNull() ?: 0L
        
        println("Formula validation performance:")
        println("  Average: ${averageTime / 1_000_000.0}ms")
        println("  Max: ${maxTime / 1_000_000.0}ms")
        
        assertTrue("Validation should average < 10ms", averageTime < 10_000_000)
        assertTrue("Validation should max < 100ms", maxTime < 100_000_000)
    }
    
    // ============================================================================
    // MOBILE DEVICE SIMULATION TESTS
    // ============================================================================
    
    @Test
    fun `performance should remain stable under mobile constraints`() {
        // Simulate mobile device constraints
        val availableMemory = Runtime.getRuntime().maxMemory()
        val targetMemoryUsage = (availableMemory * 0.1).toLong() // Use only 10% of available memory
        
        println("Simulating mobile constraints:")
        println("  Available memory: ${availableMemory / 1024 / 1024}MB")
        println("  Target usage: ${targetMemoryUsage / 1024 / 1024}MB")
        
        val results = mutableListOf<Any>()
        var calculationCount = 0
        
        // Perform calculations until we approach memory limit
        while (Runtime.getRuntime().let { it.totalMemory() - it.freeMemory() } < targetMemoryUsage) {
            val result = calculator.calculatePhysicsQuantity(1.0, 2, 1.0, 9.81)
            results.add(result)
            calculationCount++
            
            if (calculationCount % 1000 == 0) {
                val currentMemory = Runtime.getRuntime().let { it.totalMemory() - it.freeMemory() }
                println("  Calculations: $calculationCount, Memory: ${currentMemory / 1024}KB")
            }
        }
        
        // Test that performance is still good
        val finalTime = measureNanoTime {
            repeat(1000) {
                calculator.calculatePhysicsQuantity(1.0, 2, 1.0, 9.81)
            }
        }
        
        val finalTimePerCalculation = finalTime.toDouble() / 1000
        println("Final performance: ${finalTimePerCalculation / 1000.0}μs per calculation")
        
        assertTrue("Performance should remain good under memory pressure", finalTimePerCalculation < 20_000)
    }
    
    @Test
    fun `calculations should work efficiently on low-end hardware simulation`() {
        // Simulate low-end hardware by adding artificial delays
        val baselineTime = measureNanoTime {
            repeat(1000) {
                calculator.calculatePhysicsQuantity(1.0, 2, 1.0, 9.81)
            }
        }
        
        // Simulate slower CPU by adding computation overhead
        val slowSimulationTime = measureNanoTime {
            repeat(1000) {
                // Add some CPU overhead to simulate slower device
                var dummy = 0.0
                repeat(100) {
                    dummy += kotlin.math.sin(it.toDouble())
                }
                
                calculator.calculatePhysicsQuantity(1.0, 2, 1.0, 9.81)
            }
        }
        
        val baselinePerCalc = baselineTime.toDouble() / 1000
        val slowSimPerCalc = slowSimulationTime.toDouble() / 1000
        
        println("Hardware simulation:")
        println("  Baseline: ${baselinePerCalc / 1000.0}μs per calculation")
        println("  Slow simulation: ${slowSimPerCalc / 1000.0}μs per calculation")
        println("  Overhead ratio: ${String.format("%.2f", slowSimPerCalc / baselinePerCalc)}x")
        
        assertTrue("Even with overhead, calculations should be < 50μs", slowSimPerCalc < 50_000)
    }
    
    // ============================================================================
    // THROUGHPUT TESTS
    // ============================================================================
    
    @Test
    fun `should achieve target throughput on mobile hardware`() {
        val targetOpsPerSecond = 100_000L // 100K operations per second
        val testDurationMs = 1000L // 1 second test
        
        var operationCount = 0L
        val startTime = System.currentTimeMillis()
        
        while (System.currentTimeMillis() - startTime < testDurationMs) {
            calculator.calculatePhysicsQuantity(1.0, 2, 1.0, 9.81)
            operationCount++
        }
        
        val actualDuration = System.currentTimeMillis() - startTime
        val actualOpsPerSecond = (operationCount * 1000) / actualDuration
        
        println("Throughput test:")
        println("  Operations: $operationCount")
        println("  Duration: ${actualDuration}ms")
        println("  Throughput: $actualOpsPerSecond ops/sec")
        println("  Target: $targetOpsPerSecond ops/sec")
        
        assertTrue("Should achieve target throughput", actualOpsPerSecond >= targetOpsPerSecond)
    }
    
    @Test
    fun `mobile performance metrics calculation should be reasonable`() {
        val time = measureTimeMillis {
            calculator.calculateMobilePerformanceMetrics()
        }
        
        println("Mobile performance metrics calculation time: ${time}ms")
        
        assertTrue("Performance metrics calculation should complete < 5 seconds", time < 5000)
    }
}
