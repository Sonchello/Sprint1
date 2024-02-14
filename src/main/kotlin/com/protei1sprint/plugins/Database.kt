package com.protei1sprint.plugins

import org.jetbrains.exposed.sql.Database


val database = Database.connect(
    url = "jdbc:postgresql://localhost:5432/myDatabase",
    user = "root",
    password = "1234",
    driver = "org.postgresql.Driver"
)


val chatService = ChatService(database)
val userService = UserService(database)
val messageService = MessageService(database)
val sessionService = SessionService(database)
