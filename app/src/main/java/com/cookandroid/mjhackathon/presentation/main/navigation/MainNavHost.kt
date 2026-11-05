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
                    navController.navigate(Detail(questId = questId))
                },
                navigateToDiary = { navController.navigate(My) { launchSingleTop = true } },
                navigateToProfile = { navController.navigate(Profile()) { launchSingleTop = true } },
            )
        }

        composable<Profile> { backStackEntry ->
            val profile = backStackEntry.toRoute<Profile>()
            val navigateToHome: () -> Unit = {
                if (!navController.popBackStack<Home>(inclusive = false)) {
                    navController.navigate(Home) {
                        popUpTo<Profile> { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
            ProfileRoute(
                paddingValues = paddingValues,
                showTopBar = !profile.isInitialSetup,
                navigateToBack = navigateToHome,
                onSaveProfile = { navigateToHome() },
            )
        }

        composable<PostDaily> {
            PostDailyRoute(
                paddingValues = paddingValues,
                navigateToBack = { navController.popBackStack() },
                onDiarySaved = { questId, _ ->
                    // 상세가 아닌 홈의 결과 핸들로 처리된 ID를 전달한다.
                    runCatching { navController.getBackStackEntry<Home>() }.getOrNull()
                        ?.savedStateHandle?.set(HomeViewModel.QUEST_RESULT_KEY, questId)
                    navController.navigate(My) {
                        popUpTo<Detail> { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )
        }

        composable<My> {
            MyRoute(
                paddingValues = paddingValues,
                navigateToHome = {
                    // 기존 홈은 재사용하고 그 위의 My와 중간 화면들을 제거한다.
                    if (!navController.popBackStack<Home>(inclusive = false)) {
                        // 홈이 없는 진입에서도 이전 화면으로 돌아가지 않도록 이력을 정리한다.
                        navController.navigate(Home) {
                            popUpTo(navController.graph.id) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                },
                navigateToProfile = { navController.navigate(Profile()) { launchSingleTop = true } },
            )
        }

        composable<Detail>{
            DetailRoute(
                paddingValues = paddingValues,
                navigateToBack = { navController.popBackStack() },
                onQuestPassed = { questId ->
                    navController.previousBackStackEntry?.savedStateHandle
                        ?.set(HomeViewModel.QUEST_RESULT_KEY, questId)
                    navController.popBackStack()
                },
                navigateToPostDaily = { questId, photoUri ->
                    navController.navigate(PostDaily(questId = questId, photoUri = photoUri))
                },
            )
        }
    }
}
