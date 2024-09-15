package com.quadrified.wear.run.presentation

sealed interface TrackerEvent {
    data object RunFinished : TrackerEvent
}