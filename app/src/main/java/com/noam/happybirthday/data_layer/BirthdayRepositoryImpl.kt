package com.noam.happybirthday.data_layer

import com.noam.happybirthday.model.BirthdayWish
import com.noam.happybirthday.model.BirthdayWishApiModel
import com.noam.happybirthday.remote.WebSocketClient
import com.noam.happybirthday.remote.WebSocketListener
import com.noam.happybirthday.remote.WebSocketListenerEvent
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.serialization.json.Json
import java.time.Instant
import java.time.LocalDate
import java.time.Period

class BirthdayRepositoryImpl(private val webSocketClient: WebSocketClient) : BirthdayRepository {

    override fun webSocketFlow(ipAddress: String): Flow<WebSocketListenerEvent> = callbackFlow {
        val eventsListener = object : WebSocketListener {
            override fun onConnected() {
                trySend(WebSocketListenerEvent.Connected)
            }

            override fun onMessage(message: String) {
                val birthdayWishApiModel = Json.decodeFromString<BirthdayWishApiModel>(message)
                val period = getAgeInMonths(birthdayWishApiModel.dob)
                val birthdayWish = BirthdayWish(
                    birthdayWishApiModel.name,
                    period,
                    birthdayWishApiModel.theme,
                )
                trySend(WebSocketListenerEvent.MessageReceived(birthdayWish))
            }

            override fun onDisconnected() {
                trySend(WebSocketListenerEvent.Disconnected)
            }

            override fun onConnecting() {
                trySend(WebSocketListenerEvent.Connecting)
            }

            override fun onError(error: Throwable) {
                trySend(WebSocketListenerEvent.Error(error))
            }
        }

        webSocketClient.setUrl(ipAddress)
        webSocketClient.connect(eventsListener)
        awaitClose {
            webSocketClient.unregisterListener(eventsListener)
            webSocketClient.disconnect()
        }
    }

    private fun getAgeInMonths(timestamp: Long): Period {
        val instant = Instant.ofEpochMilli(timestamp)
        val localDateBirth = instant.atZone(java.time.ZoneId.systemDefault()).toLocalDate()
        val currentDate = LocalDate.now()
        val period = Period.between(
            localDateBirth, currentDate
        )
        return period
    }

    override fun disconnectFromServer() {
        webSocketClient.disconnect()
    }
}