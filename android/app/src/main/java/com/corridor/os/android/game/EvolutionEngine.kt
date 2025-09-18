package com.corridor.os.android.game

import com.corridor.os.android.physics.PhysicsCalculator
import kotlin.math.*
import kotlin.random.Random

/**
 * Evolution Engine using the Physics Decoder Formula
 * 
 * Maps the decoder formula Q(β) = m·a^(α·δ_β,2)·c^(α·(δ_β,1+δ_β,3)) to evolution:
 * - m = Population Mass (biomass, population size)
 * - a = Adaptation Rate (evolutionary speed)
 * - c = Environmental Pressure (change rate)
 * - α = Evolution Intensity (mutation/selection pressure)
 * - β = Evolution Type: 1=Survival, 2=Adaptation, 3=Intelligence
 */
class EvolutionEngine {
    
    private val physicsCalculator = PhysicsCalculator()
    
    /**
     * Calculate evolutionary fitness using decoder formula
     */
    fun calculateEvolutionaryFitness(
        populationMass: Double,      // m: Population biomass
        adaptationRate: Double,      // a: Speed of adaptation
        environmentalPressure: Double, // c: Rate of environmental change
        evolutionIntensity: Double,  // α: Mutation/selection intensity
        evolutionType: EvolutionType // β: Type of evolution
    ): EvolutionResult {
        
        val beta = evolutionType.betaValue
        
        // Use decoder formula for evolution calculation
        val fitnessValue = physicsCalculator.calculatePhysicsQuantity(
            alpha = evolutionIntensity,
            beta = beta,
            mass = populationMass,
            acceleration = adaptationRate
        )
        
        // Apply environmental pressure modifier
        val environmentalFactor = when (evolutionType) {
            EvolutionType.SURVIVAL -> environmentalPressure.pow(evolutionIntensity)
            EvolutionType.ADAPTATION -> 1.0 // Adaptation doesn't use environmental pressure in formula
            EvolutionType.INTELLIGENCE -> environmentalPressure.pow(evolutionIntensity)
        }
        
        val adjustedFitness = fitnessValue.value * environmentalFactor
        
        return EvolutionResult(
            fitnessScore = adjustedFitness,
            evolutionType = evolutionType,
            populationMass = populationMass,
            adaptationRate = adaptationRate,
            environmentalPressure = environmentalPressure,
            evolutionIntensity = evolutionIntensity,
            survivalProbability = calculateSurvivalProbability(adjustedFitness),
            nextEvolutionStage = determineNextStage(adjustedFitness, evolutionType)
        )
    }
    
    /**
     * Simulate evolution over time
     */
    fun simulateEvolution(
        currentStage: EvolutionStage,
        timeDelta: Double,
        playerChoices: PlayerChoices
    ): EvolutionSimulationResult {
        
        val populationMass = currentStage.populationMass * (1.0 + playerChoices.populationGrowthRate * timeDelta)
        val adaptationRate = currentStage.adaptationRate * (1.0 + playerChoices.adaptationBonus)
        val environmentalPressure = generateEnvironmentalPressure(currentStage, timeDelta)
        
        // Calculate fitness for each evolution type
        val survivalResult = calculateEvolutionaryFitness(
            populationMass, adaptationRate, environmentalPressure, 
            playerChoices.mutationRate, EvolutionType.SURVIVAL
        )
        
        val adaptationResult = calculateEvolutionaryFitness(
            populationMass, adaptationRate, environmentalPressure,
            playerChoices.selectionPressure, EvolutionType.ADAPTATION
        )
        
        val intelligenceResult = calculateEvolutionaryFitness(
            populationMass, adaptationRate, environmentalPressure,
            playerChoices.intelligenceBonus, EvolutionType.INTELLIGENCE
        )
        
        // Determine overall evolution outcome
        val totalFitness = survivalResult.fitnessScore + adaptationResult.fitnessScore + intelligenceResult.fitnessScore
        val newStage = determineEvolutionStage(totalFitness, currentStage)
        
        // Generate random events based on environmental pressure
        val randomEvents = generateRandomEvents(environmentalPressure, currentStage)
        
        return EvolutionSimulationResult(
            newStage = newStage,
            survivalResult = survivalResult,
            adaptationResult = adaptationResult,
            intelligenceResult = intelligenceResult,
            totalFitness = totalFitness,
            randomEvents = randomEvents,
            timeElapsed = timeDelta
        )
    }
    
