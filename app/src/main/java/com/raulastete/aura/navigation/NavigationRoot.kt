package com.raulastete.aura.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
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
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Create Echo Screen")
            }
        }
    }
}