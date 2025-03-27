package com.noam.happybirthday.remote

import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.wss
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class WebSocketClient {

    private var url: String = ""
    private lateinit var job : Job
    private val client = HttpClient {
        install(WebSockets)
    }

    fun setUrl(ipAddress: String) {
        this.url = "ws://$ipAddress/nanit"
    }

    fun connect(listener: WebSocketListener) {
        job = CoroutineScope(Dispatchers.IO).launch {
            client.wss(url) {
                listener.onConnected()

                outgoing.trySend(Frame.Text("HappyBirthday"))
                try {
                    for (frame in incoming) {
                        if (frame is Frame.Text) {
                            listener.onMessage(frame.readText())
                        }
                    }
                } catch (e: Exception) {
                    listener.onDisconnected()
                }
            }
        }
    }

    fun disconnect() {
        job.cancel()
        client.close()
    }
}