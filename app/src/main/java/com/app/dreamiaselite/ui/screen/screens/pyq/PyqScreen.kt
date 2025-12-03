package com.app.dreamiaselite.ui.screen.screens.pyq

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.CloudDownload
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material.icons.outlined.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController

private data class PyqPaper(
    val id: String,
    val title: String,
    val questions: String,
    val statusLabel: String? = null,
    val statusColor: Color = Color(0xFF2ECF7C),
    val score: Int? = null,
    val icon: ImageVector,
    val iconTint: Color,
    val iconBackground: Color,
    val onViewPaper: () -> Unit = {}
)

private data class PyqQuestion(
    val id: String,
    val year: String,
    val paper: String,
    val subject: String,
    val question: String,
    val answer: String,
    val explanation: String
)

@Composable
fun PyqScreen(navController: NavController) {

    val context = LocalContext.current
    val tabs = listOf("Year-wise", "Subject-wise")
    val selectedTab = remember { mutableStateOf(tabs.first()) }
    val years = listOf("2024", "2023", "2022", "2021", "2020")
    val selectedYear = remember { mutableStateOf("2023") }
    val subjects = listOf("GS Paper 1", "GS Paper 2", "GS Paper 3", "CSAT")
    val selectedSubject = remember { mutableStateOf(subjects.first()) }

    val pyqQuestions = remember { providePyqQuestions() }
    val basePapers = remember { providePyqPapers() }
    val papers = basePapers.map { paper ->
        paper.copy(
            onViewPaper = {
                openPaperPdf(context, paper, pyqQuestions.filter { it.paper == paper.title })
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        item {
            Text(
                text = "Previous Year Questions",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
        }

        item {
            FilterTabs(
                tabs = tabs,
                selectedTab = selectedTab.value,
                onTabSelected = { selectedTab.value = it }
            )
        }

        item {
            if (selectedTab.value == "Year-wise") {
                YearSelector(years, selectedYear.value) { selectedYear.value = it }
            } else {
                SubjectSelector(subjects, selectedSubject.value) { selectedSubject.value = it }
            }
        }

        item { AttemptSummaryCard() }

        item {
            Text(
                text = if (selectedTab.value == "Year-wise") "${selectedYear.value} Papers" else "${selectedSubject.value} Papers",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        items(papers) { paper ->
            PyqPaperCard(paper)
        }

        val filteredQuestions = pyqQuestions.filter {
            if (selectedTab.value == "Year-wise") it.year == selectedYear.value else it.paper == selectedSubject.value
        }

        if (filteredQuestions.isNotEmpty()) {
            item {
                Text(
                    text = "PYQ Questions",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            items(filteredQuestions, key = { it.id }) { question ->
                PyqQuestionCard(question)
            }
        }

        item { StatsRow() }

        item { StudyTipCard() }
    }
}

// ----------------- Paper Detail -----------------

@Composable
fun PyqPaperDetailScreen(navController: NavController, paperId: String) {
    val papers = remember { providePyqPapers() }
    val questions = remember { providePyqQuestions() }
    val paper = papers.find { it.id == paperId }
    val filteredQuestions = questions.filter { it.paper.equals(paper?.title, ignoreCase = true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (paper == null) {
            Text(
                text = "Paper not found.",
                style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface)
            )
            return@Column
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = paper.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Text(
                    text = paper.questions,
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                )
            }
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = paper.iconBackground
            ) {
                Icon(
                    imageVector = paper.icon,
                    contentDescription = paper.title,
                    tint = paper.iconTint,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }

        Divider(color = Color(0xFFE0E4EC))

        filteredQuestions.forEachIndexed { index, q ->
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
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Text(
                        text = "Answer: ${q.answer}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF17612B)
                        )
                    )
                    Text(
                        text = "Explanation: ${q.explanation}",
                        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                    )
                }
            }
        }
    }
}


@Composable
private fun FilterTabs(tabs: List<String>, selectedTab: String, onTabSelected: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            tabs.forEach { tab ->
                val isSelected = tab == selectedTab

                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) Color(0xFF2E6BFF) else Color.Transparent
                ) {
                    Box(
                        modifier = Modifier
                            .clickableWithoutRipple { onTabSelected(tab) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                    Text(
                        text = tab,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    )
                }
                }
            }
        }
    }
}

@Composable
private fun YearSelector(years: List<String>, selectedYear: String, onSelect: (String) -> Unit) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        years.forEach { year ->
            val isSelected = year == selectedYear

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) Color(0xFFE8ECFF) else MaterialTheme.colorScheme.surface,
                border = if (!isSelected) BorderStroke(1.dp, Color(0xFFE3E6EC)) else null
            ) {
                Box(
                    modifier = Modifier
                        .clickableWithoutRipple { onSelect(year) }
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = year,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                            color = if (isSelected) Color(0xFF2E6BFF) else MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun SubjectSelector(subjects: List<String>, selectedSubject: String, onSelect: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        subjects.forEach { subject ->
            val isSelected = subject == selectedSubject
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) Color(0xFFE8ECFF) else MaterialTheme.colorScheme.surface,
                border = if (!isSelected) BorderStroke(1.dp, Color(0xFFE3E6EC)) else null
            ) {
                Box(
                    modifier = Modifier
                        .clickableWithoutRipple { onSelect(subject) }
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = subject,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                            color = if (isSelected) Color(0xFF2E6BFF) else MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }
        }
    }
}


