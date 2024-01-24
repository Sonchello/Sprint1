package com.protei1sprint.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
fun Application.configureAuth(){
    install(Authentication) {
        basic("auth-basic") {
            realm = "local host"
            // sendWithoutRequest { true }
            validate { credentials ->
                if (credentials.name == "username" && credentials.password == "password") {
                    UserIdPrincipal(credentials.name)
                } else {
                    null
                }
            }
        }
    }
}