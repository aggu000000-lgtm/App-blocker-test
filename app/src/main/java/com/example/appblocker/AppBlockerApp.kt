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
import androidx.compose.runtime.getValue
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
import com.example.appblocker.ui.home.HomeScreen
import com.example.appblocker.ui.settings.SettingsScreen
import kotlinx.serialization.Serializable

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

    Scaffold(
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
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<RouteHome> { HomeScreen() }
            composable<RouteBlocker> { BlockerScreen() }
            composable<RouteSettings> { SettingsScreen() }
        }
    }
}
