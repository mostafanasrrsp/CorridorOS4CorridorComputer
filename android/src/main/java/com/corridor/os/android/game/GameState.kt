package com.corridor.os.android.game

import androidx.compose.runtime.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Global game state manager for Evolution Decoder
 * Handles game progression, achievements, and player progress
 */
class GameState {
    
    private val _currentStage = MutableStateFlow(EvolutionStage.PRIMORDIAL_SOUP)
    val currentStage: StateFlow<EvolutionStage> = _currentStage.asStateFlow()
    
    private val _gameTime = MutableStateFlow(0.0)
    val gameTime: StateFlow<Double> = _gameTime.asStateFlow()
    
    private val _totalFitness = MutableStateFlow(0.0)
    val totalFitness: StateFlow<Double> = _totalFitness.asStateFlow()
    
    private val _unlockedAchievements = MutableStateFlow<Set<Achievement>>(emptySet())
    val unlockedAchievements: StateFlow<Set<Achievement>> = _unlockedAchievements.asStateFlow()
    
    private val _gameEvents = MutableStateFlow<List<GameEvent>>(emptyList())
    val gameEvents: StateFlow<List<GameEvent>> = _gameEvents.asStateFlow()
    
    private val _isGameActive = MutableStateFlow(true)
    val isGameActive: StateFlow<Boolean> = _isGameActive.asStateFlow()
    
    private val evolutionEngine = EvolutionEngine()
    
    /**
     * Advance evolution by one time step
     */
    fun advanceEvolution(playerChoices: PlayerChoices): EvolutionSimulationResult {
        val timeDelta = 1.0 // 1 million years per step
        
        val result = evolutionEngine.simulateEvolution(
            currentStage = _currentStage.value,
            timeDelta = timeDelta,
            playerChoices = playerChoices
        )
        
        // Update game state
        _currentStage.value = result.newStage
        _gameTime.value += timeDelta
        _totalFitness.value = result.totalFitness
        
        // Check for achievements
        checkAchievements(result)
        
        // Add game events
        val newEvents = result.randomEvents.map { evolutionEvent ->
            GameEvent(
                type = evolutionEvent,
                timeOccurred = _gameTime.value,
                description = evolutionEvent.description,
                impact = calculateEventImpact(evolutionEvent, result)
            )
        }
        
        val currentEvents = _gameEvents.value.toMutableList()
        currentEvents.addAll(0, newEvents) // Add to beginning
        
        // Keep only last 20 events
        if (currentEvents.size > 20) {
            currentEvents.subList(20, currentEvents.size).clear()
        }
        
        _gameEvents.value = currentEvents
        
        // Check for game over conditions
        checkGameOverConditions(result)
        
        return result
    }
    
    /**
     * Reset game to beginning
     */
    fun resetGame() {
        _currentStage.value = EvolutionStage.PRIMORDIAL_SOUP
        _gameTime.value = 0.0
        _totalFitness.value = 0.0
        _unlockedAchievements.value = emptySet()
        _gameEvents.value = emptyList()
        _isGameActive.value = true
    }
    
    /**
     * Get game statistics
     */
    fun getGameStatistics(): GameStatistics {
        val stage = _currentStage.value
        val events = _gameEvents.value
        
        return GameStatistics(
            currentStageName = stage.name,
            timeElapsed = _gameTime.value,
            totalFitness = _totalFitness.value,
            complexity = stage.complexity,
            intelligence = stage.intelligence,
            technology = stage.technology,
            populationMass = stage.populationMass,
            unlockedTraits = stage.unlocked.size,
            totalTraits = EvolutionTrait.values().size,
            achievementsUnlocked = _unlockedAchievements.value.size,
            totalAchievements = Achievement.values().size,
            majorEvents = events.count { it.impact > 0.5 },
            extinctionEvents = events.count { it.type == EvolutionEvent.MASS_EXTINCTION }
        )
    }
    
