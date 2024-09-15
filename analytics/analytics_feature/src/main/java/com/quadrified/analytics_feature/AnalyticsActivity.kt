package com.quadrified.analytics_feature

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.play.core.splitcompat.SplitCompat
import com.quadrified.analytics.data.di.analyticsModule
import com.quadrified.analytics.presentation.AnalyticsDashboardScreenRoot
import com.quadrified.analytics.presentation.di.analyticsPresentationModule
import com.quadrified.core.presentation.designsystem.RuniqueTheme
import org.koin.core.context.loadKoinModules

/**
 * Launched via "Reflection":
 * Mechanism that allows us to open a specific class by providing class name at run time
 * Link package name with activity and fire off an Intent with the activity
 * Need proper checks else app crashes
 */

class AnalyticsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadKoinModules(
            listOf(
                analyticsModule,
                analyticsPresentationModule
            )
        )

        // SplitCompat comes from "core" dependency
        SplitCompat.installActivity(this)

        setContent {
            RuniqueTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "analytics_dashboard"
                ) {
                    composable("analytics_dashboard") {
                        AnalyticsDashboardScreenRoot(onBackClick = { finish() })
                    }
                }
            }
        }
    }
}