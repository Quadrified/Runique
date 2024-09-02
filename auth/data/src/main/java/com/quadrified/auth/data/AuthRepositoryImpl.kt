package com.quadrified.auth.data

import com.quadrified.auth.domain.AuthRepository
import com.quadrified.core.data.networking.post
import com.quadrified.core.domain.util.DataError
import com.quadrified.core.domain.util.EmptyResult
import io.ktor.client.HttpClient

class AuthRepositoryImpl(
    private val httpClient: HttpClient
) : AuthRepository {
    override suspend fun register(email: String, password: String): EmptyResult<DataError.Network> {
        return httpClient.post<RegisterRequest, Unit>(
            route = "/register",
            body = RegisterRequest(
                email = email,
                password = password
            ),
        )
    }
}