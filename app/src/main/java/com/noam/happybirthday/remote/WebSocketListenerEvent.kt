package com.noam.happybirthday.remote

import com.noam.happybirthday.model.BirthdayWish

sealed interface WebSocketListenerEvent {
    data object Connected : WebSocketListenerEvent
    class MessageReceived(val birthdayWish: BirthdayWish): WebSocketListenerEvent
    data object Disconnected : WebSocketListenerEvent
    data object Connecting : WebSocketListenerEvent
    class Error(val error: Throwable): WebSocketListenerEvent
}