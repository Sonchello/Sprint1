package com.protei1sprint.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.routing.get
import java.util.*

fun Application.configureRouting() {
    routing {
        post("/api/reg") {
            try {
                val user = call.receive<User>()
                // Проверка наличия пользователя с указанным адресом электронной почты
                if (userService.readUser(user.email) != null) {
                    // Ответ со статусным кодом 302 (Найдено), если пользователь уже существует
                    call.respond(HttpStatusCode.Found)
                } else {
                    // Если пользователь не существует, создаем нового пользователя в базе данных
                    val id = userService.createUser(user)
                    // Ответ со статусным кодом 201 (Создан) и ID созданного пользователя
                    call.respond(HttpStatusCode.Created, id)
                }
            } catch (e: IllegalArgumentException) {
                // Некорректный запрос
                call.respond(HttpStatusCode.BadRequest, "invalid request: ${e.message}")
            }
        }

        post("/api/auth/login") {
            val credentials = call.receive<Credentials>()
            val credEmail = credentials.email
            val credPassword = credentials.password
            if (userService.readUser(credEmail, credPassword) == null) {
                call.respond(HttpStatusCode.NotFound, "User not found")
            } else {
                // Создание строки без кодировки токена из учетных данных пользователя
                val notEncodedToken = "$credEmail:$credPassword"
                // Кодирование токена в Base64
                val token: String = Base64.getEncoder().encodeToString(notEncodedToken.toByteArray())
                // Создание сессии для пользователя с полученным токеном
                sessionService.createSession(token)
                // Ответ с токеном аутентификации
                call.respond(HttpStatusCode.OK, token)
            }
        }

        post("api/auth/logout") {
            val token: String = call.request.headers["X-Auth-Token"].toString()
            // Удаление сессии пользователя по токену
            sessionService.deleteSession(token)
            call.respond(HttpStatusCode.OK)
        }

        post("api/auth/check-token") {
            val token: String = call.request.headers["X-Auth-Token"].toString()
            if (sessionService.readSession(token) == null){
                // Ответ о несуществующей сессии с указанным токеном
                call.respond(HttpStatusCode.Unauthorized,"No active session with this token")
            } else {
                // Ответ о действительности токена
                call.respond(HttpStatusCode.OK,"Token is valid")
            }
        }

        post("/chats") {
            val token: String = call.request.headers["X-Auth-Token"].toString()
            if (sessionService.readSession(token) == null){
                call.respond(HttpStatusCode.Unauthorized)
            } else {
                // Извлечение данных о чате из запроса
                val chat = call.receive<Chat>()
                // Создание чата в базе данных
                val id = chatService.createChat(chat)
                // Ответ с кодом "Создан" и ID созданного чата
                call.respond(HttpStatusCode.Created, id)
            }
        }

        get("/chats/{id}") {
            val token: String = call.request.headers["X-Auth-Token"].toString()
            if (sessionService.readSession(token) == null){
                call.respond(HttpStatusCode.Unauthorized)
            } else {
                // Извлечение ID чата из параметра запроса
                val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
                // Чтение информации о чате из базы данных
                val chat = chatService.readChat(id)
                // Ответ с информацией о чате или с ошибкой "Не найдено"
                if (chat != null) {
                    call.respond(HttpStatusCode.OK, chat)
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
        }
        put("/chats/{id}") {
            val token: String = call.request.headers["X-Auth-Token"].toString()
            if (sessionService.readSession(token) == null){
                call.respond(HttpStatusCode.Unauthorized)
            } else {
                val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
                // Извлечение данных о чате из запроса
                val chat = call.receive<Chat>()
                // Обновление информации о чате в базе данных
                chatService.updateChat(id, chat)
                // Ответ с кодом "ОК"
                call.respond(HttpStatusCode.OK)
            }
        }

        delete("/chats/{id}") {
            val token: String = call.request.headers["X-Auth-Token"].toString()
            if (sessionService.readSession(token) == null){
                call.respond(HttpStatusCode.Unauthorized)
            } else {
                val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Неверный ID")
                // Удаление чата из базы данных
                chatService.deleteChat(id)
                call.respond(HttpStatusCode.OK)
            }
        }

        get("/users/{id}") {
            val token: String = call.request.headers["X-Auth-Token"].toString()
            if (sessionService.readSession(token) == null){
                call.respond(HttpStatusCode.Unauthorized)
            } else {
                val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Неверный ID")
                // Чтение информации о пользователе из базы данных
                val user = userService.readUser(id)
                // Ответ с информацией о пользователе или с ошибкой "Не найдено"
                if (user != null) {
                    call.respond(HttpStatusCode.OK, user)
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
        }

        put("/users/{id}") {
            val token: String = call.request.headers["X-Auth-Token"].toString()
            if (sessionService.readSession(token) == null){
                call.respond(HttpStatusCode.Unauthorized)
            } else {
                val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Неверный ID")
                val user = call.receive<User>()
                // Обновление информации о пользователе в базе данных
                userService.updateUser(id, user)
                call.respond(HttpStatusCode.OK)
            }
        }

        delete("/users/{id}") {
            val token: String = call.request.headers["X-Auth-Token"].toString()
            if (sessionService.readSession(token) == null){
                call.respond(HttpStatusCode.Unauthorized)
            } else {
                val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Неверный ID")
                userService.deleteUser(id)
                call.respond(HttpStatusCode.OK)
            }
        }

        post("/messages") {
            val token: String = call.request.headers["X-Auth-Token"].toString()
            if (sessionService.readSession(token) == null){
                call.respond(HttpStatusCode.Unauthorized)
            } else {
                val message = call.receive<Message>()
                // Создание нового сообщения в базе данных
                val id = messageService.createMessage(message)
                // Ответ с кодом "Создан" и ID созданного сообщения
                call.respond(HttpStatusCode.Created, id)
            }
        }
        get("/messages/{id}") {
            val token: String = call.request.headers["X-Auth-Token"].toString()
            if (sessionService.readSession(token) == null){
                call.respond(HttpStatusCode.Unauthorized)
            } else {
                val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Неверный ID")
                // Получение информации о сообщении из базы данных
                val message = messageService.readMessage(id)
                // Ответ с информацией о сообщении или с кодом "Не найдено"
                if (message != null) {
                    call.respond(HttpStatusCode.OK, message)
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
        }
        put("/messages/{id}") {
            val token: String = call.request.headers["X-Auth-Token"].toString()
            if (sessionService.readSession(token) == null){
                call.respond(HttpStatusCode.Unauthorized)
            } else {
                val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Неверный ID")
                val message = call.receive<Message>()
                // Обновление информации о сообщении в базе данных
                messageService.updateMessage(id, message)
                call.respond(HttpStatusCode.OK)
            }
        }
        delete("/messages/{id}") {
            val token: String = call.request.headers["X-Auth-Token"].toString()
            if (sessionService.readSession(token) == null){
                call.respond(HttpStatusCode.Unauthorized)
            } else {
                val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Неверный ID")
                // Удаление сообщения из базы данных
                messageService.deleteMessage(id)
                // Ответ с кодом "ОК"
                call.respond(HttpStatusCode.OK)
            }
        }

    }
}
