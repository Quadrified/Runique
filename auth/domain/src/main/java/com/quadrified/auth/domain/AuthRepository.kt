package com.quadrified.auth.domain

import com.quadrified.core.domain.util.DataError
import com.quadrified.core.domain.util.EmptyResult

interface AuthRepository {

    suspend fun register(email: String, password: String): EmptyResult<DataError.Network>
}