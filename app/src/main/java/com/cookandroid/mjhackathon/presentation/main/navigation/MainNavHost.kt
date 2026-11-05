package com.cookandroid.mjhackathon.presentation.main.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.cookandroid.mjhackathon.core.navigation.Detail
import com.cookandroid.mjhackathon.core.navigation.Home
import com.cookandroid.mjhackathon.core.navigation.My
import com.cookandroid.mjhackathon.core.navigation.PostDaily
import com.cookandroid.mjhackathon.core.navigation.Profile
import com.cookandroid.mjhackathon.core.navigation.Splash
import com.cookandroid.mjhackathon.presentation.detail.DetailRoute
import com.cookandroid.mjhackathon.presentation.home.HomeRoute
import com.cookandroid.mjhackathon.presentation.my.MyRoute
import com.cookandroid.mjhackathon.presentation.postDaily.PostDailyRoute
import com.cookandroid.mjhackathon.presentation.profile.ProfileRoute
import com.cookandroid.mjhackathon.presentation.splash.SplashRoute

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
        composable<Splash> {
            SplashRoute()
        }

        composable<Home>{
            HomeRoute(
                paddingValues = paddingValues,
                navigateToDetail = { navController.navigate(Detail) },
            )
        }

        composable<Profile> {
            ProfileRoute(paddingValues = paddingValues)
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
