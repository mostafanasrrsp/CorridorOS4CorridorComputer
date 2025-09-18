package com.corridor.os.android.data

import android.content.Context
import android.content.SharedPreferences
import com.corridor.os.android.physics.PhysicsResult
import com.corridor.os.android.physics.PhysicsType
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

/**
 * Unit tests for PreferencesManager
 * Tests data persistence, preferences management, and statistics tracking
 */
class PreferencesManagerTest {
    
    @Mock
    private lateinit var mockContext: Context
    
    @Mock
    private lateinit var mockSharedPreferences: SharedPreferences
    
    @Mock
    private lateinit var mockEditor: SharedPreferences.Editor
    
    private lateinit var preferencesManager: PreferencesManager
    
    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        
        // Setup mock behavior
        `when`(mockContext.getSharedPreferences("corridor_os_prefs", Context.MODE_PRIVATE))
            .thenReturn(mockSharedPreferences)
        `when`(mockSharedPreferences.edit()).thenReturn(mockEditor)
        `when`(mockEditor.putBoolean(any(), any())).thenReturn(mockEditor)
        `when`(mockEditor.putString(any(), any())).thenReturn(mockEditor)
        `when`(mockEditor.putInt(any(), any())).thenReturn(mockEditor)
        `when`(mockEditor.putLong(any(), any())).thenReturn(mockEditor)
        
        // Setup default values
        `when`(mockSharedPreferences.getBoolean("dark_mode", false)).thenReturn(false)
        `when`(mockSharedPreferences.getBoolean("animations_enabled", true)).thenReturn(true)
        `when`(mockSharedPreferences.getString("default_alpha", "1.0")).thenReturn("1.0")
        `when`(mockSharedPreferences.getString("default_mass", "1.0")).thenReturn("1.0")
        `when`(mockSharedPreferences.getString("default_acceleration", "9.81")).thenReturn("9.81")
        `when`(mockSharedPreferences.getInt("total_calculations", 0)).thenReturn(0)
        `when`(mockSharedPreferences.getInt("favorite_physics_type", 1)).thenReturn(1)
        `when`(mockSharedPreferences.getInt("app_launches", 0)).thenReturn(0)
        `when`(mockSharedPreferences.getLong("first_launch_date", 0L)).thenReturn(0L)
        `when`(mockSharedPreferences.getLong("last_launch_date", 0L)).thenReturn(0L)
        `when`(mockSharedPreferences.getString("calculation_history", null)).thenReturn(null)
        
