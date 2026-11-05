package com.cookandroid.mjhackathon.presentation.main.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.cookandroid.mjhackathon.core.navigation.Detail
import com.cookandroid.mjhackathon.core.navigation.Home
import com.cookandroid.mjhackathon.core.navigation.My
import com.cookandroid.mjhackathon.core.navigation.PostDaily
import com.cookandroid.mjhackathon.core.navigation.Profile
import com.cookandroid.mjhackathon.core.navigation.Splash
import com.cookandroid.mjhackathon.presentation.detail.DetailRoute
import com.cookandroid.mjhackathon.presentation.home.HomeRoute
import com.cookandroid.mjhackathon.presentation.home.HomeViewModel
import com.cookandroid.mjhackathon.presentation.my.MyRoute
import com.cookandroid.mjhackathon.presentation.postDaily.PostDailyRoute
import com.cookandroid.mjhackathon.presentation.profile.ProfileRoute
import com.cookandroid.mjhackathon.presentation.splash.SplashRoute
import kotlinx.coroutines.delay

@Composable
fun MainNavHost(
    appState: MainAppState,
    paddingValues: PaddingValues,
){
    val navController = appState.navController

    NavHost(
        navController = navController,
        startDestination = Splash,
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
        composable<Splash> {
            LaunchedEffect(Unit) {
                delay(2_600L)
                navController.navigate(Profile(isInitialSetup = true)) {
                    popUpTo<Splash> { inclusive = true }
                    launchSingleTop = true
                }
            }
            SplashRoute()
        }

        composable<Home>{ entry ->
            HomeRoute(
                paddingValues = paddingValues,
                navigateToDetail = { questId ->
                    entry.savedStateHandle[HomeViewModel.SELECTED_QUEST_KEY] = questId
                    navController.navigate(Detail)
                },
                navigateToDiary = { navController.navigate(My) { launchSingleTop = true } },
                navigateToProfile = { navController.navigate(Profile) { launchSingleTop = true } },
            )
        }

        composable<Profile> { backStackEntry ->
            val profile = backStackEntry.toRoute<Profile>()
            ProfileRoute(
                paddingValues = paddingValues,
                showTopBar = !profile.isInitialSetup,
                navigateToBack = { navController.popBackStack() },
            )
        }

        composable<PostDaily> {
            PostDailyRoute(paddingValues = paddingValues)
        }

        composable<My> {
            MyRoute(paddingValues = paddingValues)
        }

        composable<Detail>{
            DetailRoute(
                paddingValues = paddingValues,
                navigateToBack = { navController.popBackStack() },
            )
        }
    }
}
