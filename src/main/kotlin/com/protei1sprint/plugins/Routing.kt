package com.protei1sprint.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.routing.get
import org.jetbrains.exposed.sql.Database
import java.util.*

fun Application.configureRouting() {
    val database = Database.connect(
        url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
        user = "root",
        driver = "org.h2.Driver",
        password = ""
    )

    val chatService = ChatService(database)
    val userService = UserService(database)
    val messageService = MessageService(database)

    routing {
        authenticate("auth-basic") {
            post("api/auth/login"){
                val user = call.receive<ExposedUser>()
                val email = user.email
                val password = user.password
                val credentials = "$email:$password"
                val token: String = Base64.getEncoder().encodeToString(credentials.toByteArray())
                call.respond(HttpStatusCode.OK, token)
            }

            post("api/auth/logout"){
                call.respond(HttpStatusCode.OK)
            }

            post("api/auth/check-token"){
                val user = call.receive<ExposedUser>()
                val receivedToken = call.request.authorization().toString()
                val email = user.email
                val password = user.password
                val credentials = "$email:$password"
                val token = "Basic " + Base64.getEncoder().encodeToString(credentials.toByteArray())
                if (token == receivedToken){
                    call.respond(HttpStatusCode.OK)
                } else {
                    call.respondText("Invalid Token")
                }

            }

            // Chats
            // Create chat
            post("/chats") {
                val chat = call.receive<ExposedChat>()
                val id = chatService.create(chat)
                call.respond(HttpStatusCode.Created, id)
            }
            // Read chat
            get("/chats/{id}") {
                val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
                val chat = chatService.read(id)
                if (chat != null) {
                    call.respond(HttpStatusCode.OK, chat)
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
            // Update chat
            put("/chats/{id}") {
                val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
                val chat = call.receive<ExposedChat>()
                chatService.update(id, chat)
                call.respond(HttpStatusCode.OK)
            }
            // Delete chat
            delete("/chats/{id}") {
                val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
                chatService.delete(id)
                call.respond(HttpStatusCode.OK)
            }
            // Users
            // Create user
            post("/users") {
                val user = call.receive<ExposedUser>()
                val id = userService.create(user)
                call.respond(HttpStatusCode.Created, id)
            }
            // Read user
            get("/users/{id}") {
                val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
                val user = userService.read(id)
                if (user != null) {
                    call.respond(HttpStatusCode.OK, user)
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
            // Update user
            put("/users/{id}") {
                val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
                val user = call.receive<ExposedUser>()
                userService.update(id, user)
                call.respond(HttpStatusCode.OK)
            }
            // Delete user
            delete("/users/{id}") {
                val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
                userService.delete(id)
                call.respond(HttpStatusCode.OK)
            }
            // Messages
            // Create message
            post("/messages") {
                val message = call.receive<ExposedMessage>()
                val id = messageService.create(message)
                call.respond(HttpStatusCode.Created, id)
            }
            // Read user
            get("/messages/{id}") {
                val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
                val message = messageService.read(id)
                if (message != null) {
                    call.respond(HttpStatusCode.OK, message)
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
            // Update user
            put("/messages/{id}") {
                val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
                val message = call.receive<ExposedMessage>()
                messageService.update(id, message)
                call.respond(HttpStatusCode.OK)
            }
            // Delete user
            delete("/messages/{id}") {
                val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
                messageService.delete(id)
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}
