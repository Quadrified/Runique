package com.quadrified.auth.data

import com.quadrified.auth.domain.AuthRepository
import com.quadrified.core.data.networking.post
import com.quadrified.core.domain.AuthInfo
import com.quadrified.core.domain.SessionStorage
import com.quadrified.core.domain.util.DataError
import com.quadrified.core.domain.util.EmptyResult
import com.quadrified.core.domain.util.Result
import com.quadrified.core.domain.util.asEmptyDataResult
import io.ktor.client.HttpClient

class AuthRepositoryImpl(
    private val httpClient: HttpClient,
    private val sessionStorage: SessionStorage
) : AuthRepository {
    override suspend fun register(email: String, password: String): EmptyResult<DataError.Network> {
        // post<WhatWeSend, WhatWeReceive>
        return httpClient.post<RegisterRequest, Unit>(
            route = "/register",
            body = RegisterRequest(
                email = email, password = password
            ),
        )
    }

    override suspend fun login(email: String, password: String): EmptyResult<DataError.Network> {
        // post<WhatWeSend, WhatWeReceive>
        val result = httpClient.post<LoginRequest, LoginResponse>(
            route = "/login", body = LoginRequest(
                email = email, password = password
            )
        )
        if(result is Result.Success) {
            sessionStorage.set(
                AuthInfo(
                    accessToken = result.data.accessToken,
                    refreshToken = result.data.refreshToken,
                    userId = result.data.userId,
                )
            )
        }

        return result.asEmptyDataResult()
    }
}