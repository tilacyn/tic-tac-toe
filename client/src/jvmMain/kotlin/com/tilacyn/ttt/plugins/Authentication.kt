package com.tilacyn.ttt.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*

data class UserSession(val name: String) : Principal

fun Application.configureAuthentication() {
    install(Sessions) {
        cookie<UserSession>("user_session") {
            cookie.path = "/"
            cookie.maxAgeInSeconds = 60000
        }
    }
    install(Authentication) {
        form("auth-form") {
            userParamName = "username"
            passwordParamName = "password"
            validate { credentials ->
                // todo think about password
//                if (credentials.name == "hermione" && credentials.password == "123") {
                    UserIdPrincipal(credentials.name)
//                } else {
//                    null
//                }
            }
        }
        session<UserSession>("auth-session") {
            validate { session ->
                if(session.name != "") {
                    session
                } else {
                    null
                }
            }
            challenge {
                call.respondRedirect("/login")
            }
        }
    }
}

