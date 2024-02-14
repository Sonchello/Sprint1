package com.protei1sprint.plugins

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import kotlinx.coroutines.withContext

// Функция для выполнения запросов к базе данных с использованием Exposed
suspend fun <T> dbQuery(block: () -> T): T {
    return withContext(Dispatchers.IO) {
        transaction { block() } // Запуск транзакции
    }
}
class SessionService(private val database: Database) {
    // Создание новой сессии с токеном
    suspend fun createSession(newToken: String): Int {
        return dbQuery {
            // Вставка новой записи в таблицу ActiveSessions
            ActiveSessions.insert {
                it[token] = newToken // Значение токена для новой сессии
            }[ActiveSessions.id] // Возвращение id созданной записи
        }
    }

    suspend fun readSession(token: String): Session? {
        return dbQuery {
            // Выбор записи из таблицы ActiveSessions, где значение токена равно переданному токену
            ActiveSessions.select { ActiveSessions.token eq token }
                // Преобразование результата в список объектов Session
                .map { Session(
                    it[ActiveSessions.token] // Создание объекта Session с переданным токеном
                ) }
                .singleOrNull()
        }
    }

    // Функция для удаления сессии по токену
    suspend fun deleteSession(token: String) {
        dbQuery {
            // Удаление записи из таблицы ActiveSessions, где значение токена равно переданному токену
            ActiveSessions.deleteWhere { ActiveSessions.token.eq(token) }
        }
    }
}
// Операции с пользователями
class UserService(private val database: Database) {

    // Создание нового пользователя в базе данных
    suspend fun createUser(user: User): Int = dbQuery {
        // Вставка данных пользователя в таблицу Users
        Users.insert {
            it[name] = user.name
            it[email] = user.email
            it[password] = user.password
        }[Users.id]
    }

    // Чтение информации о пользователе из базы данных по его идентификатору
    suspend fun readUser(id: Int): User? {
        return dbQuery {
            // Выбор записи из таблицы Users, где идентификатор равен переданному id
            Users.select { Users.id eq id }
                // Преобразование результата в список объектов ExposedUser
                .map { User(
                    it[Users.name],
                    it[Users.email],
                    it[Users.password]
                ) }
                .singleOrNull() // Возвращаем одну запись или null, если записей не найдено
        }
    }

    // Чтение информации о пользователе из базы данных по его электронной почте
    suspend fun readUser(email: String): User? {
        return dbQuery {
            // Выбираем запись из таблицы Users, где электронная почта равна переданной email
            Users.select { Users.email eq email }
                // Преобразуем результат в список объектов ExposedUser
                .map { User(
                    it[Users.name],
                    it[Users.email],
                    it[Users.password]
                ) }
                .singleOrNull()
        }
    }

    suspend fun readUser(email: String,password: String): User? {
        return dbQuery {
            // Выбираем запись из таблицы Users, где электронная почта равна переданной email
            Users.select { Users.email eq email; Users.password eq password  }
                // Преобразуем результат в список объектов ExposedUser
                .map { User(
                    it[Users.name],
                    it[Users.email],
                    it[Users.password]
                ) }
                .singleOrNull()
        }
    }
    // Обновление информации о пользователе в базе данных по его идентификатору
    suspend fun updateUser(id: Int, user: User) {
        dbQuery {
            Users.update({ Users.id eq id }) {
                it[name] = user.name
                it[email] = user.email
                it[password] = user.password
            }
        }
    }

    // Удаление пользователя из базы данных по его идентификатору
    suspend fun deleteUser(id: Int) {
        dbQuery {
            Users.deleteWhere { Users.id.eq(id) }
        }
    }
}


class ChatService(private val database: Database) {
    // Создание нового чата в БД
    suspend fun createChat(chat: Chat): Int = dbQuery {
        Chats.insert {
            it[name] = chat.name
            it[participants] = chat.participants
        }[Chats.id]
    }

    // Чтение информации о чате из БД по идентификатору
    suspend fun readChat(id: Int): Chat? {
        return dbQuery {
            Chats.select { Chats.id eq id } // Выбор чата по его идентификатору
                .map { Chat(
                    it[Chats.name],
                    it[Chats.participants]
                ) }
                .singleOrNull()
        }
    }

    // Обновление информации о чате из БД
    suspend fun updateChat(id: Int, chat: Chat) {
        dbQuery {
            Chats.update({ Chats.id eq id }) {
                it[name] = chat.name // Обновление имени чата
                it[participants] = chat.participants // Обновление списка участников чата
            }
        }
    }

    // Удаление чата из БД по идентификатору
    suspend fun deleteChat(id: Int) {
        dbQuery {
            Chats.deleteWhere { Chats.id.eq(id) }
        }
    }
}


// Операции с сообщениями
class MessageService(private val database: Database) {
    // Создание нового сообщения в БД
    suspend fun createMessage(message: Message): Int = dbQuery {
        Messages.insert {
            it[sender] = message.sender
            it[chat] = message.chat
            it[text] = message.text
        }[Messages.id]
    }

    // Чтение информации о сообщении из БД по идентификатору
    suspend fun readMessage(id: Int): Message? {
        return dbQuery {
            Messages.select { Messages.id eq id } // Выбор сообщения по его идентификатору
                .map { Message(
                    it[Messages.sender],
                    it[Messages.chat],
                    it[Messages.text]
                )}
                .singleOrNull()
        }
    }

    // Обновление информации о сообщении из БД
    suspend fun updateMessage(id: Int, message: Message) {
        dbQuery {
            Messages.update({ Messages.id eq id }) {
                it[sender] = message.sender
                it[chat] = message.chat
                it[text] = message.text
            }
        }
    }

    // Удаление сообщения из БД по идентификатору
    suspend fun deleteMessage(id: Int) {
        dbQuery {
            Messages.deleteWhere { Messages.id.eq(id) }
        }
    }
}
