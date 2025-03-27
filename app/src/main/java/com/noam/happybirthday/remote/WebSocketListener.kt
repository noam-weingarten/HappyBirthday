package com.noam.happybirthday.remote

interface WebSocketListener {
    fun onConnected()
    fun onMessage(message: String)
    fun onDisconnected()
}