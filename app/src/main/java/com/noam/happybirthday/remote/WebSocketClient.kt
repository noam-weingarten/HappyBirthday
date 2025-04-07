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
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WebSocketClient {

    private var url: String = ""
    private lateinit var job : Job
    private var received = ""
    private lateinit var listener : WebSocketListener
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


    private val handler = CoroutineExceptionHandler { _, exception ->
        Log.e("Exception","CoroutineExceptionHandler got $exception")
        listener.onError(Exception("Max retry attempts reached"))
        job.cancel()
    }

    fun connect(eventsListener: WebSocketListener) {
        Log.d("WebSocketClient", "connect: $url")
        this.listener = eventsListener
        listener.onConnecting()
        job = CoroutineScope(Dispatchers.Default).launch(handler) {
            retry(numberOfRetries = 3, delayBetweenRetries = 2000L) {
                connectToWebSocket()
            }
        }
    }

    private suspend fun connectToWebSocket() {
        client.wss(url) {
            listener.onConnected()

            Log.d("Websocket", "connect: sending the happyBirthday frame")
            outgoing.trySend(Frame.Text(SERVER_MESSAGE_STARTER))
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
        }
    }

    private suspend fun <T> retry(
        numberOfRetries: Int,
        delayBetweenRetries: Long = 100,
        block: suspend () -> T
    ): T {
        repeat(numberOfRetries) {
            try {
                return block()
            } catch (exception: Exception) {
                Log.e("retry", "Retry got an exception =$exception")
            }
            delay(delayBetweenRetries)
        }
        return block() // last attempt
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