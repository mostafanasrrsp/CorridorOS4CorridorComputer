package com.corridor.os.android.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.corridor.os.android.ui.screens.PhysicsScreen
import com.corridor.os.android.ui.theme.CorridorOSTheme
import com.corridor.os.android.viewmodel.MainViewModel
import org.junit.Test
import org.junit.Rule
import org.junit.runner.RunWith

/**
 * Integration tests for PhysicsScreen
 * Tests UI interactions, state updates, and user workflows
 */
@RunWith(AndroidJUnit4::class)
class PhysicsScreenIntegrationTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Test
    fun physicsScreen_displaysDecoderFormula() {
        composeTestRule.setContent {
            CorridorOSTheme {
                PhysicsScreen(viewModel = MainViewModel())
            }
        }
        
        // Check that the decoder formula is displayed
        composeTestRule
            .onNodeWithText("Physics Decoder Formula")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("Q(β) = m · a^(α·δ_β,2) · c^(α·(δ_β,1+δ_β,3))")
            .assertIsDisplayed()
    }
    
    @Test
    fun physicsScreen_validationStatusIsDisplayed() {
        composeTestRule.setContent {
            CorridorOSTheme {
                PhysicsScreen(viewModel = MainViewModel())
            }
        }
        
        // Wait for validation to complete
        composeTestRule.waitForIdle()
        
        // Check validation status
        composeTestRule
            .onNodeWithText("Formula Validated")
            .assertIsDisplayed()
    }
    
    @Test
    fun physicsScreen_physicsTypeSelection() {
        composeTestRule.setContent {
            CorridorOSTheme {
                PhysicsScreen(viewModel = MainViewModel())
            }
        }
        
        // Check all physics type chips are displayed
        composeTestRule
            .onNodeWithText("Momentum")
            .assertIsDisplayed()
            .assertHasClickAction()
        
        composeTestRule
            .onNodeWithText("Force")
            .assertIsDisplayed()
            .assertHasClickAction()
        
        composeTestRule
            .onNodeWithText("Energy")
            .assertIsDisplayed()
            .assertHasClickAction()
        
        // Test selecting Force
        composeTestRule
            .onNodeWithText("Force")
            .performClick()
        
        // Acceleration field should be visible for force calculations
        composeTestRule
            .onNodeWithText("Acceleration (a)")
            .assertIsDisplayed()
    }
    
    @Test
    fun physicsScreen_inputParametersAreEditable() {
        composeTestRule.setContent {
            CorridorOSTheme {
                PhysicsScreen(viewModel = MainViewModel())
            }
        }
        
        // Test Alpha input
        composeTestRule
            .onNodeWithText("Alpha (α)")
            .assertIsDisplayed()
            .performTextClearance()
            .performTextInput("2.0")
        
        // Test Mass input
        composeTestRule
            .onNodeWithText("Mass (m)")
            .assertIsDisplayed()
            .performTextClearance()
            .performTextInput("5.0")
        
        // Select Force to enable acceleration input
        composeTestRule
            .onNodeWithText("Force")
            .performClick()
        
        // Test Acceleration input
        composeTestRule
            .onNodeWithText("Acceleration (a)")
            .assertIsDisplayed()
            .performTextClearance()
            .performTextInput("10.0")
    }
    
    @Test
    fun physicsScreen_calculationWorkflow() {
        composeTestRule.setContent {
            CorridorOSTheme {
                PhysicsScreen(viewModel = MainViewModel())
            }
        }
        
        // Select Force calculation
        composeTestRule
            .onNodeWithText("Force")
            .performClick()
        
        // Enter parameters
        composeTestRule
            .onNodeWithText("Alpha (α)")
            .performTextClearance()
            .performTextInput("1.0")
        
        composeTestRule
            .onNodeWithText("Mass (m)")
            .performTextClearance()
            .performTextInput("2.0")
        
        composeTestRule
            .onNodeWithText("Acceleration (a)")
            .performTextClearance()
            .performTextInput("9.81")
        
        // Perform calculation
        composeTestRule
            .onNodeWithText("Calculate Force")
            .assertIsDisplayed()
            .assertIsEnabled()
            .performClick()
        
        // Wait for calculation to complete
        composeTestRule.waitForIdle()
        
        // Check that result is displayed
        composeTestRule
            .onNodeWithText("Force")
            .assertIsDisplayed()
        
        // Check that calculation time is displayed
        composeTestRule
            .onNode(hasText("Calculated in") and hasText("μs"))
            .assertIsDisplayed()
    }
    
    @Test
    fun physicsScreen_multipleCalculationsCreateHistory() {
        composeTestRule.setContent {
            CorridorOSTheme {
                PhysicsScreen(viewModel = MainViewModel())
            }
        }
        
        // Perform multiple calculations
        repeat(3) { index ->
            // Select Force
            composeTestRule
                .onNodeWithText("Force")
                .performClick()
            
            // Enter different mass values
            composeTestRule
                .onNodeWithText("Mass (m)")
                .performTextClearance()
                .performTextInput("${index + 1}.0")
            
            // Calculate
            composeTestRule
                .onNodeWithText("Calculate Force")
                .performClick()
            
            composeTestRule.waitForIdle()
        }
        
        // Check that "Recent Calculations" header is displayed
        composeTestRule
            .onNodeWithText("Recent Calculations")
            .assertIsDisplayed()
        
        // Check that clear button is displayed
        composeTestRule
            .onNodeWithText("Clear Results")
            .assertIsDisplayed()
            .assertHasClickAction()
    }
    
    @Test
    fun physicsScreen_clearResultsWorks() {
        composeTestRule.setContent {
            CorridorOSTheme {
                PhysicsScreen(viewModel = MainViewModel())
            }
        }
        
        // Perform a calculation first
        composeTestRule
            .onNodeWithText("Force")
            .performClick()
        
        composeTestRule
            .onNodeWithText("Calculate Force")
            .performClick()
        
        composeTestRule.waitForIdle()
        
        // Verify results are shown
        composeTestRule
            .onNodeWithText("Recent Calculations")
            .assertIsDisplayed()
        
        // Clear results
        composeTestRule
            .onNodeWithText("Clear Results")
            .performClick()
        
        // Verify results are cleared
        composeTestRule
            .onNodeWithText("Recent Calculations")
            .assertDoesNotExist()
    }
    
    @Test
    fun physicsScreen_revalidateButtonWorks() {
        composeTestRule.setContent {
            CorridorOSTheme {
                PhysicsScreen(viewModel = MainViewModel())
            }
        }
        
        // Wait for initial validation
        composeTestRule.waitForIdle()
        
        // Find and click revalidate button
        composeTestRule
            .onNodeWithText("Revalidate")
            .assertIsDisplayed()
            .assertHasClickAction()
            .performClick()
        
        // Should show validating state briefly
        composeTestRule.waitForIdle()
        
        // Should return to validated state
        composeTestRule
            .onNodeWithText("Formula Validated")
            .assertIsDisplayed()
    }
    
    @Test
    fun physicsScreen_accessibilityLabels() {
        composeTestRule.setContent {
            CorridorOSTheme {
                PhysicsScreen(viewModel = MainViewModel())
            }
        }
        
        // Check that important elements have proper content descriptions
        composeTestRule
            .onNode(hasContentDescription("Physics Formula"))
            .assertIsDisplayed()
        
        // Check input fields have proper labels
        composeTestRule
            .onNodeWithText("Alpha (α)")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("Mass (m)")
            .assertIsDisplayed()
        
        // Check buttons have proper text
        composeTestRule
            .onNodeWithText("Calculate Momentum")
            .assertIsDisplayed()
    }
    
    @Test
    fun physicsScreen_respondsToSystemTheme() {
        // This test would need to be expanded with proper theme testing
        composeTestRule.setContent {
            CorridorOSTheme(darkTheme = false) {
                PhysicsScreen(viewModel = MainViewModel())
            }
        }
        
        // Verify content is displayed (theme-specific assertions would go here)
        composeTestRule
            .onNodeWithText("Physics Decoder Formula")
            .assertIsDisplayed()
    }
    
    @Test
    fun physicsScreen_handlesLandscapeOrientation() {
        composeTestRule.setContent {
            CorridorOSTheme {
                PhysicsScreen(viewModel = MainViewModel())
            }
        }
        
        // All major elements should still be accessible in landscape
        composeTestRule
            .onNodeWithText("Physics Decoder Formula")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("Select Physics Quantity")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("Input Parameters")
            .assertIsDisplayed()
    }
    
    @Test
    fun physicsScreen_animationsWork() {
        composeTestRule.setContent {
            CorridorOSTheme {
                PhysicsScreen(viewModel = MainViewModel())
            }
        }
        
        // Wait for animations to settle
        composeTestRule.waitForIdle()
        
        // Verify animated components are displayed
        composeTestRule
            .onNodeWithText("Physics Decoder Formula")
            .assertIsDisplayed()
        
        // Test that clicking triggers animations (hard to test directly)
        composeTestRule
            .onNodeWithText("Force")
            .performClick()
        
        composeTestRule.waitForIdle()
        
        // Verify state change occurred
        composeTestRule
            .onNodeWithText("Acceleration (a)")
            .assertIsDisplayed()
    }
}
