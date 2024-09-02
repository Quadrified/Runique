package com.quadrified.core.data.auth

import com.quadrified.core.domain.AuthInfo


// Mapping things from AuthInfo (from domain) to AuthInfoSerializable (from data)
fun AuthInfo.toAuthInfoSerializable(): AuthInfoSerializable {
    return AuthInfoSerializable(
        accessToken = accessToken,
        refreshToken = refreshToken,
        userId = userId,
    )
}


// Mapping things from AuthInfoSerializable (from data) to AuthInfo (from domain)
fun AuthInfoSerializable.toAuthInfo(): AuthInfo {
    return AuthInfo(
        accessToken = accessToken,
        refreshToken = refreshToken,
        userId = userId,
    )
}