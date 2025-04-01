package com.noam.happybirthday.data_layer

import com.noam.happybirthday.model.BirthdayWish
import com.noam.happybirthday.utils.ConnectionState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface BirthdayRepository {
    fun connectToServer(ipAddress: String)
    fun disconnectFromServer()
    val connectionState: StateFlow<ConnectionState>
    val latestBirthdayWish: Flow<BirthdayWish>
}