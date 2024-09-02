package com.quadrified.auth.domain

interface PatternValidator {
    fun matches(value: String): Boolean
}