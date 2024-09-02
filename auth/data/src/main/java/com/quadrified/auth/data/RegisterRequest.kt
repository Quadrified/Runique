package com.quadrified.auth.data

import kotlinx.serialization.Serializable

// Should be same as API request body
@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
)