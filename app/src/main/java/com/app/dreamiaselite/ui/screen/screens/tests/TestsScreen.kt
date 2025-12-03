package com.app.dreamiaselite.ui.screen.screens.tests

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

private data class SubjectCardData(
    val name: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val tint: Color,
    val bubble: Color
)

private data class UnitInfo(
    val name: String,
    val description: String,
    val availableTests: Int
)

private data class BookInfo(
    val title: String,
    val author: String,
    val summary: String,
    val availableTests: Int,
    val units: List<UnitInfo>
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TestsScreen(navController: NavController) {
    val subjects = listOf(
        SubjectCardData("History", Icons.Outlined.MenuBook, Color(0xFF3B6EFF), Color(0xFFE7F0FF)),
        SubjectCardData("Geography", Icons.Outlined.Public, Color(0xFF2F9D6B), Color(0xFFE5F5EC)),
        SubjectCardData("Polity", Icons.Outlined.AccountBalance, Color(0xFFB36AF7), Color(0xFFF3E9FF)),
        SubjectCardData("Economy", Icons.Outlined.Savings, Color(0xFFF5A524), Color(0xFFFFF4E3)),
        SubjectCardData("Environment & Ecology", Icons.Outlined.Eco, Color(0xFF2CB174), Color(0xFFE6F8EE)),
        SubjectCardData("Science & Technology", Icons.Outlined.Science, Color(0xFFDF6CC6), Color(0xFFFCEBFF))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Prelims Preparation",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        )
        Text(
            text = "Select a subject to view resources",
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
        )

        HighlightCard()

        FlowRow(
            maxItemsInEachRow = 2,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            subjects.forEach { subject ->
                SubjectCard(
                    subject = subject,
                    modifier = Modifier.fillMaxWidth(0.48f),
                    onClick = {
                        val encoded = Uri.encode(subject.name)
                        navController.navigate("test_books/$encoded")
                    }
                )
            }
        }
        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun HighlightCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1F73FF)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "Choose Your Subject",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            )
            Text(
                text = "Select a subject to explore recommended reference books and practice tests for UPSC Prelims",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.White.copy(alpha = 0.9f)
                )
            )
        }
    }
}

@Composable
private fun SubjectCard(subject: SubjectCardData, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(subject.bubble),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = subject.icon,
                    contentDescription = subject.name,
                    tint = subject.tint,
                    modifier = Modifier.size(26.dp)
                )
            }

            Text(
                text = subject.name,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun SubjectBooksScreen(subjectName: String, navController: NavController) {
    val books = buildSubjectBooks()[subjectName] ?: emptyList()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Outlined.ArrowBack, contentDescription = "Back")
            }
            Column {
                Text(
                    text = subjectName,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Text(
                    text = "Pick a reference book to unlock practice tests for $subjectName.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                )
            }
        }

        FullLengthTestCard(
            subjectName = subjectName,
            onStart = {
                val encoded = Uri.encode("$subjectName Full Length")
                navController.navigate("test_session/$encoded")
            }
        )

        books.forEach { book ->
            BookCard(
                book = book,
                onOpenUnits = {
                    val encodedSubject = Uri.encode(subjectName)
                    val encodedBook = Uri.encode(book.title)
                    navController.navigate("test_units/$encodedSubject/$encodedBook")
                }
            )
        }

        if (books.isEmpty()) {
            Text(
                text = "No reference books configured for this subject yet.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            )
        }
    }
}

@Composable
private fun FullLengthTestCard(subjectName: String, onStart: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Full-Length Subject Test",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            Text(
                text = "Shuffles 50–60 MCQs across all units for $subjectName.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            )
            Button(
                onClick = onStart,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
            ) {
                Text("Start Full-Length Test")
            }
        }
    }
}

@Composable
private fun BookCard(
    book: BookInfo,
    onOpenUnits: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenUnits() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = book.title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            Text(
                text = book.author,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            )
            Text(
                text = book.summary,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${book.availableTests} tests available",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                )
                Button(
                    onClick = onOpenUnits,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("Open units")
                }
            }
        }
    }
}

@Composable
fun BookUnitsScreen(subjectName: String, bookTitle: String, navController: NavController) {
    val book = buildSubjectBooks()[subjectName]?.firstOrNull { it.title == bookTitle }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Outlined.ArrowBack, contentDescription = "Back")
            }
            Column {
                Text(
                    text = bookTitle,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Text(
                    text = "Tests tailored for $bookTitle ($subjectName).",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                )
            }
        }

        if (book == null) {
            Text(
                text = "No units found for this book.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            )
            return
        }

        book.units.take(9).forEach { unit ->
            UnitCard(
                unit = unit,
                onStart = {
                    val encoded = Uri.encode("${book.title} - ${unit.name}")
                    navController.navigate("test_session/$encoded")
                }
            )
        }
    }
}

