package com.quadrified.core.data.auth

import kotlinx.serialization.Serializable

// Data Model
@Serializable
data class AuthInfoSerializable(
    val accessToken: String,
    val refreshToken: String,
    val userId: String,
)
