package com.quadrified.auth.presentation.register

import com.quadrified.core.presentation.ui.UiText

sealed interface RegisterEvent {
    data object RegistrationSuccess : RegisterEvent
    data class Error(val error: UiText) : RegisterEvent
}