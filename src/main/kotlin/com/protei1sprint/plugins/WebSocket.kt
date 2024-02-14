package com.protei1sprint.plugins
import io.ktor.serialization.kotlinx.*
import io.ktor.websocket.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import kotlinx.serialization.json.Json
import java.time.Duration
import java.util.*
import kotlin.collections.LinkedHashSet


class Connection(val session: DefaultWebSocketSession) {
    }

fun Application.configureWebSocket() {
    install(WebSockets) {
        contentConverter = KotlinxWebsocketSerializationConverter(Json)
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    routing {
        val connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())
        webSocket("/chat") {
            println("Adding user!")
            // Извлечение токена из заголовка запроса
            val token: String = call.request.headers["X-Auth-Token"].toString()
            // Проверка наличия активной сессии по токену
            if (token != null && sessionService.readSession(token) != null) {
                val thisConnection = Connection(this)
                connections += thisConnection
                try {
                    send("You are connected! There are ${connections.count()} users here.")
                    for (frame in incoming) {
                        frame as? Frame.Text ?: continue
                        // Декодирование JSON-строки в объект сообщения и сохранение сообщения
                        val message = Json.decodeFromString<Message>(frame.readText())
                        messageService.createMessage(message)
                        // Создание строки сообщения для передачи
                        val messageFrame = "${message.sender}: ${message.text}"
                        connections.forEach {
                            it.session.send(messageFrame)
                        }
                    }
                } catch (e: Exception) {
                    println(e.localizedMessage)
                } finally {
                    println("Removing $thisConnection!")
                    connections -= thisConnection
                }
            }
        }
    }
}