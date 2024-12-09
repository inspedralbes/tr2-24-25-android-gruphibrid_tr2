package com.example.gomath.model

data class Usuari (
    val email: String,
    val role: String
)

data class LoginRequest (
    val email: String,
    val password: String
)