@Composable
private fun UnitCard(unit: UnitInfo, onStart: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = unit.name,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            Text(
                text = unit.description,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            )
            Text(
                text = "${unit.availableTests} unit tests available",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            )
            Button(
                onClick = onStart,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
            ) {
                Text("Start Unit Test")
            }
        }
    }
}

@Composable
private fun UnitRow(index: Int, unit: UnitInfo, onStart: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = "${index + 1}. ${unit.name}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
            )
            Text(
                text = unit.description,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            )
            Text(
                text = "${unit.availableTests} unit tests available",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            )
        }
        Button(
            onClick = onStart,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            modifier = Modifier.height(34.dp)
        ) {
            Text("Start")
        }
    }
}

private fun buildSubjectBooks(): Map<String, List<BookInfo>> {
    fun units(vararg items: UnitInfo) = items.toList()
    fun book(title: String, author: String, summary: String, units: List<UnitInfo>) =
        BookInfo(title, author, summary, units.size, units)

    val history = listOf(
        book(
            "Spectrum – Modern India",
            "Rajiv Ahir",
            "Modern history essentials with chapter drills.",
            units(
                UnitInfo("Revolt & Reform", "1857 and socio-religious reformers", 2),
                UnitInfo("Moderates & Extremists", "Congress phases, partition of Bengal", 2),
                UnitInfo("Gandhian Mass Movements", "Non-Cooperation to Quit India", 2),
                UnitInfo("Towards Independence", "Cabinet Mission to 1947", 1)
            )
        ),
        book(
            "Ancient & Medieval India",
            "Poonam Dalal Dahiya",
            "Clear narrative for early periods.",
            units(
                UnitInfo("Indus & Vedic Age", "Culture, polity, economy", 1),
                UnitInfo("Mahajanapadas to Mauryas", "State formation and administration", 1),
                UnitInfo("Post-Mauryan & Gupta", "Sangam, Kushan, Gupta polity", 1)
            )
        ),
        book(
            "Plassey to Partition & After",
            "Sekhar Bandyopadhyay",
            "Analytical modern history with focus on freedom struggle and aftermath.",
            units(
                UnitInfo("Company Rule", "Expansion, reforms and economic impact", 1),
                UnitInfo("Nationalism & Partition", "Mass politics, communalism, 1947", 1),
                UnitInfo("Post-1947 Consolidation", "Integration, reforms, constitutional phase", 1)
            )
        ),
        book(
            "India's Struggle for Independence",
            "Bipan Chandra",
            "Authoritative narrative of the freedom movement.",
            units(
                UnitInfo("Early Nationalism", "Foundations of Congress, moderates vs extremists", 1),
                UnitInfo("Gandhian Era", "Mass movements, constructive programme", 1),
                UnitInfo("Towards Freedom", "War years, negotiations, partition dynamics", 1)
            )
        ),
        book(
            "History of Modern India",
            "Bipan Chandra",
            "Concise modern history with focus on colonial impact.",
            units(
                UnitInfo("Reforms & Revolts", "18th–19th century policies, uprising patterns", 1),
                UnitInfo("National Movement Phases", "Moderate, extremist, Gandhian phases", 1),
                UnitInfo("Post-1947 Integration", "Princely states, constitution making", 1)
            )
        )
    )

    val geography = listOf(
        book(
            "NCERT Fundamentals of Physical Geography",
            "NCERT",
            "Physical processes and landforms.",
            units(
                UnitInfo("Earth & Plate Tectonics", "Interior, plates, quakes, volcanoes", 1),
                UnitInfo("Geomorphic Processes", "Weathering, erosion, deposition", 1),
                UnitInfo("Climatology Basics", "Heat budget, winds, rainfall", 1)
            )
        ),
        book(
            "G.C. Leong Physical Geography",
            "G.C. Leong",
            "World climates and physical bases.",
            units(
                UnitInfo("World Climates", "Controls and types", 1),
                UnitInfo("Soils & Biomes", "Formation and distribution", 1)
            )
        ),
        book(
            "NCERT India: Physical Environment",
            "NCERT",
            "Indian physiography, drainage, climate, and natural resources.",
            units(
                UnitInfo("Physiography of India", "Relief, Himalayas, Peninsular plateau, plains", 1),
                UnitInfo("Drainage & Climate", "Rivers, monsoon system, weather patterns", 1),
                UnitInfo("Natural Hazards", "Disasters, mitigation, vulnerability mapping", 1)
            )
        ),
        book(
            "Oxford School Atlas",
            "Oxford",
            "Map-based learning for India and world geography.",
            units(
                UnitInfo("India Maps", "States, rivers, infrastructure, environment", 1),
                UnitInfo("World Maps", "Countries, capitals, physical features", 1),
                UnitInfo("Thematic Maps", "Climate, resources, population themes", 1)
            )
        ),
        book(
            "NCERT India: People and Economy",
            "NCERT",
            "Human and economic geography of India.",
            units(
                UnitInfo("Resources & Agriculture", "Resource distribution, farming systems", 1),
                UnitInfo("Industries & Transport", "Industrial regions, logistics, trade", 1),
                UnitInfo("Human Geography", "Population, migration, settlements", 1)
            )
        )
    )

    val polity = listOf(
        book(
            "Laxmikanth Indian Polity",
            "M. Laxmikanth",
            "Comprehensive polity reference.",
            units(
                UnitInfo("Constitutional Framework", "Preamble, features, schedules", 1),
                UnitInfo("Rights & DPSP", "FRs, DPSP, Duties", 1),
                UnitInfo("Legislature & Executive", "Parliament, President, Governor", 1)
            )
        ),
        book(
            "DD Basu – Constitution of India",
            "D.D. Basu",
            "Commentary on constitutional law.",
            units(
                UnitInfo("Federalism & Emergencies", "Centre-state, emergency powers", 1),
                UnitInfo("Civil Liberties", "Freedom, equality, remedies", 1)
            )
        ),
        book(
            "PM Bakshi – Constitution of India (Bare Act)",
            "P.M. Bakshi",
            "Article-wise reference for quick fact checks and legal wording.",
            units(
                UnitInfo("Articles & Parts", "Text of key articles and parts", 1),
                UnitInfo("Schedules & Amendments", "Latest constitutional amendments, schedules", 1),
                UnitInfo("Landmark Judgments", "Essential SC cases for prelims", 1)
            )
        ),
        book(
            "Our Parliament",
            "Subhash Kashyap",
            "Plain-language guide to Parliament's working.",
            units(
                UnitInfo("Parliament Basics", "Composition, sessions, procedures", 1),
                UnitInfo("Committees & Oversight", "PAC, estimates, privileges", 1),
                UnitInfo("Presiding Officers", "Speaker, Chairman roles and powers", 1)
            )
        ),
        book(
            "Governance in India",
            "M. Laxmikanth",
            "Governance, accountability, and e-governance coverage.",
            units(
                UnitInfo("Governance Concepts", "Governance indicators, transparency, RTI", 1),
                UnitInfo("Programmes & Delivery", "Flagship schemes, social sector delivery", 1),
                UnitInfo("e-Governance & Civil Services", "Digital initiatives, civil services roles", 1)
            )
        )
    )

    val economy = listOf(
        book(
            "Ramesh Singh Indian Economy",
            "Ramesh Singh",
            "Macro and sectoral view for UPSC.",
            units(
                UnitInfo("Growth & Development", "Concepts and indicators", 1),
                UnitInfo("Fiscal Policy", "Budget, FRBM, deficits", 1),
                UnitInfo("Monetary & Inflation", "RBI, MPC, prices", 1)
            )
        ),
        book(
            "Sanjiv Verma Indian Economy",
            "Sanjiv Verma",
            "Concise coverage for prelims.",
            units(
                UnitInfo("National Income", "Methods and issues", 1),
                UnitInfo("Poverty & Unemployment", "Measures and schemes", 1)
            )
        ),
        book(
            "Economic Survey (Key Chapters)",
            "MoF Summary",
            "Core graphs, data trends, and policy themes from the latest survey.",
            units(
                UnitInfo("Growth & Fiscal Trends", "GDP outlook, deficits, fiscal stance", 1),
                UnitInfo("Sectoral Highlights", "Agriculture, industry, services updates", 1),
                UnitInfo("Reforms & Schemes", "Flagship initiatives and reforms focus", 1)
            )
        ),
        book(
            "Sriram IAS Economy Notes",
            "Sriram IAS",
            "Coaching-style concise notes for macro and sectoral topics.",
            units(
                UnitInfo("Macroeconomy Basics", "GDP, inflation, policy tools", 1),
                UnitInfo("External Sector", "BoP, trade policy, currency management", 1),
                UnitInfo("Financial Sector", "Banking, NBFCs, markets, regulation", 1)
            )
        ),
        book(
            "NCERT Economics (11-12) – Essentials",
            "NCERT",
            "Micro, macro, and Indian economic development fundamentals.",
            units(
                UnitInfo("Microeconomics Core", "Demand-supply, markets, elasticity", 1),
                UnitInfo("Macroeconomics Core", "National income, money, government budget", 1),
                UnitInfo("Indian Economic Development", "Planning, reforms, inclusive growth", 1)
            )
        )
    )

    val environment = listOf(
        book(
            "Shankar IAS Environment",
            "Shankar IAS",
            "Flagship environment guide.",
            units(
                UnitInfo("Ecology Basics", "Ecosystems and services", 1),
                UnitInfo("Biodiversity", "Hotspots, conservation", 1)
            )
        ),
        book(
            "NCERT Bio (Environment Units)",
            "NCERT",
            "NCERT chapters on environment.",
            units(
                UnitInfo("Organisms & Populations", "Interactions and adaptations", 1),
                UnitInfo("Environmental Issues", "Pollution and mitigation", 1)
            )
        ),
        book(
            "PMF IAS Environment",
            "PMF IAS",
            "Concise notes with diagrams covering ecology, biodiversity, and conventions.",
            units(
                UnitInfo("Ecology & Cycles", "Ecosystem dynamics, cycles, services", 1),
                UnitInfo("Conventions & Acts", "International agreements, Indian laws", 1),
                UnitInfo("Climate & Pollution", "Climate change basics, pollutants, control", 1)
            )
        ),
        book(
            "Majid Husain Environment & Ecology",
            "Majid Husain",
            "UPSC-focused environment text with maps and examples.",
            units(
                UnitInfo("Ecology Foundations", "Ecosystems, food chains, biogeochemical cycles", 1),
                UnitInfo("Biodiversity & Protected Areas", "Flora-fauna, PAs, IUCN categories", 1),
                UnitInfo("Climate & Disaster", "Climate change, disaster management basics", 1)
            )
        ),
        book(
            "Down To Earth Compendium",
            "CEE/DTL",
            "Annual compilations on environment, climate, and governance.",
            units(
                UnitInfo("Pollution & Waste", "Air, water, waste management case studies", 1),
                UnitInfo("Climate & Energy", "COP updates, renewables, transitions", 1),
                UnitInfo("Biodiversity & Governance", "Wildlife updates, policies, schemes", 1)
            )
        )
    )

    val science = listOf(
        book(
            "NCERT Science (Class 9-10)",
            "NCERT",
            "School-level science recap.",
            units(
                UnitInfo("Physics Basics", "Motion, work, energy", 1),
                UnitInfo("Chemistry Basics", "Atoms, bonding, reactions", 1),
                UnitInfo("Biology Basics", "Life processes, health", 1)
            )
        ),
        book(
            "Lucent General Science",
            "Lucent",
            "Quick reference for basics.",
            units(
                UnitInfo("Everyday Science", "Practical applications", 1),
                UnitInfo("Space & Nuclear", "Basics of space and nuclear tech", 1)
            )
        ),
        book(
            "NCERT Science (Class 11-12) – Condensed",
            "NCERT",
            "Higher secondary fundamentals for physics, chemistry, and biology.",
            units(
                UnitInfo("Physics Foundations", "Waves, optics, electricity basics", 1),
                UnitInfo("Chemistry Foundations", "Periodic table, bonding, organic basics", 1),
                UnitInfo("Biology Foundations", "Cell, genetics, human physiology", 1)
            )
        ),
        book(
            "Arihant General Science",
            "Arihant",
            "Objective-style recap of physics, chemistry, and biology for prelims.",
            units(
                UnitInfo("Physics Quick Recap", "Kinematics, electricity, modern physics", 1),
                UnitInfo("Chemistry Quick Recap", "Atomic structure, reactions, everyday chem", 1),
                UnitInfo("Biology Quick Recap", "Human body systems, botany basics", 1)
            )
        ),
        book(
            "Science & Technology for UPSC",
            "TMH/McGraw Hill",
            "Applied S&T topics with current affairs linkage.",
            units(
                UnitInfo("Space & Defence Tech", "ISRO, DRDO, missiles, space missions", 1),
                UnitInfo("ICT & Emerging Tech", "AI, quantum, cyber, biotech basics", 1),
                UnitInfo("Energy & Enviro Tech", "Renewables, batteries, green tech", 1)
            )
        )
    )

    return mapOf(
        "History" to history,
        "Geography" to geography,
        "Polity" to polity,
        "Economy" to economy,
        "Environment & Ecology" to environment,
        "Science & Technology" to science
    )
}
