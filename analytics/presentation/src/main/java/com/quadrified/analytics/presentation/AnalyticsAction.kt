package com.quadrified.analytics.presentation

sealed interface AnalyticsAction {
    data object OnBackClick : AnalyticsAction
}