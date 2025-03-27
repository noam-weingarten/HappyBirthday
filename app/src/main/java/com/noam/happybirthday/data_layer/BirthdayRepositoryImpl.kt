package com.noam.happybirthday.data_layer

import android.util.Log
import com.noam.happybirthday.model.BirthdayWish
import com.noam.happybirthday.remote.WebSocketClient
import com.noam.happybirthday.remote.WebSocketListener
import kotlinx.serialization.json.Json

class BirthdayRepositoryImpl(private val webSocketClient: WebSocketClient) : BirthdayRepository {

    private var connectionState: Boolean = false

    private val webSocketClientListener = object : WebSocketListener {
        override fun onConnected() {
            connectionState = true
        }

        override fun onMessage(message: String) {
            Log.d("TAG", "onMessage: received this message = $message")
            try {
                val birthdayWish = Json.decodeFromString<BirthdayWish>(message)
                Log.d("TAG", "onMessage: received birthdayWish = $birthdayWish")
            } catch (e: Exception) {
                Log.d("TAG", "onMessage: error occurred = $e")
            }
        }

        override fun onDisconnected() {
            connectionState = false
        }
    }

    private fun setUpWebSocketClient(ipAddress: String) {
        webSocketClient.setUrl(ipAddress)
    }

    override fun connectToServer(ipAddress: String) {
        setUpWebSocketClient(ipAddress)
        webSocketClient.connect(webSocketClientListener)
    }

    override fun disconnectFromServer() {
        webSocketClient.disconnect()
    }
}