    private fun checkAchievements(result: EvolutionSimulationResult) {
        val currentAchievements = _unlockedAchievements.value.toMutableSet()
        
        // Survival achievements
        if (result.survivalResult.survivalProbability > 0.9) {
            currentAchievements.add(Achievement.MASTER_SURVIVOR)
        }
        
        // Adaptation achievements
        if (result.adaptationResult.fitnessScore > 1e15) {
            currentAchievements.add(Achievement.ADAPTATION_EXPERT)
        }
        
        // Intelligence achievements
        if (result.intelligenceResult.fitnessScore > 1e18) {
            currentAchievements.add(Achievement.CONSCIOUSNESS_EMERGES)
        }
        
        // Stage achievements
        when (result.newStage.name) {
            "Simple Cells" -> currentAchievements.add(Achievement.FIRST_CELL)
            "Complex Organisms" -> currentAchievements.add(Achievement.MULTICELLULAR_LIFE)
            "Intelligence Emergence" -> currentAchievements.add(Achievement.FIRST_BRAIN)
            "Civilization" -> currentAchievements.add(Achievement.CIVILIZATION_FOUNDED)
        }
        
        // Time-based achievements
        if (_gameTime.value > 100.0) {
            currentAchievements.add(Achievement.LONG_TERM_SURVIVOR)
        }
        
        // Fitness achievements
        if (_totalFitness.value > 1e20) {
            currentAchievements.add(Achievement.EVOLUTION_MASTER)
        }
        
        _unlockedAchievements.value = currentAchievements
    }
    
    private fun calculateEventImpact(event: EvolutionEvent, result: EvolutionSimulationResult): Double {
        return when (event) {
            EvolutionEvent.MASS_EXTINCTION -> 1.0
            EvolutionEvent.CLIMATE_CHANGE -> 0.7
            EvolutionEvent.TECHNOLOGICAL_BREAKTHROUGH -> 0.8
            EvolutionEvent.GENETIC_MUTATION -> 0.3
            EvolutionEvent.RESOURCE_DISCOVERY -> 0.5
            EvolutionEvent.PREDATOR_EMERGENCE -> 0.6
            EvolutionEvent.SYMBIOSIS_OPPORTUNITY -> 0.4
        }
    }
    
    private fun checkGameOverConditions(result: EvolutionSimulationResult) {
        // Game over if survival probability drops too low
        if (result.survivalResult.survivalProbability < 0.01) {
            _isGameActive.value = false
        }
        
        // Victory condition: reach space age with high intelligence
        if (result.newStage.name == "Space Age" && result.newStage.intelligence > 95) {
            _isGameActive.value = false // Victory!
        }
    }
}

/**
 * Game achievements
 */
enum class Achievement(val displayName: String, val description: String) {
    FIRST_CELL("First Cell", "Successfully evolve from primordial soup to cellular life"),
    MULTICELLULAR_LIFE("Multicellular Life", "Develop complex multicellular organisms"),
    FIRST_BRAIN("First Brain", "Evolve basic intelligence and nervous system"),
    CIVILIZATION_FOUNDED("Civilization", "Establish organized society and culture"),
    MASTER_SURVIVOR("Master Survivor", "Achieve 90%+ survival probability"),
    ADAPTATION_EXPERT("Adaptation Expert", "Reach maximum adaptation fitness"),
    CONSCIOUSNESS_EMERGES("Consciousness Emerges", "Develop advanced intelligence"),
    LONG_TERM_SURVIVOR("Long Term Survivor", "Survive for 100+ million years"),
    EVOLUTION_MASTER("Evolution Master", "Achieve ultimate evolutionary fitness"),
    EXTINCTION_SURVIVOR("Extinction Survivor", "Survive a mass extinction event"),
    TECHNOLOGICAL_PIONEER("Tech Pioneer", "Develop advanced technology"),
    SPACE_EXPLORER("Space Explorer", "Achieve interplanetary travel")
}

/**
 * Game events with timing and impact
 */
data class GameEvent(
    val type: EvolutionEvent,
    val timeOccurred: Double,
    val description: String,
    val impact: Double // 0-1 scale of event significance
)

/**
 * Game statistics for display
 */
data class GameStatistics(
    val currentStageName: String,
    val timeElapsed: Double,
    val totalFitness: Double,
    val complexity: Int,
    val intelligence: Int,
    val technology: Int,
    val populationMass: Double,
    val unlockedTraits: Int,
    val totalTraits: Int,
    val achievementsUnlocked: Int,
    val totalAchievements: Int,
    val majorEvents: Int,
    val extinctionEvents: Int
)
