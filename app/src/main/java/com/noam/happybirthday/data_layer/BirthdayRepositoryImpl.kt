package com.noam.happybirthday.data_layer

import com.noam.happybirthday.model.BirthdayWish
import com.noam.happybirthday.remote.WebSocketClient
import com.noam.happybirthday.remote.WebSocketListener
import com.noam.happybirthday.remote.WebSocketListenerEvent
import com.noam.happybirthday.utils.ConnectionState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import java.time.Instant
import java.time.LocalDate
import java.time.Period

class BirthdayRepositoryImpl(private val webSocketClient: WebSocketClient) : BirthdayRepository {

    private val _connectionState: MutableStateFlow<ConnectionState> = MutableStateFlow(ConnectionState.DISCONNECTED)
    override val connectionState: StateFlow<ConnectionState>
        get() = _connectionState

    private val eventsFlow: MutableStateFlow<WebSocketListenerEvent> = MutableStateFlow(WebSocketListenerEvent.Disconnected)

    private val eventsListener = object : WebSocketListener {
        override fun onConnected() {
            eventsFlow.value = (WebSocketListenerEvent.Connected)
        }

        override fun onMessage(message: String) {
            eventsFlow.value = (WebSocketListenerEvent.MessageReceived(Json.decodeFromString(message)))
        }

        override fun onDisconnected() {
            eventsFlow.value = (WebSocketListenerEvent.Disconnected)
        }

        override fun onConnecting() {
            eventsFlow.value = (WebSocketListenerEvent.Connecting)
        }

        override fun onError(error: Throwable) {
            eventsFlow.value = (WebSocketListenerEvent.Error(error))
        }
    }

    private fun registerListener(listener: WebSocketListener) {
        webSocketClient.setListener(listener)
    }

    private fun unregisterListener(listener: WebSocketListener) {
        webSocketClient.unregisterListener(listener)
    }

    override var latestBirthdayWish: Flow<BirthdayWish> = flow {
        eventsFlow.collect { event ->
            when(event) {
                is WebSocketListenerEvent.Connecting -> { _connectionState.value = ConnectionState.CONNECTING }
                is WebSocketListenerEvent.Connected -> { _connectionState.value = ConnectionState.CONNECTED }
                is WebSocketListenerEvent.Disconnected -> { _connectionState.value = ConnectionState.DISCONNECTED }
                is WebSocketListenerEvent.Error -> { _connectionState.value = ConnectionState.ERROR }
                is WebSocketListenerEvent.MessageReceived -> {
                    val period = getAgeInMonths(event.birthdayWishApiModel.dob)
                    val birthdayWish = BirthdayWish(
                        event.birthdayWishApiModel.name,
                        period,
                        event.birthdayWishApiModel.theme,
                    )
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
        return period
    }

    private fun setUpWebSocketClient(ipAddress: String) {
        webSocketClient.setUrl(ipAddress)
        registerListener(eventsListener)
    }

    override fun connectToServer(ipAddress: String) {
        setUpWebSocketClient(ipAddress)
        webSocketClient.connect()
    }

    override fun disconnectFromServer() {
        unregisterListener(eventsListener)
        webSocketClient.disconnect()
    }
}