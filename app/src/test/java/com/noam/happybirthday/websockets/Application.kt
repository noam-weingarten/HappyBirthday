package com.noam.happybirthday.websockets

import io.ktor.server.application.*
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    routing {
        get("/nanit") {
            val text = call.receive<String>()
            println("received the text = $text")
            if (text == "HappyBirthday") {
                call.respondText("Hello, world!")
            } else {
                call.respondText("Do I know you?")
            }
        }
    }
}