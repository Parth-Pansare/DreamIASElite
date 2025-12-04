package com.app.dreamiaselite

import android.net.Uri
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.viewmodel.initializer
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.app.dreamiaselite.ui.screen.screens.auth.AuthScreen
import com.app.dreamiaselite.ui.screen.screens.auth.AuthViewModel
import com.app.dreamiaselite.ui.screen.screens.auth.AuthUiState
import com.app.dreamiaselite.ui.screens.currentaffairs.CurrentAffairsScreen
import com.app.dreamiaselite.ui.screens.dashboard.DashboardScreen
import com.app.dreamiaselite.ui.screens.dashboard.SubjectDashboardScreen
import com.app.dreamiaselite.ui.screen.screens.notes.NotesScreen
import com.app.dreamiaselite.ui.screen.screens.notes.NotesReferenceBooksScreen
import com.app.dreamiaselite.ui.screen.screens.notes.NotesReferenceUnitsScreen
import com.app.dreamiaselite.ui.screen.screens.profile.ProfileScreen
import com.app.dreamiaselite.ui.screen.screens.pyq.PyqScreen
import com.app.dreamiaselite.ui.screen.screens.pyq.PyqPaperDetailScreen
import com.app.dreamiaselite.ui.screen.screens.tests.TestsScreen
import com.app.dreamiaselite.ui.screen.screens.tests.TestSubjectScreen
import com.app.dreamiaselite.ui.screen.screens.tests.TestSessionScreen
import com.app.dreamiaselite.ui.screen.screens.tests.TestResultScreen
import com.app.dreamiaselite.ui.screen.screens.tests.SubjectBooksScreen
import com.app.dreamiaselite.ui.screen.screens.tests.BookUnitsScreen
import com.app.dreamiaselite.ui.screen.screens.studyplanner.StudyPlannerScreen
import com.app.dreamiaselite.ui.theme.DreamIasEliteTheme
import com.app.dreamiaselite.ui.theme.LocalThemeController
import com.app.dreamiaselite.ui.theme.ThemeController
import kotlinx.coroutines.launch
import com.app.dreamiaselite.ui.screen.screens.theme.ThemeAppearanceScreen
import com.app.dreamiaselite.ui.screen.screens.settings.SettingsScreen
import com.app.dreamiaselite.ui.screen.screens.help.HelpFeedbackScreen
import com.app.dreamiaselite.ui.screen.screens.about.AboutPrivacyScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.runtime.produceState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var isDarkMode by rememberSaveable { mutableStateOf(false) }

            CompositionLocalProvider(
                LocalThemeController provides ThemeController(
                    isDarkMode = isDarkMode,
                    setDarkMode = { isDarkMode = it }
                )
            ) {
                DreamIasEliteTheme(isDarkMode = isDarkMode) {
                    DreamIasApp()   // ✅ FIX: Use real navigation app
                }
            }
        }
    }
}

// ----------------- Navigation models -----------------

sealed class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    object Home : BottomNavItem("dashboard", "Home", Icons.Filled.Home)
    object CurrentAffairs : BottomNavItem("current_affairs", "CA", Icons.Outlined.Article)
    object Tests : BottomNavItem("tests", "Tests", Icons.Outlined.AssignmentTurnedIn)
    object Pyq : BottomNavItem("pyq", "PYQ", Icons.Outlined.HistoryEdu)
    object Notes : BottomNavItem("notes", "Notes", Icons.Outlined.StickyNote2)
}

data class DrawerItem(
    val label: String,
    val route: String? = null,
    val icon: ImageVector,
    val subtitle: String? = null,
    val badge: String? = null
)