@Composable
private fun AttemptSummaryCard() {

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.elevatedCardElevation(2.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF9C1BEA), Color(0xFF6C1BCE))
                    )
                )
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Column {
                        Text(
                            text = "Papers Attempted",
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        Text(
                            text = "5 / 15",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Surface(
                        modifier = Modifier.size(42.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White.copy(alpha = 0.18f)
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            Icon(
                                imageVector = Icons.Outlined.CloudDownload,
                                contentDescription = "download",
                                tint = Color.White
                            )
                        }
                    }
                }

                LinearProgressIndicator(
                    progress = 5f / 15f,
                    color = Color.White,
                    trackColor = Color.White.copy(alpha = 0.25f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(12.dp))
                )

                Text(
                    text = "Keep solving! PYQs are the key to success 🎯",
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}


@Composable
private fun PyqPaperCard(paper: PyqPaper) {

   Card(
       modifier = Modifier.fillMaxWidth(),
       shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {

        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Row(verticalAlignment = Alignment.CenterVertically) {

                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(paper.iconBackground),
                        contentAlignment = Alignment.Center
                    ) {

                        Icon(
                            imageVector = paper.icon,
                            contentDescription = paper.title,
                            tint = paper.iconTint,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                       Text(
                           text = paper.title,
                           style = MaterialTheme.typography.titleMedium.copy(
                               fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = paper.questions,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }

                paper.statusLabel?.let {
                    StatusBadge(label = it, color = paper.statusColor)
                }
            }

            paper.score?.let { score ->
                ScoreStrip(score)
            }

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = paper.onViewPaper,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF111827),
                    contentColor = Color.White
                )
            ) {
                Text("View Paper")
            }
        }
    }
}

@Composable
private fun PyqQuestionCard(question: PyqQuestion) {
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${question.paper} • ${question.year}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Text(
                        text = question.subject,
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                    )
                }
                StatusBadge(label = "PYQ", color = Color(0xFF2E6BFF))
            }

            Text(
                text = question.question,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )

            Divider(color = Color(0xFFE9EBF1))

            Text(
                text = "Answer: ${question.answer}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF17612B)
                )
            )
            Text(
                text = "Explanation: ${question.explanation}",
                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            )
        }
    }
}


@Composable
private fun StatusBadge(label: String, color: Color) {

    Surface(
        color = color.copy(alpha = 0.14f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = label,
            color = color,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}


@Composable
private fun ScoreStrip(score: Int) {

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF0F9F0)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text("Your Score", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            Text("$score%", color = Color(0xFF17B26A), fontWeight = FontWeight.Bold)
        }
    }
}



@Composable
private fun StatsRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatsCard(
            label = "Latest Year Papers",
            value = "5",
            icon = Icons.Outlined.Book,
            tint = Color(0xFF2E6BFF),
            bubble = Color(0xFFE8EDFF),
            modifier = Modifier.weight(1f)
        )
        StatsCard(
            label = "Completed Papers",
            value = "5",
            icon = Icons.Outlined.PlayCircle,
            tint = Color(0xFF1BA97F),
            bubble = Color(0xFFE8F6F1),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatsCard(
    label: String,
    value: String,
    icon: ImageVector,
    tint: Color,
    bubble: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(bubble),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = tint
                )
            }
           Text(
               text = value,
               style = MaterialTheme.typography.headlineSmall.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            )
        }
    }
}


@Composable
private fun StudyTipCard() {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFF6F1E)),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {

        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = "Study Tip",
                tint = Color.White
            )

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {

                Text(
                    text = "Study Tip",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )

                Text(
                    text = "Analyze the pattern of questions asked in previous years. Focus on frequently asked topics and practice at least 2–3 papers from each subject.",
                    color = Color.White
                )
            }
        }
    }
}

