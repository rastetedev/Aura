package com.raulastete.aura.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.raulastete.aura.screens.create_record.CreateRecordScreen
import com.raulastete.aura.screens.record_list.RecordListScreen
import com.raulastete.aura.screens.settings.SettingsScreen
import com.raulastete.aura.screens.statistics.StatisticsScreen

const val ACTION_CREATE_RECORD = "com.raulastete.aura.CREATE_RECORD"

@Composable
fun NavigationRoot(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = NavigationRoute.RecordList(startRecording = false)
    ) {
        composable<NavigationRoute.RecordList>(
            deepLinks = listOf(
                navDeepLink<NavigationRoute.RecordList>(
                    basePath = "https://aura.com/records"
                ){
                    action = ACTION_CREATE_RECORD
                }
            )
        ) {
            RecordListScreen(
                onNavigateToCreateRecord = { details ->
                    navController.navigate(details.toCreateRecordRoute())
                },
                onNavigateToStatistics = {
                    navController.navigate(NavigationRoute.Statistics)
                },
                onNavigateToSettings = {
                    navController.navigate(NavigationRoute.Settings)
                }
            )
        }
        composable<NavigationRoute.CreateRecord> {
            CreateRecordScreen(
                onConfirmLeave = navController::navigateUp
            )
        }
        composable<NavigationRoute.Settings> {
            SettingsScreen(
                onGoBack = navController::navigateUp
            )
        }

        composable<NavigationRoute.Statistics> {
            StatisticsScreen(
                onGoBack = navController::navigateUp
            )
        }
    }
}