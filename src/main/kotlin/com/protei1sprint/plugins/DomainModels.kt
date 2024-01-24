package com.protei1sprint.plugins

import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import kotlinx.serialization.Serializable
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*

// Доменная модель Пользователя
@Serializable
data class ExposedUser(val name: String, val email: String, val password: String)
// Операции с пользователями
class UserService(private val database: Database) {
    object Users : Table() {
        val id = integer("id").autoIncrement()
        val name = varchar("name", 20)
        val email = varchar("email",20)
        val password = varchar("password",20)

        override val primaryKey = PrimaryKey(id) // Определение первичного ключа
    }

    init {
        transaction(database) {
            SchemaUtils.create(Users) // Создание таблицы Users в БД
        }
    }
    // Функция для выполнения асинхронных операций с БД
    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
    // Создание нового пользователя в БД
    suspend fun create(user: ExposedUser): Int = dbQuery {
        Users.insert {
            it[name] = user.name
            it[email] = user.email
            it[password] = user.password
        }[Users.id]
    }
    // Чтение информации о пользователе из БД по идентификатору
    suspend fun read(id: Int): ExposedUser? {
        return dbQuery {
            Users.select { Users.id eq id }
                .map { ExposedUser(it[Users.name], it[Users.email], it[Users.password]) }
                .singleOrNull()
        }
    }
    // Обновление информации о пользователе в БД
    suspend fun update(id: Int, user: ExposedUser) {
        dbQuery {
            Users.update({ Users.id eq id }) {
                it[name] = user.name
                it[email] = user.email
                it[password] = user.password
            }
        }
    }
    // Удаление пользователя из БД по идентификатору
    suspend fun delete(id: Int) {
        dbQuery {
            Users.deleteWhere { Users.id.eq(id) }
        }
    }
}

// Доменная модель чата
@Serializable
data class ExposedChat(val name: String, val participants: String)
// Операции с чатами
class ChatService(private val database: Database) {
    object Chats : Table() {
        val id = integer("id").autoIncrement()
        val name = varchar("name", 30)
        val participants = text("participants")

        override val primaryKey = PrimaryKey(id) //Определение первичного ключа
    }

    init {
        transaction(database) {
            SchemaUtils.create(Chats)// Создание таблицы Chats в БД
        }
    }
    // Функция для выполнения асинхронных операций с БД
    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
    // Создание нового чата в БД
    suspend fun create(chat: ExposedChat): Int = dbQuery {
        Chats.insert {
            it[name] = chat.name
            it[participants] = chat.participants
        }[Chats.id]
    }
    // Чтение информации о чате из БД по идентификатору
    suspend fun read(id: Int): ExposedChat? {
        return dbQuery {
            Chats.select { Chats.id eq id }
                .map { ExposedChat(it[Chats.name],  it[Chats.participants]) }
                .singleOrNull()
        }
    }
    // Обновление информации о чате из БД
    suspend fun update(id: Int, chat: ExposedChat) {
        dbQuery {
            Chats.update({ Chats.id eq id }) {
                it[name] = chat.name
                it[participants] = chat.participants
            }
        }
    }
    // Удаление чата из БД по идентификатору
    suspend fun delete(id: Int) {
        dbQuery {
            Chats.deleteWhere { Chats.id.eq(id) }
        }
    }
}

// Доменная модель сообщения
@Serializable
data class ExposedMessage(val sender: String, val chat: String, val body: String)
// Операции с сообщениями
class MessageService(private val database: Database) {
    object Messages : Table() {
        val id = integer("id").autoIncrement()
        val sender = varchar("sender", 20)
        val chat = varchar("chat",20)
        val body = varchar("body",256)

        override val primaryKey = PrimaryKey(id) //Определение первичного ключа
    }

    init {
        transaction(database) {
            SchemaUtils.create(Messages) // Создание таблицы Messages в БД
        }
    }
    // Функция для выполнения асинхронных операций с БД
    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
    // Создание нового сообщения в БД
    suspend fun create(message: ExposedMessage): Int = dbQuery {
        Messages.insert {
            it[sender] = message.sender
            it[chat] = message.chat
            it[body] = message.body
        }[Messages.id]
    }
    // чтение информации о сообщении из БД
    suspend fun read(id: Int): ExposedMessage? {
        return dbQuery {
            Messages.select { Messages.id eq id }
                .map {ExposedMessage(
                    it[Messages.sender],
                    it[Messages.chat],
                    it[Messages.body]
                )}
                .singleOrNull()
        }
    }
    // Обновление информации о сообщении из БД
    suspend fun update(id: Int, message: ExposedMessage) {
        dbQuery {
            Messages.update({ Messages.id eq id }) {
                it[sender] = message.sender
                it[chat] = message.chat
                it[body] = message.body
            }
        }
    }
    // Удаление сообщения из БД по идентификатору
    suspend fun delete(id: Int) {
        dbQuery {
            Messages.deleteWhere { Messages.id.eq(id) }
        }
    }
}