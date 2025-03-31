package com.noam.happybirthday.remote

import com.noam.happybirthday.model.BirthdayWishApiModel

sealed interface WebSocketListenerEvent {
    class Connected : WebSocketListenerEvent
    class MessageReceived(val birthdayWishApiModel: BirthdayWishApiModel): WebSocketListenerEvent
    class Disconnected: WebSocketListenerEvent
}