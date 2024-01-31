package com.protei1sprint.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*

data class UserCredentials(val name: String, val password: String)

// Функция для конфигурации аутентификации
fun Application. configureAuthentication() {
    install(Authentication) {
        basic("auth-basic") {
            realm = "localhost"
            // Функция для валидации учетных данных
            validate { credentials ->
                // Проверка, являются ли переданные учетные данные объектом UserPasswordCredential
                if (credentials is UserPasswordCredential) {
                    val userCredentials = UserCredentials(credentials.name, credentials.password)// Преобразование UserPasswordCredential в объект Credentials
                    validateUserCredentials(userCredentials)?.let { UserIdPrincipal(it.name) }// Вызов функции для валидации пользовательских учетных данных  и преобразование результата в UserIdPrincipal
                } else {
                    null
                }
            }
        }
    }
}
fun validateUserCredentials(credentials: UserCredentials): UserCredentials? {
    return if (credentials.name == "username" && credentials.password == "password") {
        credentials
    } else {
        null
    }
}
