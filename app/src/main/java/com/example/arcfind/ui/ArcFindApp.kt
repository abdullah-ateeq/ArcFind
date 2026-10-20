package com.example.arcfind.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.arcfind.ui.about.AboutScreen
import com.example.arcfind.ui.history.HistoryScreen
import com.example.arcfind.ui.home.HomeScreen
import com.example.arcfind.ui.settings.SettingsScreen
import com.example.arcfind.ui.splash.SplashScreen

sealed class Screen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    data object Splash : Screen("splash", "Splash", Icons.Default.Calculate, Icons.Outlined.Calculate)
    data object Home : Screen("home", "Home", Icons.Default.Calculate, Icons.Outlined.Calculate)
    data object History : Screen("history", "History", Icons.Default.History, Icons.Outlined.History)
    data object Settings : Screen("settings", "Settings", Icons.Default.Settings, Icons.Outlined.Settings)
    data object About : Screen("about", "About", Icons.Default.Info, Icons.Outlined.Info)
}

val bottomNavItems = listOf(
    Screen.Home,
    Screen.History,
    Screen.Settings,
    Screen.About
)

@Composable
fun ArcFindApp(
    viewModel: ArcFindViewModel,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in bottomNavItems.map { it.route }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { screen ->
                        val isSelected = currentRoute == screen.route
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                if (currentRoute != screen.route) {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                                    contentDescription = screen.title
                                )
                            },
                            label = { Text(screen.title) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(
                    onSplashFinished = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Home.route) {
                HomeScreen(
                    uiState = uiState,
                    onSolveTypeSelected = viewModel::onSolveTypeSelected,
                    onRadiusChanged = viewModel::onRadiusInputChanged,
                    onAngleChanged = viewModel::onAngleInputChanged,
                    onArcLengthChanged = viewModel::onArcLengthInputChanged,
                    onRadiusUnitChanged = viewModel::onRadiusUnitChanged,
                    onArcLengthUnitChanged = viewModel::onArcLengthUnitChanged,
                    onResultUnitChanged = viewModel::onResultUnitChanged,
                    onSaveCalculation = viewModel::onSaveCalculation,
                    onResetAll = viewModel::onResetAll,
                    onClearNotification = viewModel::clearNotification,
                    getShareText = viewModel::getShareText,
                    getCopyValueOnly = viewModel::getCopyValueOnly
                )
            }

            composable(Screen.History.route) {
                HistoryScreen(
                    history = uiState.history,
                    confirmDelete = uiState.settings.confirmDelete,
                    hapticsEnabled = uiState.settings.hapticFeedback,
                    onRestoreHistory = viewModel::onRestoreHistory,
                    onDeleteEntry = viewModel::onDeleteHistory,
                    onClearHistory = viewModel::onClearHistory,
                    onNavigateToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    settings = uiState.settings,
                    onUpdateSettings = viewModel::onUpdateSettings,
                    onClearHistory = viewModel::onClearHistory,
                    onResetSettings = viewModel::onResetSettings
                )
            }

            composable(Screen.About.route) {
                AboutScreen()
            }
        }
    }
}
