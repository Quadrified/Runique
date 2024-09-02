package com.quadrified.core.domain

data class AuthInfo(
    val accessToken: String, // JWT token
    val refreshToken: String,
    val userId: String,
)
