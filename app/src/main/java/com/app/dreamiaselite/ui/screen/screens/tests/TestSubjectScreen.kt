package com.app.dreamiaselite.ui.screen.screens.tests

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
//import androidx.compose.runtime.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.io.Serializable
import kotlinx.coroutines.delay
import android.widget.Toast
import androidx.compose.foundation.layout.Spacer

data class TestQuestion(
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
) : Serializable

data class TestResultData(
    val subject: String,
    val questions: List<TestQuestion>,
    val selected: List<Int?>,
    val timeTakenSeconds: Int
) : Serializable



// ----------------- Test Session Screen -----------------

@Composable
fun TestSessionScreen(subjectName: String, navController: NavController) {
    val questions = remember { generateQuestionsFor(subjectName) }
    val totalTimeSeconds = 10 * 60
    var timeLeft by rememberSaveable { mutableStateOf(totalTimeSeconds) }
    var currentIndex by rememberSaveable { mutableStateOf(0) }
    var selected by rememberSaveable { mutableStateOf(List(questions.size) { null as Int? }) }
    var bookmarks by rememberSaveable { mutableStateOf(List(questions.size) { false }) }
    var completed by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    val displayTitle = subjectName.substringAfterLast(" - ", subjectName)
    val isBookmarked = bookmarks.getOrElse(currentIndex) { false }

    LaunchedEffect(completed) {
        while (!completed && timeLeft > 0) {
            delay(1000)
            timeLeft--
            if (timeLeft == 0) {
                submitTest(subjectName, questions, selected, totalTimeSeconds - timeLeft, navController)
                completed = true
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(MaterialTheme.colorScheme.background, MaterialTheme.colorScheme.surface)
                )
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = displayTitle,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Text(
                    text = "Question ${currentIndex + 1} of ${questions.size}",
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = {
                        bookmarks = bookmarks.toMutableList().also { it[currentIndex] = !isBookmarked }
                        Toast.makeText(
                            context,
                            if (!isBookmarked) "Bookmarked this question" else "Bookmark removed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                ) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Outlined.Star else Icons.Outlined.StarBorder,
                        contentDescription = "Bookmark question",
                        tint = if (isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                }
                TimerPill(timeLeft)
            }
        }

        QuestionCard(
            question = questions[currentIndex],
            selectedIndex = selected[currentIndex],
            onSelect = { chosen ->
                selected = selected.toMutableList().also { it[currentIndex] = chosen }
            }
        )

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                enabled = currentIndex > 0,
                onClick = { currentIndex-- },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                Text("Previous")
            }

            Button(
                onClick = {
                    if (currentIndex < questions.lastIndex) {
                        currentIndex++
                    } else {
                        submitTest(subjectName, questions, selected, totalTimeSeconds - timeLeft, navController)
                        completed = true
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = if (currentIndex < questions.lastIndex) "Next" else "Submit",
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Composable
private fun TimerPill(timeLeft: Int) {
    val minutes = timeLeft / 60
    val seconds = timeLeft % 60
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = String.format("%02d:%02d", minutes, seconds),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
        )
    }
}

@Composable
private fun QuestionCard(
    question: TestQuestion,
    selectedIndex: Int?,
    onSelect: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = question.question,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )

            question.options.forEachIndexed { index, option ->
                AnswerOption(
                    text = option,
                    selected = selectedIndex == index,
                    onClick = { onSelect(index) }
                )
            }
        }
    }
}

@Composable
private fun AnswerOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
    val backgroundColor = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .clickable { onClick() },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface)
        )
        if (selected) {
            Icon(
                imageVector = Icons.Outlined.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

private fun submitTest(
    subject: String,
    questions: List<TestQuestion>,
    selected: List<Int?>,
    timeTaken: Int,
    navController: NavController
) {
    val result = TestResultData(
        subject = subject,
        questions = questions,
        selected = selected,
        timeTakenSeconds = timeTaken
    )
    navController.currentBackStackEntry?.savedStateHandle?.set("test_result", result)
    navController.navigate("test_result")
}

private fun generateQuestionsFor(subject: String): List<TestQuestion> {
    val pool = when {
        subject.contains("History", ignoreCase = true) -> historyQuestions()
        subject.contains("Geography", ignoreCase = true) -> geographyQuestions()
        subject.contains("Polity", ignoreCase = true) -> polityQuestions()
        subject.contains("Economy", ignoreCase = true) -> economyQuestions()
        subject.contains("Environment", ignoreCase = true) -> environmentQuestions()
        subject.contains("Science", ignoreCase = true) -> scienceQuestions()
        else -> generalQuestions()
    }
    return pool.take(10)
}

private fun historyQuestions() = listOf(
    TestQuestion(
        "Who founded the Mauryan Empire?",
        listOf("Chandragupta Maurya", "Ashoka", "Bindusara", "Bimbisara"),
        0,
        "Chandragupta Maurya founded the Mauryan Empire in 322 BCE with guidance from Chanakya."
    ),
    TestQuestion(
        "The Battle of Plassey (1757) was fought between?",
        listOf("British and Marathas", "British and Nawab of Bengal", "French and British", "Mughals and British"),
        1,
        "The battle was between the British East India Company and Siraj-ud-Daulah, Nawab of Bengal."
    ),
    TestQuestion(
        "Who led the Khilafat Movement?",
        listOf("Nehru", "Maulana Azad", "Ali Brothers", "Gandhi"),
        2,
        "The Ali brothers, Mohammad Ali and Shaukat Ali, were prominent leaders of the Khilafat Movement."
    ),
    TestQuestion(
        "Dandi March was associated with?",
        listOf("Non-Cooperation", "Civil Disobedience", "Quit India", "Swadeshi"),
        1,
        "It launched the Civil Disobedience Movement against the salt tax in 1930."
    ),
    TestQuestion(
        "Capital of the Pallavas was?",
        listOf("Kanchipuram", "Madurai", "Tanjore", "Pataliputra"),
        0,
        "Kanchipuram served as the capital of the Pallava dynasty."
    ),
    TestQuestion(
        "Who wrote 'Anand Math'?",
        listOf("Bankim Chandra Chatterjee", "Rabindranath Tagore", "Saratchandra", "Premchand"),
        0,
        "Bankim Chandra Chatterjee authored 'Anand Math', which popularized 'Vande Mataram'."
    ),
    TestQuestion(
        "Leader associated with Bardoli Satyagraha?",
        listOf("Patel", "Nehru", "Tilak", "Gokhale"),
        0,
        "Sardar Vallabhbhai Patel led Bardoli Satyagraha in 1928."
    ),
    TestQuestion(
        "Indus Valley script is?",
        listOf("Deciphered", "Undeciphered", "Greek-based", "Brahmi-derived"),
        1,
        "The script remains undeciphered with no confirmed linkage."
    ),
    TestQuestion(
        "First Governor-General of independent India?",
        listOf("Mountbatten", "C. Rajagopalachari", "Nehru", "Linlithgow"),
        0,
        "Lord Mountbatten served as the first Governor-General after independence."
    ),
    TestQuestion(
        "Who gave the slogan 'Swaraj is my birthright'?",
        listOf("Tilak", "Gandhi", "Nehru", "Bose"),
        0,
        "Bal Gangadhar Tilak coined the slogan asserting the right to self-rule."
    )
)

private fun geographyQuestions() = listOf(
    TestQuestion(
        "The Tropic of Cancer passes through how many Indian states?",
        listOf("6", "7", "8", "9"),
        2,
        "It passes through 8 states including Gujarat, Rajasthan, MP, Chhattisgarh, Jharkhand, WB, Tripura, and Mizoram."
    ),
    TestQuestion(
        "Longest west-flowing river in India?",
        listOf("Narmada", "Tapti", "Mahi", "Sabarmati"),
        0,
        "The Narmada is the longest west-flowing river in India."
    ),
    TestQuestion(
        "Highest peak of Aravalli Range?",
        listOf("Guru Shikhar", "Doddabetta", "Anamudi", "Dhupgarh"),
        0,
        "Guru Shikhar in Rajasthan is the highest point of the Aravalli range."
    ),
    TestQuestion(
        "Type of rainfall caused by mountains?",
        listOf("Cyclonic", "Frontal", "Orographic", "Convectional"),
        2,
        "Orographic rainfall occurs when moist air rises over mountains."
    ),
    TestQuestion(
        "The largest delta in the world is?",
        listOf("Amazon", "Ganga-Brahmaputra", "Niger", "Mekong"),
        1,
        "The Ganga-Brahmaputra (Sundarbans) delta is the largest delta globally."
    ),
    TestQuestion(
        "Which soil is richest in humus?",
        listOf("Black", "Alluvial", "Red", "Laterite"),
        1,
        "Alluvial soils, especially in the Gangetic plains, are rich in humus and fertility."
    ),
    TestQuestion(
        "El Niño refers to?",
        listOf("Cold current", "Warm current in Pacific", "Monsoon wind", "Hurricane"),
        1,
        "El Niño is a periodic warming of the central and eastern Pacific Ocean affecting global weather."
    ),
    TestQuestion(
        "Highest waterfall in India by height?",
        listOf("Jog", "Dudhsagar", "Nohkalikai", "Kunchikal"),
        2,
        "Nohkalikai Falls in Meghalaya is the tallest plunge waterfall in India."
    ),
    TestQuestion(
        "Coral reefs in India are NOT found in?",
        listOf("Lakshadweep", "Andaman", "Sundarbans", "Gulf of Mannar"),
        2,
        "Sundarbans have mangroves; notable coral reefs are in Lakshadweep, Andamans, and Gulf of Mannar."
    ),
    TestQuestion(
        "The jet streams influence which Indian season most?",
        listOf("Winter", "Pre-monsoon", "Monsoon", "Post-monsoon"),
        2,
        "Jet streams have a significant role in the onset and withdrawal of the southwest monsoon."
    )
)

private fun polityQuestions() = listOf(
    TestQuestion(
        "Right to property is now a?",
        listOf("Fundamental Right", "Directive Principle", "Legal/Constitutional Right", "Natural Right"),
        2,
        "After the 44th Amendment, right to property became a constitutional/legal right under Article 300A."
    ),
    TestQuestion(
        "Which schedule lists languages?",
        listOf("7th Schedule", "8th Schedule", "9th Schedule", "10th Schedule"),
        1,
        "The 8th Schedule lists the recognized languages of India."
    ),
    TestQuestion(
        "Money Bill can be introduced in?",
        listOf("Lok Sabha only", "Rajya Sabha only", "Either House", "Joint Sitting"),
        0,
        "A Money Bill is introduced only in the Lok Sabha on the recommendation of the President."
    ),
    TestQuestion(
        "Maximum gap between two sessions of Parliament?",
        listOf("3 months", "4 months", "6 months", "12 months"),
        2,
        "There should not be more than a six-month gap between two sessions."
    ),
    TestQuestion(
        "Which Article deals with Amendment procedure?",
        listOf("356", "360", "368", "370"),
        2,
        "Article 368 lays down the procedure for constitutional amendments."
    ),
    TestQuestion(
        "Chairman of Rajya Sabha is?",
        listOf("President", "Vice President", "Prime Minister", "Speaker"),
        1,
        "The Vice President of India is the ex-officio Chairman of the Rajya Sabha."
    ),
    TestQuestion(
        "Fundamental Duties are in which part?",
        listOf("Part III", "Part IVA", "Part IV", "Part V"),
        1,
        "Fundamental Duties were added in Part IVA by the 42nd Amendment."
    ),
    TestQuestion(
        "Anti-defection provisions are in which Schedule?",
        listOf("7th", "8th", "9th", "10th"),
        3,
        "The Tenth Schedule contains anti-defection provisions."
    ),
    TestQuestion(
        "Minimum age for Rajya Sabha membership?",
        listOf("25", "30", "35", "40"),
        1,
        "A person must be at least 30 years old to be a Rajya Sabha member."
    ),
    TestQuestion(
        "Who appoints the CAG?",
        listOf("President", "Prime Minister", "Parliament", "Cabinet"),
        0,
        "The President appoints the Comptroller and Auditor General of India."
    )
)

private fun economyQuestions() = listOf(
    TestQuestion(
        "Repo rate is?",
        listOf("Rate banks lend to RBI", "Rate RBI lends to banks", "Rate for term deposits", "Rate for savings"),
        1,
        "Repo rate is the rate at which RBI lends to commercial banks."
    ),
    TestQuestion(
        "GDP at constant prices accounts for?",
        listOf("Inflation", "Population", "No inflation", "Exports only"),
        2,
        "Constant prices remove the effect of inflation to measure real growth."
    ),
    TestQuestion(
        "FRBM stands for?",
        listOf("Fiscal Responsibility and Budget Management", "Financial Reform and Banking Mandate", "Fiscal Reserve Bank Mandate", "Financial Ratio Balance Management"),
        0,
        "FRBM Act aims at fiscal discipline for the Government of India."
    ),
    TestQuestion(
        "Disguised unemployment is seen in?",
        listOf("Capital intensive industry", "IT sector", "Seasonal jobs", "Over-crowded agriculture"),
        3,
        "Too many workers on a small farm contribute less than proportionally—disguised unemployment."
    ),
    TestQuestion(
        "FDI stands for?",
        listOf("Foreign Direct Investment", "Foreign Domestic Investment", "Federal Direct Investment", "Foreign Debt Investment"),
        0,
        "FDI is investment from a foreign entity into business interests in another country."
    ),
    TestQuestion(
        "GST is a ____ tax.",
        listOf("Direct", "Progressive", "Destination-based indirect", "Origin-based indirect"),
        2,
        "GST is a destination-based indirect tax on consumption."
    ),
    TestQuestion(
        "Which body publishes IIP?",
        listOf("NITI Aayog", "RBI", "NSO", "CSO & RBI jointly"),
        2,
        "The National Statistical Office (NSO) releases the Index of Industrial Production."
    ),
    TestQuestion(
        "MSP is recommended by?",
        listOf("CACP", "FAO", "NABARD", "SEBI"),
        0,
        "The Commission for Agricultural Costs and Prices recommends MSPs."
    ),
    TestQuestion(
        "Inflation targeting is managed by?",
        listOf("NABARD", "RBI MPC", "SEBI", "SIDBI"),
        1,
        "The Monetary Policy Committee of the RBI handles inflation targeting."
    ),
    TestQuestion(
        "Balance of Payments includes?",
        listOf("Current & Capital accounts", "Only Current account", "Only Capital account", "FX reserves only"),
        0,
        "BoP records both current and capital account transactions."
    )
)

private fun environmentQuestions() = listOf(
    TestQuestion(
        "Montreal Protocol addresses?",
        listOf("Climate change", "Ozone depletion", "Biodiversity", "Desertification"),
        1,
        "The protocol targets substances that deplete the ozone layer."
    ),
    TestQuestion(
        "CITES deals with?",
        listOf("Migratory birds", "Endangered species trade", "Climate finance", "Marine pollution"),
        1,
        "CITES regulates international trade of endangered species."
    ),
    TestQuestion(
        "Ramsar sites are related to?",
        listOf("Forests", "Wetlands", "Deserts", "Mountains"),
        1,
        "Ramsar Convention identifies and conserves wetlands of international importance."
    ),
    TestQuestion(
        "Chipko movement was about?",
        listOf("Pollution control", "Forest conservation", "River cleaning", "Anti-dam"),
        1,
        "Chipko was a forest conservation movement where people hugged trees to prevent felling."
    ),
    TestQuestion(
        "Biodiversity hotspots in India include?",
        listOf("Western Ghats & Himalaya", "Indo-Gangetic Plains", "Thar Desert", "Deccan Plateau"),
        0,
        "Two of the global hotspots are in India: Western Ghats and Himalaya."
    ),
    TestQuestion(
        "Paris Agreement aims to limit warming to?",
        listOf("1.0°C", "1.5°C-2°C", "2.5°C", "3°C"),
        1,
        "The agreement seeks to keep global temperature rise well below 2°C, preferably 1.5°C."
    ),
    TestQuestion(
        "National Clean Air Programme target year?",
        listOf("2022", "2024", "2025-26", "2030"),
        2,
        "NCAP targets 20-30% reduction in PM2.5 and PM10 concentrations by 2024-26; extended targets around 2025-26."
    ),
    TestQuestion(
        "Primary greenhouse gas with highest concentration?",
        listOf("CO2", "CH4", "N2O", "O3"),
        0,
        "CO2 is the most abundant anthropogenic GHG by concentration."
    ),
    TestQuestion(
        "Mangroves thrive in?",
        listOf("Freshwater lakes", "Brackish tidal water", "Deserts", "High mountains"),
        1,
        "Mangroves are found in brackish tidal waters along tropical coasts."
    ),
    TestQuestion(
        "Project Tiger launched in?",
        listOf("1963", "1973", "1983", "1993"),
        1,
        "Project Tiger was launched in 1973 to protect tiger populations and habitats."
    )
)

private fun scienceQuestions() = listOf(
    TestQuestion(
        "Light year measures?",
        listOf("Time", "Speed", "Distance", "Brightness"),
        2,
        "A light year is the distance light travels in one year."
    ),
    TestQuestion(
        "DNA stands for?",
        listOf("Deoxyribonucleic Acid", "Deoxyribose Nucleic Acid", "Dual Nucleic Acid", "Deoxynucleic Acid"),
        0,
        "DNA expands to Deoxyribonucleic Acid."
    ),
    TestQuestion(
        "Insulin is produced by?",
        listOf("Liver", "Pancreas", "Kidney", "Thyroid"),
        1,
        "Beta cells of the pancreas secrete insulin."
    ),
    TestQuestion(
        "The ozone layer is found in?",
        listOf("Troposphere", "Stratosphere", "Mesosphere", "Thermosphere"),
        1,
        "The ozone layer is concentrated in the lower stratosphere."
    ),
    TestQuestion(
        "Which vitamin is synthesized in skin by sunlight?",
        listOf("Vitamin A", "Vitamin B12", "Vitamin C", "Vitamin D"),
        3,
        "Sunlight helps synthesize Vitamin D in the skin."
    ),
    TestQuestion(
        "SI unit of electric current?",
        listOf("Volt", "Ampere", "Ohm", "Coulomb"),
        1,
        "The SI unit of electric current is Ampere."
    ),
    TestQuestion(
        "Newton’s third law states?",
        listOf("F=ma", "Every action has equal and opposite reaction", "Inertia law", "Universal gravitation"),
        1,
        "It states every action has an equal and opposite reaction."
    ),
    TestQuestion(
        "Speed of light in vacuum is approximately?",
        listOf("1.5x10^8 m/s", "2.5x10^8 m/s", "3x10^8 m/s", "3.5x10^8 m/s"),
        2,
        "Speed of light is about 3x10^8 m/s in vacuum."
    ),
    TestQuestion(
        "Which gas is used in balloons?",
        listOf("Oxygen", "Helium", "Nitrogen", "Hydrogen"),
        1,
        "Helium is inert and lighter than air, commonly used in balloons."
    ),
    TestQuestion(
        "Device to measure earthquakes?",
        listOf("Barometer", "Seismograph", "Hygrometer", "Altimeter"),
        1,
        "Earthquakes are recorded using a seismograph."
    )
)

private fun generalQuestions() = listOf(
    TestQuestion(
        "UN headquarters is located in?",
        listOf("Geneva", "New York", "Vienna", "Paris"),
        1,
        "The UN HQ is in New York City, USA."
    ),
    TestQuestion(
        "World Environment Day is on?",
        listOf("June 5", "April 22", "March 21", "July 11"),
        0,
        "World Environment Day is celebrated on June 5."
    ),
    TestQuestion(
        "Smallest continent by area?",
        listOf("Europe", "Australia", "Antarctica", "South America"),
        1,
        "Australia is the smallest continent by land area."
    ),
    TestQuestion(
        "Father of Indian Constitution?",
        listOf("Nehru", "Ambedkar", "Patel", "Gandhi"),
        1,
        "Dr. B.R. Ambedkar is widely regarded as the chief architect of the Constitution."
    ),
    TestQuestion(
        "Currency of Japan?",
        listOf("Won", "Yen", "Yuan", "Dollar"),
        1,
        "Japan uses the Yen."
    ),
    TestQuestion(
        "Water boils at what °C at sea level?",
        listOf("90", "95", "100", "110"),
        2,
        "At 1 atm pressure, water boils at 100°C."
    ),
    TestQuestion(
        "Planet known as Red Planet?",
        listOf("Mars", "Venus", "Jupiter", "Saturn"),
        0,
        "Mars appears red due to iron oxide on its surface."
    ),
    TestQuestion(
        "Largest ocean on Earth?",
        listOf("Atlantic", "Indian", "Pacific", "Arctic"),
        2,
        "The Pacific Ocean is the largest."
    ),
    TestQuestion(
        "The longest river in the world?",
        listOf("Nile", "Amazon", "Yangtze", "Mississippi"),
        0,
        "Nile is traditionally cited as the longest river."
    ),
    TestQuestion(
        "Vitamin C is also called?",
        listOf("Retinol", "Ascorbic Acid", "Calciferol", "Tocopherol"),
        1,
        "Vitamin C is Ascorbic Acid."
    )
)

// ----------------- Result Screen -----------------

@Composable
fun TestResultScreen(navController: NavController) {
    val result = navController.previousBackStackEntry?.savedStateHandle?.get<TestResultData>("test_result")

    if (result == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("No result data found.", color = MaterialTheme.colorScheme.onSurface)
        }
        return
    }

    val score = result.questions.indices.count { idx ->
        result.selected.getOrNull(idx) == result.questions[idx].correctIndex
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(MaterialTheme.colorScheme.background, MaterialTheme.colorScheme.surface)
                )
            )
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "${result.subject} Test Result",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        )
        Text(
            text = "Score: $score / ${result.questions.size}",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
        )
        Text(
            text = "Time taken: ${result.timeTakenSeconds / 60}m ${(result.timeTakenSeconds % 60)}s",
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
        )

        result.questions.forEachIndexed { index, q ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Q${index + 1}. ${q.question}",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    val userAnswer = result.selected.getOrNull(index)
                    val isCorrect = userAnswer == q.correctIndex
                    Text(
                        text = "Your answer: ${userAnswer?.let { q.options[it] } ?: "Not answered"}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = if (isCorrect) Color(0xFF15803D) else Color(0xFFB91C1C),
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Text(
                        text = "Correct answer: ${q.options[q.correctIndex]}",
                        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface)
                    )
                    Text(
                        text = "Explanation: ${q.explanation}",
                        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)),
                        textAlign = TextAlign.Start
                    )
                }
            }
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { navController.popBackStack("tests", inclusive = false) },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Back to Tests", color = MaterialTheme.colorScheme.onPrimary)
        }
    }
}
@Composable
fun TestSubjectScreen(subjectName: String, navController: NavController) {
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
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = subjectName,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        )
        Text(
            text = "Practice topic-wise and full-length tests for $subjectName.",
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Quick Subject Test",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Assignment,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }

                Text(
                    text = "10 MCQs • 10 minutes • Auto-evaluated with explanations",
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Timer,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Timer starts when you begin",
                        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                    )
                }

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        val encoded = Uri.encode(subjectName)
                        navController.navigate("test_session/$encoded")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Start Test", color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }

        Divider()

        Text(
            text = "More practice sections coming soon.",
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
        )
    }
}
