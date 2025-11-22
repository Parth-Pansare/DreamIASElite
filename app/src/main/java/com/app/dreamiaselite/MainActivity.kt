package com.app.dreamiaselite

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.app.dreamiaselite.ui.screens.currentaffairs.CurrentAffairsScreen
import com.app.dreamiaselite.ui.screens.dashboard.DashboardScreen
import com.app.dreamiaselite.ui.screen.screens.notes.NotesScreen
import com.app.dreamiaselite.ui.screen.screens.pyq.PyqScreen
import com.app.dreamiaselite.ui.screen.screens.tests.TestsScreen
import com.app.dreamiaselite.ui.theme.DreamIasEliteTheme
import com.app.dreamiaselite.ui.theme.LightSurface
import com.app.dreamiaselite.ui.theme.TextPrimary
import kotlinx.coroutines.launch

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

// bottom navigation items
sealed class BottomNavItem(
    val route: String,
    val label: String
) {
    object Home : BottomNavItem("dashboard", "Home")
    object CurrentAffairs : BottomNavItem("current_affairs", "CA")
    object Tests : BottomNavItem("tests", "Tests")
    object Pyq : BottomNavItem("pyq", "PYQ")
    object Notes : BottomNavItem("notes", "Notes")
}

data class DrawerItem(
    val label: String,
    val route: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DreamIasApp() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DreamDrawer(
                currentRoute = currentRoute,
                onItemClick = { route ->
                    scope.launch { drawerState.close() }
                    if (route != null && route != currentRoute) {
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
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
                DreamBottomBar(navController = navController)
            }
        ) { innerPadding ->
            DreamNavHost(
                navController = navController,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

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
        BottomNavItem.Notes.route -> "My Notes"
        else -> "Dream IAS Elite"
    }

    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu"
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = LightSurface,
            titleContentColor = TextPrimary,
            navigationIconContentColor = TextPrimary
        )
    )
}

@Composable
fun DreamDrawer(
    currentRoute: String?,
    onItemClick: (String?) -> Unit
) {
    val items = listOf(
        DrawerItem("Dashboard", BottomNavItem.Home.route),
        DrawerItem("Current Affairs", BottomNavItem.CurrentAffairs.route),
        DrawerItem("Test Series", BottomNavItem.Tests.route),
        DrawerItem("PYQ", BottomNavItem.Pyq.route),
        DrawerItem("Notes", BottomNavItem.Notes.route),
        DrawerItem("Study Planner (coming soon)"),
        DrawerItem("Settings (coming soon)")
    )

    ModalDrawerSheet(
        drawerContainerColor = LightSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Text(
                text = "Dream IAS Elite",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            Text(
                text = "UPSC Preparation Suite",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = TextPrimary.copy(alpha = 0.6f)
                ),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            items.forEach { item ->
                val selected = item.route != null && item.route == currentRoute
                NavigationDrawerItem(
                    label = { Text(item.label) },
                    selected = selected,
                    onClick = { onItemClick(item.route) },
                    modifier = Modifier.padding(
                        start = 8.dp,
                        end = 8.dp,
                        top = 4.dp,
                        bottom = 4.dp
                    ),
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                        selectedTextColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
    }
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
                        imageVector = Icons.Default.Home, // keep simple for now
                        contentDescription = item.label
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
