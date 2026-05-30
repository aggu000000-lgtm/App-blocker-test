package com.example.appblocker

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.appblocker.ui.blocker.BlockerScreen
import com.example.appblocker.ui.foundation.DynamismLevel
import com.example.appblocker.ui.foundation.LocalDynamism
import com.example.appblocker.ui.home.HomeScreen
import com.example.appblocker.ui.settings.SettingsScreen
import kotlinx.serialization.Serializable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import com.example.appblocker.ui.foundation.meshGradientBackground
import androidx.compose.material3.MaterialTheme

@Serializable data object RouteHome
@Serializable data object RouteBlocker
@Serializable data object RouteSettings

data class TopLevelRoute(val name: String, val route: Any, val icon: ImageVector)

val topLevelRoutes = listOf(
    TopLevelRoute("Home", RouteHome, Icons.Default.Home),
    TopLevelRoute("Blocker", RouteBlocker, Icons.Default.Shield),
    TopLevelRoute("Settings", RouteSettings, Icons.Default.Settings),
)

@Composable
fun AppBlockerApp() {
    val navController = rememberNavController()
    val motion = com.example.appblocker.ui.theme.LocalMotion.current

    var isFocusSessionActive by remember { mutableStateOf(false) }
    val currentDynamism = if (isFocusSessionActive) DynamismLevel.Active else DynamismLevel.Ambient

    CompositionLocalProvider(LocalDynamism provides currentDynamism) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .meshGradientBackground(
                    base = MaterialTheme.colorScheme.background
                )
        ) {
            Scaffold(
                containerColor = androidx.compose.ui.graphics.Color.Transparent,
                bottomBar = {
                NavigationBar {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination

                    topLevelRoutes.forEach { topLevelRoute ->
                        NavigationBarItem(
                            icon = { Icon(topLevelRoute.icon, contentDescription = topLevelRoute.name) },
                            label = { Text(topLevelRoute.name) },
                            selected = currentDestination?.hierarchy?.any {
                                it.hasRoute(topLevelRoute.route::class)
                            } == true,
                            onClick = {
                                navController.navigate(topLevelRoute.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = RouteHome,
                modifier = Modifier.padding(innerPadding),
                enterTransition = {
                    androidx.compose.animation.fadeIn(animationSpec = motion.navigationSpring) +
                    androidx.compose.animation.slideInVertically(
                        initialOffsetY = { 50 },
                        animationSpec = motion.navigationSpringIntOffset
                    )
                },
                exitTransition = {
                    androidx.compose.animation.fadeOut(animationSpec = motion.navigationSpring)
                },
                popEnterTransition = {
                    androidx.compose.animation.fadeIn(animationSpec = motion.navigationSpring)
                },
                popExitTransition = {
                    androidx.compose.animation.fadeOut(animationSpec = motion.navigationSpring)
                }
            ) {
                composable<RouteHome> { 
                    HomeScreen(
                        onStartFocusSession = {
                            isFocusSessionActive = !isFocusSessionActive
                        }
                    )
                }
                composable<RouteBlocker> { BlockerScreen() }
                composable<RouteSettings> { SettingsScreen() }
            }
        }
        }
    }
}
