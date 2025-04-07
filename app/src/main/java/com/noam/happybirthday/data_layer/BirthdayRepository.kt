package com.noam.happybirthday.data_layer

import com.noam.happybirthday.remote.WebSocketListenerEvent
import kotlinx.coroutines.flow.Flow

interface BirthdayRepository {
    fun disconnectFromServer()
    fun webSocketFlow(ipAddress: String): Flow<WebSocketListenerEvent>
}