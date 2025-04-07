package com.noam.happybirthday.remote

import android.util.Log
import com.noam.happybirthday.remote.WebSocketListener.Companion.emptyListener
import com.noam.happybirthday.utils.SERVER_MESSAGE_STARTER
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.wss
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class WebSocketClient {

    private var url: String = ""
    private lateinit var job : Job
    private var received = ""
    private lateinit var listener : WebSocketListener
    private var retryCounter = 0
    private val client = HttpClient {
        install(HttpTimeout) {
            requestTimeoutMillis = 5000
            connectTimeoutMillis = 3000
            socketTimeoutMillis = 3000
        }
        install(WebSockets) {
            pingInterval = 1000
        }
    }

    fun setUrl(ipAddress: String) {
        this.url = "ws://$ipAddress/nanit"
    }

    fun connect(eventsListener: WebSocketListener) {
        Log.d("WebSocketClient", "connect: $url")
        this.listener = eventsListener
        listener.onConnecting()
        job = CoroutineScope(Dispatchers.Default).launch {
            while (isActive) {
                try {
                    client.wss(url) {
                        listener.onConnected()
                        retryCounter = 0

                        Log.d("Websocket", "connect: sending the happyBirthday frame")
                        outgoing.trySend(Frame.Text(SERVER_MESSAGE_STARTER))
                        try {
                            for (frame in incoming) {
                                if (frame is Frame.Text) {
                                    received = frame.readText()
                                    listener.onMessage(received)
                                    Log.d("Websocket", "incoming received message $received")
                                }
                                if (frame is Frame.Close) {
                                    Log.d("Websocket", "incoming received close frame")
                                    listener.onDisconnected()
                                    break
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("WebSocketClient", "Error in WebSocket connection: ${e.message}")
                            listener.onDisconnected()
                        }
                    }
                } catch (e: Exception) {
                    Log.e("WebSocketClient", "Error connecting to WebSocket: ${e.message}")
                    listener.onDisconnected()
                    retry()
                    return@launch
                }
                Log.d("WebSocketClient", "WebSocket connection closed, reconnecting...")
            }
            Log.d("WebSocketClient", "WebSocket connection closed cancelling job")
            listener.onDisconnected()
        }
    }

    private fun retry() {
        job.cancel()
        CoroutineScope(Dispatchers.Default).launch {
            if (retryCounter < 3) {
                retryCounter++
                listener.onConnecting()
                delay(2000)
                connect(listener)
            } else {
                Log.d("WebSocketClient", "Max retry attempts reached. Not reconnecting.")
                listener.onError(Exception("Max retry attempts reached"))
                retryCounter = 0
            }
        }
    }

    fun disconnect() {
        client.close()
        job.cancel()
    }

    fun unregisterListener(listener: WebSocketListener) {
        if (this.listener == listener) {
            this.listener = emptyListener
        }
    }
}