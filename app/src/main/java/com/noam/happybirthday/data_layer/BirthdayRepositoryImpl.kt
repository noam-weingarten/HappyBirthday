package com.noam.happybirthday.data_layer

import android.util.Log
import com.noam.happybirthday.model.BirthdayWish
import com.noam.happybirthday.remote.WebSocketClient
import com.noam.happybirthday.remote.WebSocketListener
import com.noam.happybirthday.remote.WebSocketListenerEvent
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import java.time.Instant
import java.time.LocalDate
import java.time.Period

class BirthdayRepositoryImpl(private val webSocketClient: WebSocketClient) : BirthdayRepository {

    private var connectionState: Boolean = false
    private val webSocketListenerEvent = MutableSharedFlow<WebSocketListenerEvent>()

    private fun actionEvents() = callbackFlow {
        val listener = object : WebSocketListener {
            override fun onConnected() {
                trySend(WebSocketListenerEvent.Connected())
            }

            override fun onMessage(message: String) {
                trySend(WebSocketListenerEvent.MessageReceived(Json.decodeFromString(message)))
            }

            override fun onDisconnected() {
                trySend(WebSocketListenerEvent.Disconnected())
            }
        }

        registerListener(listener)

        awaitClose {
            unregisterListener(listener)
        }
    }

    private fun registerListener(listener: WebSocketListener) {
        webSocketClient.setListener(listener)
    }

    private fun unregisterListener(listener: WebSocketListener) {
        webSocketClient.unregisterListener(listener)
    }

    override var latestBirthdayWish: Flow<BirthdayWish> = flow {
            actionEvents().collect { event ->
                Log.d("Flow", "received event = ${event}")
                when(event) {
                    is WebSocketListenerEvent.Connected -> {}
                    is WebSocketListenerEvent.Disconnected -> {}
                    is WebSocketListenerEvent.MessageReceived -> {
                        val period = getAgeInMonths(event.birthdayWishApiModel.dob)
                        Log.d("Flow", "emitting the birthdayWishApiModel ${event.birthdayWishApiModel}")
                        val birthdayWish = BirthdayWish(
                            event.birthdayWishApiModel.name,
                            period,
                            event.birthdayWishApiModel.theme,
                        )
                        Log.d("Flow", "emitting the birthdayWish ${birthdayWish}")
                        emit(birthdayWish)
                    }
                }
            }
    }

    private fun getAgeInMonths(timestamp: Long): Period {
        val instant = Instant.ofEpochMilli(timestamp)
        val localDateBirth = instant.atZone(java.time.ZoneId.systemDefault()).toLocalDate()
        val currentDate = LocalDate.now()
        val period = Period.between(
            localDateBirth, currentDate
        )
        val months = period.toTotalMonths()
        Log.d("TAG", "getAgeInMonths: localDateBirth = $localDateBirth and currentDate = $currentDate, months = $months")
        return period
    }

    private fun setUpWebSocketClient(ipAddress: String) {
        webSocketClient.setUrl(ipAddress)
    }

    override fun connectToServer(ipAddress: String) {
        setUpWebSocketClient(ipAddress)
        webSocketClient.connect()
    }

    override fun disconnectFromServer() {
        webSocketClient.disconnect()
    }
}