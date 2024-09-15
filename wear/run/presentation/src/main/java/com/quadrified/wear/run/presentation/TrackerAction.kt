package com.quadrified.wear.run.presentation

sealed interface TackerAction {
    data object OnToggleRunClick: TrackerAction
}