    private fun calculateSurvivalProbability(fitness: Double): Double {
        // Convert fitness to survival probability (0-1)
        return 1.0 / (1.0 + exp(-fitness / 1e10)) // Sigmoid function
    }
    
    private fun determineNextStage(fitness: Double, evolutionType: EvolutionType): EvolutionStage? {
        // Logic to determine if evolution advances to next stage
        val threshold = when (evolutionType) {
            EvolutionType.SURVIVAL -> 1e12
            EvolutionType.ADAPTATION -> 1e15
            EvolutionType.INTELLIGENCE -> 1e18
        }
        
        return if (fitness > threshold) {
            // Would advance to next stage
            null // Placeholder - actual stage progression logic
        } else null
    }
    
    private fun generateEnvironmentalPressure(stage: EvolutionStage, timeDelta: Double): Double {
        // Simulate environmental changes
        val basePressure = stage.baseEnvironmentalPressure
        val randomVariation = Random.nextDouble(-0.2, 0.2)
        val timeVariation = sin(timeDelta * 0.1) * 0.1
        
        return basePressure * (1.0 + randomVariation + timeVariation)
    }
    
    private fun determineEvolutionStage(totalFitness: Double, currentStage: EvolutionStage): EvolutionStage {
        // Evolution stage progression based on total fitness
        val newComplexity = currentStage.complexity + (totalFitness / 1e15).toInt()
        val newIntelligence = currentStage.intelligence + (totalFitness / 1e18).toInt()
        
        return currentStage.copy(
            complexity = newComplexity.coerceAtMost(100),
            intelligence = newIntelligence.coerceAtMost(100),
            populationMass = (currentStage.populationMass * 1.1).coerceAtMost(1e12)
        )
    }
    
    private fun generateRandomEvents(environmentalPressure: Double, stage: EvolutionStage): List<EvolutionEvent> {
        val events = mutableListOf<EvolutionEvent>()
        
        if (environmentalPressure > 1.5) {
            events.add(EvolutionEvent.CLIMATE_CHANGE)
        }
        
        if (Random.nextDouble() < 0.1 && stage.complexity > 50) {
            events.add(EvolutionEvent.MASS_EXTINCTION)
        }
        
        if (Random.nextDouble() < 0.05 && stage.intelligence > 70) {
            events.add(EvolutionEvent.TECHNOLOGICAL_BREAKTHROUGH)
        }
        
        return events
    }
}

/**
 * Evolution types mapped to decoder formula β parameter
 */
enum class EvolutionType(val betaValue: Int, val displayName: String) {
    SURVIVAL(1, "Survival"),           // β=1: Momentum of life
    ADAPTATION(2, "Adaptation"),       // β=2: Force of change
    INTELLIGENCE(3, "Intelligence")    // β=3: Energy of consciousness
}

/**
 * Current evolution stage
 */
data class EvolutionStage(
    val name: String,
    val populationMass: Double,        // Total biomass
    val adaptationRate: Double,        // Base adaptation speed
    val complexity: Int,               // Organism complexity (0-100)
    val intelligence: Int,             // Intelligence level (0-100)
    val technology: Int,               // Technology level (0-100)
    val baseEnvironmentalPressure: Double,
    val unlocked: List<EvolutionTrait>
) {
    companion object {
        val PRIMORDIAL_SOUP = EvolutionStage(
            name = "Primordial Soup",
            populationMass = 1e6,
            adaptationRate = 0.1,
            complexity = 1,
            intelligence = 0,
            technology = 0,
            baseEnvironmentalPressure = 1.0,
            unlocked = listOf(EvolutionTrait.BASIC_METABOLISM)
        )
        
        val SIMPLE_CELLS = EvolutionStage(
            name = "Simple Cells",
            populationMass = 1e8,
            adaptationRate = 0.5,
            complexity = 10,
            intelligence = 1,
            technology = 0,
            baseEnvironmentalPressure = 1.2,
            unlocked = listOf(EvolutionTrait.BASIC_METABOLISM, EvolutionTrait.REPRODUCTION)
        )
        
        val COMPLEX_ORGANISMS = EvolutionStage(
            name = "Complex Organisms",
            populationMass = 1e10,
            adaptationRate = 1.0,
            complexity = 50,
            intelligence = 10,
            technology = 0,
            baseEnvironmentalPressure = 1.5,
            unlocked = listOf(EvolutionTrait.BASIC_METABOLISM, EvolutionTrait.REPRODUCTION, EvolutionTrait.SENSORY_ORGANS)
        )
        
        val INTELLIGENCE_EMERGENCE = EvolutionStage(
            name = "Intelligence Emergence",
            populationMass = 1e9,
            adaptationRate = 2.0,
            complexity = 80,
            intelligence = 50,
            technology = 10,
            baseEnvironmentalPressure = 2.0,
            unlocked = listOf(EvolutionTrait.BASIC_METABOLISM, EvolutionTrait.REPRODUCTION, 
                             EvolutionTrait.SENSORY_ORGANS, EvolutionTrait.BRAIN_DEVELOPMENT)
        )
        
        val CIVILIZATION = EvolutionStage(
            name = "Civilization",
            populationMass = 1e11,
            adaptationRate = 5.0,
            complexity = 95,
            intelligence = 90,
            technology = 70,
            baseEnvironmentalPressure = 3.0,
            unlocked = listOf(EvolutionTrait.BASIC_METABOLISM, EvolutionTrait.REPRODUCTION,
                             EvolutionTrait.SENSORY_ORGANS, EvolutionTrait.BRAIN_DEVELOPMENT,
                             EvolutionTrait.TOOL_USE, EvolutionTrait.LANGUAGE)
        )
    }
}

