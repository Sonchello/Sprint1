package com.protei1sprint.plugins

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction


fun Application.createTables() {
    transaction(database) {
        SchemaUtils.create(ActiveSessions)
        SchemaUtils.create(Users)
        SchemaUtils.create(Chats)
        SchemaUtils.create(Messages)
    }
}
