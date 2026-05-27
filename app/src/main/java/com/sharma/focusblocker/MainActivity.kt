package com.sharma.focusblocker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.feature.FeatureScreen
import com.sharma.focusblocker.ui.ScheduleManagementScreen
import com.sharma.focusblocker.ui.theme.FocusBlockerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FocusBlockerTheme {
                val navController = rememberNavController()
                com.example.designsystem.components.CinematicNavHostProvider {
                    Scaffold(
                        modifier = Modifier.fillMaxSize()
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = "home",
                            modifier = Modifier.padding(innerPadding),
                            enterTransition = { com.example.designsystem.components.cinematicEnterTransition() },
                            exitTransition = { com.example.designsystem.components.cinematicExitTransition() },
                            popEnterTransition = { com.example.designsystem.components.cinematicPopEnterTransition() },
                            popExitTransition = { com.example.designsystem.components.cinematicPopExitTransition() }
                        ) {
                            composable("home") {
                                androidx.compose.runtime.CompositionLocalProvider(
                                    com.example.designsystem.components.LocalAnimatedVisibilityScope provides this
                                ) {
                                    HomeScreen(
                                        onNavigateToManageSchedules = { navController.navigate("schedules") }
                                    )
                                }
                            }
                            composable("schedules") {
                                androidx.compose.runtime.CompositionLocalProvider(
                                    com.example.designsystem.components.LocalAnimatedVisibilityScope provides this
                                ) {
                                    ScheduleManagementScreen(
                                        onNavigateBack = { navController.popBackStack() },
                                        onEditSchedule = { id -> 
                                            if (id != null) {
                                                navController.navigate("schedule_edit/$id")
                                            } else {
                                                navController.navigate("schedule_create")
                                            }
                                        }
                                    )
                                }
                            }
                            composable("schedule_edit/{id}") { backStackEntry ->
                                val id = backStackEntry.arguments?.getString("id")
                                androidx.compose.runtime.CompositionLocalProvider(
                                    com.example.designsystem.components.LocalAnimatedVisibilityScope provides this
                                ) {
                                    com.sharma.focusblocker.ui.ScheduleEditScreen(
                                        scheduleId = id,
                                        onNavigateBack = { navController.popBackStack() }
                                    )
                                }
                            }
                            composable("schedule_create") {
                                androidx.compose.runtime.CompositionLocalProvider(
                                    com.example.designsystem.components.LocalAnimatedVisibilityScope provides this
                                ) {
                                    com.sharma.focusblocker.ui.ScheduleEditScreen(
                                        scheduleId = null,
                                        onNavigateBack = { navController.popBackStack() }
                                    )
                                }
                            }
                            composable("feature") {
                                androidx.compose.runtime.CompositionLocalProvider(
                                    com.example.designsystem.components.LocalAnimatedVisibilityScope provides this
                                ) {
                                    FeatureScreen()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