/**
 * Evolution traits that can be unlocked
 */
enum class EvolutionTrait(val displayName: String, val description: String) {
    BASIC_METABOLISM("Basic Metabolism", "Convert energy from environment"),
    REPRODUCTION("Reproduction", "Create offspring with variations"),
    SENSORY_ORGANS("Sensory Organs", "Detect environmental changes"),
    BRAIN_DEVELOPMENT("Brain Development", "Process information and learn"),
    TOOL_USE("Tool Use", "Manipulate environment with tools"),
    LANGUAGE("Language", "Communicate complex ideas"),
    AGRICULTURE("Agriculture", "Control food production"),
    TECHNOLOGY("Technology", "Build complex tools and machines"),
    SCIENCE("Science", "Understand natural laws"),
    SPACE_TRAVEL("Space Travel", "Expand beyond home planet")
}

/**
 * Random evolution events
 */
enum class EvolutionEvent(val displayName: String, val description: String) {
    CLIMATE_CHANGE("Climate Change", "Environmental conditions shift rapidly"),
    MASS_EXTINCTION("Mass Extinction", "Catastrophic event threatens survival"),
    TECHNOLOGICAL_BREAKTHROUGH("Breakthrough", "Major advancement unlocked"),
    GENETIC_MUTATION("Beneficial Mutation", "Positive genetic change occurs"),
    RESOURCE_DISCOVERY("Resource Discovery", "New energy source found"),
    PREDATOR_EMERGENCE("New Predator", "Dangerous species appears"),
    SYMBIOSIS_OPPORTUNITY("Symbiosis", "Chance for beneficial cooperation")
}

/**
 * Player choices affecting evolution
 */
data class PlayerChoices(
    val populationGrowthRate: Double,  // How fast to grow population
    val adaptationBonus: Double,       // Investment in adaptation
    val mutationRate: Double,          // α for survival calculations
    val selectionPressure: Double,     // α for adaptation calculations
    val intelligenceBonus: Double,     // α for intelligence calculations
    val resourceAllocation: ResourceAllocation
)

/**
 * How player allocates evolutionary resources
 */
data class ResourceAllocation(
    val survivalFocus: Double,      // 0-1: Focus on staying alive
    val adaptationFocus: Double,    // 0-1: Focus on changing with environment
    val intelligenceFocus: Double   // 0-1: Focus on developing consciousness
) {
    init {
        require(survivalFocus + adaptationFocus + intelligenceFocus == 1.0) {
            "Resource allocation must sum to 1.0"
        }
    }
}

/**
 * Result of evolution calculation
 */
data class EvolutionResult(
    val fitnessScore: Double,
    val evolutionType: EvolutionType,
    val populationMass: Double,
    val adaptationRate: Double,
    val environmentalPressure: Double,
    val evolutionIntensity: Double,
    val survivalProbability: Double,
    val nextEvolutionStage: EvolutionStage?
)

/**
 * Result of evolution simulation step
 */
data class EvolutionSimulationResult(
    val newStage: EvolutionStage,
    val survivalResult: EvolutionResult,
    val adaptationResult: EvolutionResult,
    val intelligenceResult: EvolutionResult,
    val totalFitness: Double,
    val randomEvents: List<EvolutionEvent>,
    val timeElapsed: Double
)
