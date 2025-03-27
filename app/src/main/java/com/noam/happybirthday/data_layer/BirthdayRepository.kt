package com.noam.happybirthday.data_layer

interface BirthdayRepository {
    fun connectToServer(ipAddress: String)
    fun disconnectFromServer()
}