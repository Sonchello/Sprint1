package com.protei1sprint.plugins

import org.jetbrains.exposed.sql.Table

object ActiveSessions : Table() {
    val id = integer("id").autoIncrement()
    val token = varchar("token",20)

    override val primaryKey = PrimaryKey(id)
}
object Users : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 20)
    val email = varchar("email",20)
    val password = varchar("password",20)

    override val primaryKey = PrimaryKey(id) // Определение первичного ключа
}
object Chats : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 30)
    val participants = text("participants")

    override val primaryKey = PrimaryKey(id) //Определение первичного ключа
}
object Messages : Table() {
    val id = integer("id").autoIncrement()
    val sender = varchar("sender", 20)
    val chat = varchar("chat",20)
    val text = varchar("text",256)

    override val primaryKey = PrimaryKey(id) //Определение первичного ключа
}