package com.corridor.os.android.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.corridor.os.android.ui.components.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TutorialScreen(
    onComplete: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { tutorialPages.size })
    var currentPage by remember { mutableIntStateOf(0) }
    
    LaunchedEffect(pagerState.currentPage) {
        currentPage = pagerState.currentPage
    }
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Progress indicator
        LinearProgressIndicator(
            progress = { (currentPage + 1).toFloat() / tutorialPages.size },
            modifier = Modifier.fillMaxWidth()
        )
        
        // Tutorial content
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            TutorialPage(tutorialPages[page])
        }
        
        // Navigation buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (currentPage > 0) {
                OutlinedButton(
                    onClick = {
                        currentPage--
                        // Animate to previous page
                    }
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Previous")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Previous")
                }
            } else {
                Spacer(modifier = Modifier.width(1.dp))
            }
            
            if (currentPage < tutorialPages.size - 1) {
                Button(
                    onClick = {
                        currentPage++
                        // Animate to next page
                    }
                ) {
                    Text("Next")
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.ArrowForward, contentDescription = "Next")
                }
            } else {
                Button(
                    onClick = onComplete
                ) {
                    Text("Start Evolution!")
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.PlayArrow, contentDescription = "Start")
                }
            }
        }
    }
}

@Composable
fun TutorialPage(page: TutorialPageData) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
        
        item {
            PulsingIcon(
                icon = page.icon,
                contentDescription = page.title,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary,
                isAnimating = true
            )
        }
        
        item {
            Text(
                text = page.title,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary
            )
        }
        
        item {
            Text(
                text = page.description,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        
        if (page.formula != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = page.formula,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        
                        if (page.formulaExplanation != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = page.formulaExplanation,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }
        }
        
        itemsIndexed(page.bulletPoints) { index, point ->
            SlideInCard(
                content = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Circle,
                            contentDescription = null,
                            modifier = Modifier.size(8.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = point,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                },
                delay = index * 100
            )
        }
    }
}

data class TutorialPageData(
    val title: String,
    val description: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val formula: String? = null,
    val formulaExplanation: String? = null,
    val bulletPoints: List<String> = emptyList()
)

val tutorialPages = listOf(
    TutorialPageData(
        title = "Welcome to Evolution Decoder",
        description = "Discover how physics principles govern the evolution of life, from the first microbes to space-faring civilizations.",
        icon = Icons.Default.Biotech,
        bulletPoints = listOf(
            "Guide life through 6 major evolutionary stages",
            "Use real physics formulas to calculate evolutionary fitness",
            "Make strategic decisions about survival, adaptation, and intelligence",
            "Experience the mathematical beauty of life's emergence",
            "Completely free with no ads or purchases"
        )
    ),
    
    TutorialPageData(
        title = "The Physics of Evolution",
        description = "Your decisions are powered by a unified physics formula that connects momentum, force, and energy to evolutionary processes.",
        icon = Icons.Default.Functions,
        formula = "Q(β) = m · a^(α·δ_β,2) · c^(α·(δ_β,1+δ_β,3))",
        formulaExplanation = "Where m=population, a=adaptation rate, c=environmental pressure, α=evolution intensity",
        bulletPoints = listOf(
            "β=1: Survival (momentum of life persisting)",
            "β=2: Adaptation (force of evolutionary change)", 
            "β=3: Intelligence (energy of consciousness)",
            "Real physics principles govern every calculation"
        )
    ),
    
    TutorialPageData(
        title = "Evolution Stages",
        description = "Guide life through six major evolutionary milestones, each with unique challenges and opportunities.",
        icon = Icons.Default.Timeline,
        bulletPoints = listOf(
            "🦠 Primordial Soup: Develop basic metabolism",
            "🔬 Simple Cells: Evolve cellular complexity",
            "🐟 Complex Organisms: Multicellular specialization",
            "🧠 Intelligence: Consciousness emergence",
            "🏛️ Civilization: Technology and culture",
            "🚀 Space Age: Transcend planetary limits"
        )
    ),
    
    TutorialPageData(
        title = "Player Controls",
        description = "Adjust evolutionary parameters to guide your species through challenges and opportunities.",
        icon = Icons.Default.Tune,
        bulletPoints = listOf(
            "Population Growth: How fast your species reproduces",
            "Adaptation Speed: How quickly you respond to changes",
            "Mutation Rate (α): Genetic variation intensity",
            "Selection Pressure (α): Environmental selection strength",
            "Intelligence Focus (α): Investment in consciousness development"
        )
    ),
    
    TutorialPageData(
        title = "Random Events",
        description = "Evolution is unpredictable! Respond to random events that challenge or accelerate your species' development.",
        icon = Icons.Default.Event,
        bulletPoints = listOf(
            "🌋 Mass Extinctions: Test your survival strategies",
            "🌡️ Climate Changes: Adapt to new environments",
            "🧬 Beneficial Mutations: Lucky genetic improvements",
            "🔬 Technological Breakthroughs: Leap forward in development",
            "🤝 Symbiosis Opportunities: Cooperative evolution"
        )
    ),
    
    TutorialPageData(
        title = "Educational Goals",
        description = "Learn real evolutionary biology and physics while playing. Every calculation is scientifically accurate.",
        icon = Icons.Default.School,
        bulletPoints = listOf(
            "Understand how mathematical principles govern evolution",
            "Explore the emergence of consciousness from simple matter",
            "Learn about environmental pressures and adaptation",
            "Discover the physics behind life's complexity",
            "Experience the scientific method through gameplay"
        )
    ),
    
    TutorialPageData(
        title = "Ready to Evolve!",
        description = "You're ready to begin the greatest journey in the universe: the evolution of life itself. Guide your species wisely!",
        icon = Icons.Default.Rocket,
        bulletPoints = listOf(
            "Start with simple organisms in primordial soup",
            "Make strategic choices about evolution parameters",
            "Respond to environmental challenges and opportunities",
            "Unlock new traits and capabilities",
            "Reach for the stars and beyond!"
        )
    )
)
