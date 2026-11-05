package com.cookandroid.mjhackathon.presentation.main.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.input.key.Key.Companion.Home
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.cookandroid.mjhackathon.core.navigation.Detail
import com.cookandroid.mjhackathon.core.navigation.Home
import com.cookandroid.mjhackathon.presentation.detail.DetailRoute
import com.cookandroid.mjhackathon.presentation.home.HomeRoute

@Composable
fun MainNavHost(
    appState: MainAppState,
    paddingValues: PaddingValues,
){
    val navController = appState.navController

    NavHost(
        navController = navController,
        startDestination = Home,
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(durationMillis = 300)
            )
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> -fullWidth },
                animationSpec = tween(durationMillis = 300)
            )
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> -fullWidth },
                animationSpec = tween(durationMillis = 300)
            )
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(durationMillis = 300)
            )
        },
    ){
        composable<Home>{
            HomeRoute(
                paddingValues = paddingValues,
                navigateToDetail = { navController.navigate(Detail) },
            )
        }

        composable<Detail>{
            DetailRoute(
                paddingValues = paddingValues,
                navigateToBack = { navController.popBackStack() },
                viewModel = TODO(),
            )
        }
    }
}