        preferencesManager = PreferencesManager(mockContext)
    }
    
    // ============================================================================
    // USER PREFERENCES TESTS
    // ============================================================================
    
    @Test
    fun `initial preferences should have default values`() = runBlocking {
        assertEquals("Dark mode should be false by default", false, preferencesManager.isDarkMode.first())
        assertEquals("Animations should be enabled by default", true, preferencesManager.isAnimationsEnabled.first())
        assertEquals("Default alpha should be 1.0", "1.0", preferencesManager.defaultAlpha.first())
        assertEquals("Default mass should be 1.0", "1.0", preferencesManager.defaultMass.first())
        assertEquals("Default acceleration should be 9.81", "9.81", preferencesManager.defaultAcceleration.first())
    }
    
    @Test
    fun `setDarkMode should update preference and persist`() = runBlocking {
        preferencesManager.setDarkMode(true)
        
        verify(mockEditor).putBoolean("dark_mode", true)
        verify(mockEditor).apply()
    }
    
    @Test
    fun `setAnimationsEnabled should update preference and persist`() = runBlocking {
        preferencesManager.setAnimationsEnabled(false)
        
        verify(mockEditor).putBoolean("animations_enabled", false)
        verify(mockEditor).apply()
    }
    
    @Test
    fun `setDefaultAlpha should update preference and persist`() = runBlocking {
        val newAlpha = "2.5"
        preferencesManager.setDefaultAlpha(newAlpha)
        
        verify(mockEditor).putString("default_alpha", newAlpha)
        verify(mockEditor).apply()
    }
    
    @Test
    fun `setDefaultMass should update preference and persist`() = runBlocking {
        val newMass = "5.0"
        preferencesManager.setDefaultMass(newMass)
        
        verify(mockEditor).putString("default_mass", newMass)
        verify(mockEditor).apply()
    }
    
    @Test
    fun `setDefaultAcceleration should update preference and persist`() = runBlocking {
        val newAcceleration = "10.0"
        preferencesManager.setDefaultAcceleration(newAcceleration)
        
        verify(mockEditor).putString("default_acceleration", newAcceleration)
        verify(mockEditor).apply()
    }
    
    // ============================================================================
    // CALCULATION HISTORY TESTS
    // ============================================================================
    
    @Test
    fun `addCalculationToHistory should add result to beginning of list`() = runBlocking {
        val result = createTestPhysicsResult(PhysicsType.FORCE, 2)
        
        preferencesManager.addCalculationToHistory(result)
        
        val history = preferencesManager.calculationHistory.first()
        assertEquals("History should contain 1 item", 1, history.size)
        assertEquals("First item should be the added result", result, history.first())
    }
    
    @Test
    fun `addCalculationToHistory should limit history to 50 items`() = runBlocking {
        // Add 51 calculations
        repeat(51) { index ->
            val result = createTestPhysicsResult(PhysicsType.FORCE, 2, calculationTimeNs = index.toLong())
            preferencesManager.addCalculationToHistory(result)
        }
        
        val history = preferencesManager.calculationHistory.first()
        assertEquals("History should be limited to 50 items", 50, history.size)
        
        // Most recent should be first (index 50), oldest should be last (index 1)
        assertEquals("Most recent calculation should be first", 50L, history.first().calculationTimeNs)
        assertEquals("Oldest calculation should be last", 1L, history.last().calculationTimeNs)
    }
    
    @Test
    fun `addCalculationToHistory should update total calculations counter`() = runBlocking {
        val result = createTestPhysicsResult(PhysicsType.MOMENTUM, 1)
        
        preferencesManager.addCalculationToHistory(result)
        
        verify(mockEditor).putInt("total_calculations", 1)
        verify(mockEditor).apply()
    }
    
    @Test
    fun `clearCalculationHistory should empty the history`() = runBlocking {
        // Add some calculations first
        repeat(5) {
            preferencesManager.addCalculationToHistory(createTestPhysicsResult(PhysicsType.ENERGY, 3))
        }
        
        preferencesManager.clearCalculationHistory()
        
        val history = preferencesManager.calculationHistory.first()
        assertTrue("History should be empty after clearing", history.isEmpty())
        verify(mockEditor).remove("calculation_history")
    }
    
    // ============================================================================
    // STATISTICS TESTS
    // ============================================================================
    
    @Test
    fun `getCalculationStats should return correct statistics`() = runBlocking {
        // Setup mock to return some calculation history
        `when`(mockSharedPreferences.getInt("total_calculations", 0)).thenReturn(15)
        `when`(mockSharedPreferences.getInt("favorite_physics_type", 1)).thenReturn(2)
        
        // Add some test calculations
        val testResults = listOf(
            createTestPhysicsResult(PhysicsType.MOMENTUM, 1, calculationTimeNs = 1000),
            createTestPhysicsResult(PhysicsType.FORCE, 2, calculationTimeNs = 2000),
            createTestPhysicsResult(PhysicsType.FORCE, 2, calculationTimeNs = 1500),
            createTestPhysicsResult(PhysicsType.ENERGY, 3, calculationTimeNs = 3000),
            createTestPhysicsResult(PhysicsType.ENERGY, 3, calculationTimeNs = 2500)
        )
        
        testResults.forEach { preferencesManager.addCalculationToHistory(it) }
        
        val stats = preferencesManager.getCalculationStats()
        
        assertTrue("Total calculations should be positive", stats.totalCalculations >= 0)
        assertEquals("Should have 1 momentum calculation", 1, stats.momentumCalculations)
        assertEquals("Should have 2 force calculations", 2, stats.forceCalculations)
        assertEquals("Should have 2 energy calculations", 2, stats.energyCalculations)
        
        val expectedAvgTime = (1000 + 2000 + 1500 + 3000 + 2500) / 5.0
        assertEquals("Average calculation time should be correct", expectedAvgTime, stats.averageCalculationTimeNs, 1.0)
        assertEquals("Fastest calculation should be 1000ns", 1000L, stats.fastestCalculationTimeNs)
        assertEquals("Slowest calculation should be 3000ns", 3000L, stats.slowestCalculationTimeNs)
    }
    
    @Test
    fun `getCalculationStats should handle empty history`() = runBlocking {
        val stats = preferencesManager.getCalculationStats()
        
        assertEquals("Empty history should have 0 momentum calculations", 0, stats.momentumCalculations)
        assertEquals("Empty history should have 0 force calculations", 0, stats.forceCalculations)
        assertEquals("Empty history should have 0 energy calculations", 0, stats.energyCalculations)
        assertEquals("Empty history should have 0 average time", 0.0, stats.averageCalculationTimeNs, 1.0)
        assertEquals("Empty history should have 0 fastest time", 0L, stats.fastestCalculationTimeNs)
        assertEquals("Empty history should have 0 slowest time", 0L, stats.slowestCalculationTimeNs)
    }
    
    // ============================================================================
    // APP USAGE TRACKING TESTS
    // ============================================================================
    
    @Test
    fun `recordAppLaunch should increment launch counter`() {
        `when`(mockSharedPreferences.getInt("app_launches", 0)).thenReturn(5)
        
        preferencesManager.recordAppLaunch()
        
        verify(mockEditor).putInt("app_launches", 6)
        verify(mockEditor).putLong(eq("last_launch_date"), any())
        verify(mockEditor, times(2)).apply() // Called twice: for launches and last_launch_date
    }
    
    @Test
    fun `recordAppLaunch should set first launch date on first launch`() {
        `when`(mockSharedPreferences.getInt("app_launches", 0)).thenReturn(0)
        `when`(mockSharedPreferences.getLong("first_launch_date", 0L)).thenReturn(0L)
        
        preferencesManager.recordAppLaunch()
        
        verify(mockEditor).putInt("app_launches", 1)
        verify(mockEditor).putLong(eq("first_launch_date"), any())
        verify(mockEditor).putLong(eq("last_launch_date"), any())
        verify(mockEditor, times(3)).apply() // Called three times
    }
    
    @Test
    fun `getAppUsageStats should return correct statistics`() {
        val expectedLaunches = 10
        val expectedFirstLaunch = 1234567890L
        val expectedLastLaunch = 1234567999L
        val expectedCalculations = 25
        
        `when`(mockSharedPreferences.getInt("app_launches", 0)).thenReturn(expectedLaunches)
        `when`(mockSharedPreferences.getLong("first_launch_date", 0L)).thenReturn(expectedFirstLaunch)
        `when`(mockSharedPreferences.getLong("last_launch_date", 0L)).thenReturn(expectedLastLaunch)
        `when`(mockSharedPreferences.getInt("total_calculations", 0)).thenReturn(expectedCalculations)
        
        val stats = preferencesManager.getAppUsageStats()
        
        assertEquals("Total launches should match", expectedLaunches, stats.totalLaunches)
        assertEquals("First launch date should match", expectedFirstLaunch, stats.firstLaunchDate)
        assertEquals("Last launch date should match", expectedLastLaunch, stats.lastLaunchDate)
        assertEquals("Total calculations should match", expectedCalculations, stats.totalCalculations)
    }
    
    // ============================================================================
    // FAVORITE PHYSICS TYPE TRACKING
    // ============================================================================
    
    @Test
    fun `favorite physics type should update based on usage`() = runBlocking {
        // Add more force calculations than others
        repeat(5) {
            preferencesManager.addCalculationToHistory(createTestPhysicsResult(PhysicsType.FORCE, 2))
        }
        repeat(2) {
            preferencesManager.addCalculationToHistory(createTestPhysicsResult(PhysicsType.MOMENTUM, 1))
        }
        repeat(1) {
            preferencesManager.addCalculationToHistory(createTestPhysicsResult(PhysicsType.ENERGY, 3))
        }
        
        // Force (beta=2) should become the favorite
        verify(mockEditor, atLeastOnce()).putInt("favorite_physics_type", 2)
    }
    
    // ============================================================================
    // SERIALIZATION TESTS
    // ============================================================================
    
    @Test
    fun `calculation history serialization should handle complex objects`() = runBlocking {
        val complexResult = PhysicsResult(
            value = 1.23456789e-15,
            unit = "kg⋅m/s",
            physicsType = PhysicsType.MOMENTUM,
            alpha = 1.5,
            beta = 1,
            mass = 9.1093837015e-31, // Electron mass
            acceleration = 0.0,
            calculationTimeNs = 1234567L
        )
        
        preferencesManager.addCalculationToHistory(complexResult)
        
        // Verify that the JSON serialization was attempted
        verify(mockEditor).putString(eq("calculation_history"), any())
    }
    
    @Test
    fun `should handle corrupted preferences gracefully`() {
        // Simulate corrupted JSON data
        `when`(mockSharedPreferences.getString("calculation_history", null))
            .thenReturn("{invalid json}")
        
        // Should not throw exception and return empty list
        val preferencesManager2 = PreferencesManager(mockContext)
        runBlocking {
            val history = preferencesManager2.calculationHistory.first()
            assertTrue("Should return empty list for corrupted data", history.isEmpty())
        }
    }
    
    // ============================================================================
    // HELPER METHODS
    // ============================================================================
    
    private fun createTestPhysicsResult(
        physicsType: PhysicsType,
        beta: Int,
        value: Double = 1.0,
        alpha: Double = 1.0,
        mass: Double = 1.0,
        acceleration: Double = 9.81,
        calculationTimeNs: Long = 1000L
    ): PhysicsResult {
        return PhysicsResult(
            value = value,
            unit = when (physicsType) {
                PhysicsType.MOMENTUM -> "kg⋅m/s"
                PhysicsType.FORCE -> "N"
                PhysicsType.ENERGY -> "J"
                else -> "unknown"
            },
            physicsType = physicsType,
            alpha = alpha,
            beta = beta,
            mass = mass,
            acceleration = acceleration,
            calculationTimeNs = calculationTimeNs
        )
    }
    
    private fun <T> any(): T {
        return org.mockito.ArgumentMatchers.any()
    }
}