// ----------------- Root App -----------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DreamIasApp() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val appContext = LocalContext.current.applicationContext
    var showLogoutDialog by remember { mutableStateOf(false) }
    val authViewModel: AuthViewModel = viewModel(
        factory = viewModelFactory {
            initializer { AuthViewModel(appContext as android.app.Application) }
        }
    )
    val authState by authViewModel.uiState.collectAsStateWithLifecycle()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    when {
        authState.isLoading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        !authState.isAuthenticated -> {
            AuthScreen(
                state = authState,
                onLogin = { email, password -> authViewModel.login(email, password) },
                onRegister = { email, username, targetYear, password ->
                    authViewModel.register(email, username, targetYear, password)
                },
                onClearError = { authViewModel.clearError() }
            )
        }
        else -> {
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    DreamDrawer(
                        currentRoute = currentRoute,
                        userName = authState.currentUserName,
                        userEmail = authState.currentUserEmail,
                        avatarUrl = authState.avatarUrl,
                        onItemClick = { route ->
                            scope.launch { drawerState.close() }

                            when {
                                route == "logout" -> {
                                    showLogoutDialog = true
                                }
                                route != null && route != currentRoute -> {
                                    navController.navigate(route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        }
                    )
                }
            ) {
                Scaffold(
                    topBar = {
                        DreamTopBar(
                            currentRoute = currentRoute,
                            onMenuClick = { scope.launch { drawerState.open() } }
                        )
                    },
                    bottomBar = {
                        DreamBottomBar(navController)
                    }
                ) { padding ->
                    DreamNavHost(
                        navController = navController,
                        modifier = Modifier.padding(padding),
                        authState = authState,
                        onUpdateProfile = { name, targetYear, avatarUri ->
                            authViewModel.updateProfile(name, targetYear, avatarUri)
                        },
                        onClearProfileMessage = { authViewModel.clearProfileMessage() }
                    )
                }
            }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Log out?") },
            text = { Text("Are you sure you want to log out?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        authViewModel.logout()
                        navController.navigate(BottomNavItem.Home.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                inclusive = true
                            }
                            launchSingleTop = true
                            restoreState = false
                        }
                    }
                ) { Text("Confirm") }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// ----------------- Top App Bar -----------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DreamTopBar(
    currentRoute: String?,
    onMenuClick: () -> Unit
) {
    val title = when (currentRoute) {
        BottomNavItem.Home.route -> "Dashboard"
        BottomNavItem.CurrentAffairs.route -> "Current Affairs"
        BottomNavItem.Tests.route -> "Test Series"
        BottomNavItem.Pyq.route -> "Previous Year Questions"
        BottomNavItem.Notes.route -> "Notes"
        "theme_appearance" -> "Theme & Appearance"
        "settings" -> "Settings"
        "help_feedback" -> "Help & Feedback"
        "about_privacy" -> "About & Privacy"
        "study_planner" -> "Study Planner"
        else -> "Dream IAS Elite"
    }

    CenterAlignedTopAppBar(
        title = { Text(text = title, style = MaterialTheme.typography.titleMedium) },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

// ----------------- Drawer -----------------

@Composable
fun DreamDrawer(
    currentRoute: String?,
    userName: String?,
    userEmail: String?,
    avatarUrl: String?,
    onItemClick: (String?) -> Unit
) {

    // Remember drawer items to prevent recreation on each recomposition
    val accountItems = remember {
        listOf(
            DrawerItem("My Profile", route = "profile", icon = Icons.Outlined.AccountCircle, subtitle = "View your details"),
            DrawerItem("Progress & Analytics", icon = Icons.Outlined.BarChart, subtitle = "Accuracy, time spent, trends", badge = "Soon")
        )
    }

    val studyToolsItems = remember {
        listOf(
            DrawerItem("Study Planner", route = "study_planner", icon = Icons.Outlined.EventNote, subtitle = "Daily / weekly targets"),
            DrawerItem("Downloads & Offline", icon = Icons.Outlined.Download, subtitle = "Saved tests, notes, videos", badge = "Soon")
        )
    }

    val appItems = remember {
        listOf(
            DrawerItem("Theme & Appearance", route = "theme_appearance", icon = Icons.Outlined.Palette),
            DrawerItem("Settings", route = "settings", icon = Icons.Outlined.Settings),
            DrawerItem("Help & Feedback", route = "help_feedback", icon = Icons.Outlined.HelpOutline),
            DrawerItem("About & Privacy Policy", route = "about_privacy", icon = Icons.Outlined.Info),
            DrawerItem("Logout", route = "logout", icon = Icons.Outlined.Logout)
        )
    }

    ModalDrawerSheet(
        drawerContainerColor = MaterialTheme.colorScheme.surface,
        drawerShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp),
        modifier = Modifier.fillMaxWidth(0.7f)
    ) {
        DrawerHeader(userName = userName, userEmail = userEmail, avatarUrl = avatarUrl)

        DrawerSection("Account", accountItems, currentRoute, onItemClick)
        DrawerSection("Study Tools", studyToolsItems, currentRoute, onItemClick)
        DrawerSection("App", appItems, currentRoute, onItemClick)
    }
}

@Composable
fun DrawerSection(
    title: String,
    items: List<DrawerItem>,
    currentRoute: String?,
    onItemClick: (String?) -> Unit
) {
    Text(
        text = title,
        style = MaterialTheme.typography.bodyLarge.copy(
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            fontWeight = FontWeight.SemiBold
        ),
        modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 4.dp)
    )

    items.forEachIndexed { index, item ->
        DrawerItemRow(
            item = item,
            isSelected = currentRoute == item.route,
            onItemClick = onItemClick
        )
        if (index < items.lastIndex) {
            Divider(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                thickness = 0.8.dp,
                modifier = Modifier
                    .padding(start = 20.dp, end = 12.dp)
                    .padding(vertical = 2.dp)
            )
        }
    }
}

@Composable
fun DrawerHeader(userName: String?, userEmail: String?, avatarUrl: String?) {
    val displayName = userName?.ifBlank { null } ?: "Dream IAS Elite"
    val displayEmail = userEmail?.ifBlank { null } ?: "UPSC CSE • Free plan"
    val avatarBitmap by rememberBitmapFromUri(avatarUrl?.let { Uri.parse(it) })

    Surface(
        tonalElevation = 2.dp,
        shadowElevation = 4.dp,
        shape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (avatarBitmap != null) {
                Image(
                    bitmap = avatarBitmap!!,
                    contentDescription = "Profile photo",
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text("DE", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.width(12.dp))

            Column {
                Text(displayName, fontWeight = FontWeight.Bold)
                Text(displayEmail, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            }
        }
    }
}

// ----------------- Drawer Item -----------------

@Composable
fun DrawerItemRow(item: DrawerItem, isSelected: Boolean, onItemClick: (String?) -> Unit) {

    NavigationDrawerItem(
        label = {
            Column {
                Text(item.label, fontWeight = FontWeight.Medium)
                item.subtitle?.let {
                    Text(it, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                }
            }
        },
        icon = { Icon(item.icon, contentDescription = item.label) },
        selected = isSelected,
        onClick = { onItemClick(item.route) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
            selectedTextColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
private fun rememberBitmapFromUri(uri: Uri?): State<ImageBitmap?> {
    val context = LocalContext.current
    return produceState<ImageBitmap?>(initialValue = null, key1 = uri) {
        if (uri == null) {
            value = null
            return@produceState
        }
        value = withContext(Dispatchers.IO) {
            runCatching {
                when (uri.scheme) {
                    "content" -> {
                        context.contentResolver.openInputStream(uri)?.use { stream ->
                            BitmapFactory.decodeStream(stream)?.asImageBitmap()
                        }
                    }
                    "file" -> {
                        java.io.File(uri.path.orEmpty()).takeIf { it.exists() }?.inputStream()?.use { stream ->
                            BitmapFactory.decodeStream(stream)?.asImageBitmap()
                        }
                    }
                    else -> null
                }
            }.getOrNull()
        }
    }
}

// ----------------- Bottom Bar -----------------

@Composable
fun DreamBottomBar(navController: NavHostController) {

    // Remember bottom nav items to prevent recreation on each recomposition
    val items = remember {
        listOf(
            BottomNavItem.Home,
            BottomNavItem.Tests,
            BottomNavItem.Pyq,
            BottomNavItem.Notes
        )
    }

    fun rootForRoute(route: String?): String? {
        if (route == null) return null
        return when {
            route == BottomNavItem.Home.route || route.startsWith("subject_dashboard") -> BottomNavItem.Home.route
            route.startsWith("test_") || route == BottomNavItem.Tests.route -> BottomNavItem.Tests.route
            route.startsWith("pyq") || route == BottomNavItem.Pyq.route -> BottomNavItem.Pyq.route
            route.startsWith("notes") || route == BottomNavItem.Notes.route -> BottomNavItem.Notes.route
            else -> route
        }
    }

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        val currentRoot = rootForRoute(currentRoute)

        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoot == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(item.route) {
                            inclusive = false
                            saveState = false
                        }
                        // Fallback to graph start if the route is not in the back stack
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = false
                            saveState = false
                        }
                        launchSingleTop = true
                        restoreState = false
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}

// ----------------- Navigation Host -----------------

@Composable
fun DreamNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    authState: AuthUiState,
    onUpdateProfile: (String, String, Uri?) -> Unit,
    onClearProfileMessage: () -> Unit
) {

    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Home.route,
        modifier = modifier
    ) {

        composable(BottomNavItem.Home.route) { DashboardScreen(navController, authState.currentUserName) }
        composable(BottomNavItem.CurrentAffairs.route) { CurrentAffairsScreen() }
        composable(BottomNavItem.Tests.route) { TestsScreen(navController) }
        composable(BottomNavItem.Pyq.route) { PyqScreen(navController) }
        composable(BottomNavItem.Notes.route) { NotesScreen(navController) }
        composable("profile") {
            ProfileScreen(
                navController = navController,
                userName = authState.currentUserName,
                userEmail = authState.currentUserEmail,
                targetYear = authState.targetYear,
                createdAt = authState.createdAt,
                avatarUrl = authState.avatarUrl,
                isSaving = authState.isProfileSaving,
                profileMessage = authState.profileMessage,
                profileMessageIsError = authState.profileMessageIsError,
                onUpdateProfile = onUpdateProfile,
                onClearMessage = onClearProfileMessage
            )
        }
        composable("subject_dashboard/{subject}") { entry ->
            val subject = entry.arguments?.getString("subject") ?: "Subject"
            SubjectDashboardScreen(Uri.decode(subject), navController)
        }

        composable("theme_appearance") { ThemeAppearanceScreen() }
        composable("settings") { SettingsScreen() }
        composable("help_feedback") {
            HelpFeedbackScreen(
                currentUserEmail = authState.currentUserEmail
            )
        }
        composable("about_privacy") { AboutPrivacyScreen() }
        composable("study_planner") { StudyPlannerScreen() }
        composable("test_books/{subject}") { entry ->
            val subject = entry.arguments?.getString("subject") ?: "Tests"
            SubjectBooksScreen(Uri.decode(subject), navController)
        }
        composable("test_units/{subject}/{book}") { entry ->
            val subject = entry.arguments?.getString("subject") ?: return@composable
            val book = entry.arguments?.getString("book") ?: return@composable
            BookUnitsScreen(
                subjectName = Uri.decode(subject),
                bookTitle = Uri.decode(book),
                navController = navController
            )
        }
        composable("test_subject/{name}") { entry ->
            val name = entry.arguments?.getString("name") ?: "Tests"
            TestSubjectScreen(Uri.decode(name), navController)
        }
        composable("test_session/{subject}") { entry ->
            val subject = entry.arguments?.getString("subject") ?: "Test"
            TestSessionScreen(Uri.decode(subject), navController)
        }
        composable("test_result") {
            TestResultScreen(navController)
        }
        composable("pyq_paper/{paperId}") { entry ->
            val paperId = entry.arguments?.getString("paperId") ?: return@composable
            PyqPaperDetailScreen(navController, paperId)
        }
        composable("notes_reference/{subject}") { entry ->
            val subject = entry.arguments?.getString("subject") ?: return@composable
            NotesReferenceBooksScreen(Uri.decode(subject), navController)
        }
        composable("notes_reference_units/{subject}/{book}") { entry ->
            val subject = entry.arguments?.getString("subject") ?: return@composable
            val book = entry.arguments?.getString("book") ?: return@composable
            NotesReferenceUnitsScreen(Uri.decode(subject), Uri.decode(book), navController)
        }
    }
}
