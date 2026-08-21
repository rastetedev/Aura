package com.raulastete.aura.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.raulastete.aura.screens.create_record.CreateRecordScreen
import com.raulastete.aura.screens.record_list.RecordListScreen


@Composable
fun NavigationRoot(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = NavigationRoute.RecordList
    ) {
        composable<NavigationRoute.RecordList> {
            RecordListScreen(
                onNavigateToCreateRecord = { details ->
                    navController.navigate(details.toCreateRecordRoute())
                }
            )
        }
        composable<NavigationRoute.CreateRecord> {
            CreateRecordScreen(
                onConfirmLeave = navController::navigateUp
            )
        }
    }
}