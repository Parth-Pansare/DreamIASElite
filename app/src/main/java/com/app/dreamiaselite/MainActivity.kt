package com.app.dreamiaselite

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Article
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.app.dreamiaselite.ui.theme.LightSurface
import com.app.dreamiaselite.ui.theme.TextPrimary
import com.app.dreamiaselite.ui.screen.screens.currentaffairs.CurrentAffairsScreen
import com.app.dreamiaselite.ui.screens.dashboard.DashboardScreen
import com.app.dreamiaselite.ui.screen.screens.notes.NotesScreen
import com.app.dreamiaselite.ui.screen.screens.pyq.PyqScreen
import com.app.dreamiaselite.ui.screen.screens.tests.TestsScreen
import com.app.dreamiaselite.ui.theme.DreamIasEliteTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DreamIasEliteTheme {
                DreamIasApp()
            }
        }
    }
}

@Composable
fun DreamIasApp() {
    val navController = rememberNavController()

    Scaffold(
        topBar = {
            DreamTopBar(navController = navController)
        },
        bottomBar = {
            DreamBottomBar(navController = navController)
        }
    ) { innerPadding ->
        DreamNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

// bottom navigation items
sealed class BottomNavItem(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    object Home : BottomNavItem("dashboard", "Home", Icons.Default.Home)
    object CurrentAffairs : BottomNavItem("current_affairs", "CA", Icons.Default.Article)
    object Tests : BottomNavItem("tests", "Tests", Icons.Default.Quiz)
    object Pyq : BottomNavItem("pyq", "PYQ", Icons.Default.Description)
    object Notes : BottomNavItem("notes", "Notes", Icons.Default.LibraryBooks)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DreamTopBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val title = when (currentRoute) {
        BottomNavItem.Home.route -> "Dashboard"
        BottomNavItem.CurrentAffairs.route -> "Current Affairs"
        BottomNavItem.Tests.route -> "Test Series"
        BottomNavItem.Pyq.route -> "PYQ"
        BottomNavItem.Notes.route -> "Notes"
        else -> "Dream IAS Elite"
    }

    CenterAlignedTopAppBar(
        title = { Text(title, style = MaterialTheme.typography.titleMedium) },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = LightSurface,
            titleContentColor = TextPrimary
        )
    )
}


@Composable
fun DreamBottomBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.CurrentAffairs,
        BottomNavItem.Tests,
        BottomNavItem.Pyq,
        BottomNavItem.Notes
    )

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = if (selected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                },
                label = { Text(item.label) }
            )
        }
    }
}

@Composable
fun DreamNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Home.route,
        modifier = modifier
    ) {
        composable(BottomNavItem.Home.route) { DashboardScreen(navController) }
        composable(BottomNavItem.CurrentAffairs.route) { CurrentAffairsScreen() }
        composable(BottomNavItem.Tests.route) { TestsScreen() }
        composable(BottomNavItem.Pyq.route) { PyqScreen() }
        composable(BottomNavItem.Notes.route) { NotesScreen() }
    }
}
