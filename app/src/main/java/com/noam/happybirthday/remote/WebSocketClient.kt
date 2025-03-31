package com.noam.happybirthday.remote

import android.util.Log
import com.noam.happybirthday.remote.WebSocketListener.Companion.emptyListener
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.wss
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class WebSocketClient {

    private var url: String = ""
    private lateinit var job : Job
    private var received = ""
    private lateinit var listener : WebSocketListener
    private val client = HttpClient {
        install(WebSockets)
    }

    fun setUrl(ipAddress: String) {
        this.url = "ws://$ipAddress/nanit"
    }

    fun connect() {
        job = CoroutineScope(Dispatchers.IO).launch {
            client.wss(url) {
                listener.onConnected()

                outgoing.trySend(Frame.Text("HappyBirthday"))
                try {
                    for (frame in incoming) {
                        if (frame is Frame.Text) {
                            received = frame.readText()
                            listener.onMessage(received)
                            Log.d("Flow", "incoming received message $received")
                        }
                    }
                } catch (e: Exception) {
                    Log.e("WebSocketClient", "Error in WebSocket connection: ${e.message}")
                    listener.onDisconnected()
                }
            }
        }
    }

    fun disconnect() {
        client.close()
        job.cancel()
    }

    fun setListener(listener: WebSocketListener) {
        this.listener = listener
    }

    fun unregisterListener(listener: WebSocketListener) {
        if (this.listener == listener) {
            this.listener = emptyListener
        }
    }
}