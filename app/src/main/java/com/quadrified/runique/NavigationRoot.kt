package com.quadrified.runique

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navDeepLink
import com.quadrified.auth.presentation.intro.IntroScreenRoot
import com.quadrified.auth.presentation.login.LoginScreenRoot
import com.quadrified.auth.presentation.register.RegisterScreenRoot
import com.quadrified.run.presentation.active_run.ActiveRunScreenRoot
import com.quadrified.run.presentation.active_run.service.ActiveRunService
import com.quadrified.run.presentation.run_overview.RunOverviewScreenRoot

@Composable
fun NavigationRoot(
    navController: NavHostController,
    isLoggedIn: Boolean,
    onAnalyticsClick: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) "run" else "auth"
    ) {
        authGraph(navController)
        runGraph(navController, onAnalyticsClick)
    }
}

private fun NavGraphBuilder.authGraph(navController: NavHostController) {
    // navigation => Used to create a nested navigation graph that allows you to group related destinations (screens) under a specific route.
    navigation(
        startDestination = "intro",
        route = "auth" // name of the entire "auth" flow
    ) {
        // composable => Each screen in "auth" flow
        composable(route = "intro") {
            IntroScreenRoot(
                onSignUpClick = {
                    navController.navigate("register")
                },
                onSignInClick = {
                    navController.navigate("login")
                }
            )
        }

        composable(route = "register") {
            RegisterScreenRoot(
                onSignInClick = {
                    navController.navigate("login") {
                        // For not having multiple instances in back stack when navigating b/w Login and Register
                        // Otherwise It can cause user to click back multiple times to just get back to Intro
                        popUpTo("register") {
                            inclusive = true
                            saveState = true // saving any state
                        }
                        restoreState = true // restoring state
                    }
                },
                onSuccessfulRegistration = {
                    navController.navigate("login")
                }
            )
        }

        composable("login") {
            LoginScreenRoot(
                onLoginSuccess = {
                    navController.navigate("run") {
                        popUpTo("auth") {
                            inclusive = true
                        }
                    }
                },
                onSignUpClick = {
                    navController.navigate("register") {
                        popUpTo("login") {
                            inclusive = true
                            saveState = true
                        }
                        restoreState = true
                    }
                }
            )
        }
    }
}

private fun NavGraphBuilder.runGraph(
    navController: NavHostController,
    onAnalyticsClick: () -> Unit
) {
    navigation(
        startDestination = "run_overview",
        route = "run"
    ) {
        composable("run_overview") {
            RunOverviewScreenRoot(
                onAnalyticsClick = onAnalyticsClick,
                onStartRunClick = {
                    navController.navigate("active_run")
                },
                onLogoutClick = {
                    navController.navigate("auth") {
                        popUpTo("run") {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable(
            route = "active_run",
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "runique://active_run"
                }
            )
        ) {
            val context = LocalContext.current
            ActiveRunScreenRoot(
                onBack = {
                    navController.navigateUp()
                },
                onFinish = {
                    navController.navigateUp()
                },
                onServiceToggle = { shouldServiceRun ->
                    if (shouldServiceRun) {
                        context.startService(
                            ActiveRunService.createStartIntent(
                                context = context,
                                activityClass = MainActivity::class.java
                            )
                        )
                    } else {
                        context.startService(
                            ActiveRunService.createStopIntent(context = context)
                        )
                    }
                }
            )
        }
    }
}