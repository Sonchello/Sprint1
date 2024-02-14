package com.protei1sprint.plugins
import kotlinx.serialization.Serializable

//Модель данных для входа
@Serializable
data class Credentials(val email: String, val password: String)
data class Session(val token: String)

// Модель пользователя
@Serializable
data class User(val name: String, val email: String, val password: String)

// Модель чата
@Serializable
data class Chat(val name: String, val participants: String)

// Модель сообщения
@Serializable
data class Message(val sender: String, val chat: String, val text: String)
