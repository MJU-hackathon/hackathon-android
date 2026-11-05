package com.cookandroid.mjhackathon.presentation.main.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class MainAppState(
    val navController: NavHostController,
    private val coroutineScope: CoroutineScope,
){
    private val currentDestination: StateFlow<NavDestination?> =
        navController.currentBackStackEntryFlow
            .map { it.destination }
            .stateIn(
                scope = coroutineScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = null
            )
}

@Composable
fun rememberMainAppState(
    navController: NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
): MainAppState {
    return remember(navController, coroutineScope) {
        MainAppState(
            navController = navController,
            coroutineScope = coroutineScope,
        )
    }
}