private fun providePyqPapers(): List<PyqPaper> = listOf(
    PyqPaper(
        id = "gs_paper_1",
        title = "GS Paper 1",
        questions = "100 Questions",
        statusLabel = "Downloaded",
        score = 68,
        icon = Icons.Outlined.CheckCircle,
        iconTint = Color(0xFF28B463),
        iconBackground = Color(0xFFE8F7EF)
    ),
    PyqPaper(
        id = "gs_paper_2",
        title = "GS Paper 2",
        questions = "100 Questions",
        statusLabel = "Downloaded",
        score = null,
        icon = Icons.Outlined.Quiz,
        iconTint = Color(0xFF2E86DE),
        iconBackground = Color(0xFFE8F1FF)
    ),
    PyqPaper(
        id = "gs_paper_3",
        title = "GS Paper 3",
        questions = "100 Questions",
        statusLabel = null,
        score = null,
        icon = Icons.Outlined.Assignment,
        iconTint = Color(0xFF8E44AD),
        iconBackground = Color(0xFFF3E9FF)
    ),
    PyqPaper(
        id = "csat",
        title = "CSAT",
        questions = "80 Questions",
        statusLabel = "Downloaded",
        score = 72,
        icon = Icons.Outlined.WorkspacePremium,
        iconTint = Color(0xFF16A085),
        iconBackground = Color(0xFFE6FAF3)
    )
)

private fun providePyqQuestions(): List<PyqQuestion> = listOf(
    PyqQuestion(
        id = "2024_gs1_1",
        year = "2024",
        paper = "GS Paper 1",
        subject = "History",
        question = "With reference to the Indian National Movement, what was the immediate cause of launching the Non-Cooperation Movement in 1920?",
        answer = "The Rowlatt Act and Jallianwala Bagh massacre",
        explanation = "Gandhi launched the Non-Cooperation Movement after the repressive Rowlatt Act and the Jallianwala Bagh massacre in 1919."
    ),
    PyqQuestion(
        id = "2023_gs1_1",
        year = "2023",
        paper = "GS Paper 1",
        subject = "Geography",
        question = "Why do tropical cyclones not originate in the South Atlantic and South Eastern Pacific Oceans?",
        answer = "Due to weak Coriolis force and cooler sea surface temperatures",
        explanation = "Cooler waters and weak Coriolis force near the equator in these basins inhibit cyclone formation."
    ),
    PyqQuestion(
        id = "2022_gs2_1",
        year = "2022",
        paper = "GS Paper 2",
        subject = "Polity",
        question = "Discuss the significance of the 102nd Constitutional Amendment for the backward class commissions in India.",
        answer = "It gave constitutional status to the National Commission for Backward Classes under Article 338B.",
        explanation = "The amendment introduced Articles 338B and 342A, making NCBC a constitutional body and detailing central and state lists of SEBCs."
    ),
    PyqQuestion(
        id = "2021_gs3_1",
        year = "2021",
        paper = "GS Paper 3",
        subject = "Economy",
        question = "What are Minimum Support Prices (MSP) and how do they impact farmers' income?",
        answer = "MSP is a pre-announced price at which government procures crops; it provides price assurance and reduces income volatility.",
        explanation = "MSP cushions farmers against market crashes, incentivises crop choices, and stabilises incomes when procurement occurs."
    ),
    PyqQuestion(
        id = "2020_csat_1",
        year = "2020",
        paper = "CSAT",
        subject = "Reasoning",
        question = "If A is thrice as good a worker as B, and together they finish a work in 18 days, how long would B alone take?",
        answer = "72 days",
        explanation = "A's rate = 3B. Combined rate 4B = 1/18 -> B = 1/72, so B alone takes 72 days."
    )
)


private fun Modifier.clickableWithoutRipple(onClick: () -> Unit): Modifier = composed {
    clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        onClick = onClick
    )
}

private fun openPaperPdf(
    context: Context,
    paper: PyqPaper,
    questions: List<PyqQuestion>
) {
    runCatching {
        val fileName = "pyq_${paper.id}.pdf"
        val file = java.io.File(context.cacheDir, fileName)

        val doc = PdfDocument()
        val paint = Paint().apply {
            textSize = 12f
            isAntiAlias = true
            color = android.graphics.Color.BLACK
        }

        val pageWidth = 595
        val pageHeight = 842
        var y = 40f

        fun newPage(): PdfDocument.Page {
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, doc.pages.size + 1).create()
            val page = doc.startPage(pageInfo)
            y = 40f
            return page
        }

        var page = newPage()

        fun writeLine(text: String) {
            if (y > pageHeight - 40) {
                doc.finishPage(page)
                page = newPage()
            }
            page.canvas.drawText(text, 40f, y, paint)
            y += 18f
        }

        writeLine(paper.title)
        writeLine(paper.questions)
        writeLine("")

        questions.forEachIndexed { index, q ->
            writeLine("Q${index + 1}: ${q.question}")
            writeLine("Answer: ${q.answer}")
            writeLine("Explanation: ${q.explanation}")
            writeLine("")
        }

        doc.finishPage(page)
        file.outputStream().use { out -> doc.writeTo(out) }
        doc.close()

        val uri = androidx.core.content.FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NO_HISTORY)
        }
        context.startActivity(intent)
    }.onFailure {
        Toast.makeText(context, "No PDF viewer found.", Toast.LENGTH_SHORT).show()
    }
}
