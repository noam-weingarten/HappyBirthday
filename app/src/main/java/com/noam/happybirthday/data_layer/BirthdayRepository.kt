package com.noam.happybirthday.data_layer

import com.noam.happybirthday.model.BirthdayWish
import kotlinx.coroutines.flow.Flow

interface BirthdayRepository {
    fun connectToServer(ipAddress: String)
    fun disconnectFromServer()
    val latestBirthdayWish: Flow<BirthdayWish>
}