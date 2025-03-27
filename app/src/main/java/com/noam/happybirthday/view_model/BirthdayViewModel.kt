package com.noam.happybirthday.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noam.happybirthday.data_layer.BirthdayRepository
import kotlinx.coroutines.launch

class BirthdayViewModel(private val repository: BirthdayRepository) : ViewModel() {

    fun connectToServer(ipAddress: String) {
        viewModelScope.launch {
            repository.connectToServer(ipAddress)

        }
    }
}