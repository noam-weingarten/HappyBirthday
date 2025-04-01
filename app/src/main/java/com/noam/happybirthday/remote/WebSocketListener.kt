package com.noam.happybirthday.remote

interface WebSocketListener {
    fun onConnected()
    fun onMessage(message: String)
    fun onDisconnected()
    fun onConnecting()
    fun onError(error: Throwable)

    companion object {
        val emptyListener = object : WebSocketListener {
            override fun onConnected() {}
            override fun onMessage(message: String) {}
            override fun onDisconnected() {}
            override fun onConnecting() {}
            override fun onError(error: Throwable) {}
        }
    }
}
