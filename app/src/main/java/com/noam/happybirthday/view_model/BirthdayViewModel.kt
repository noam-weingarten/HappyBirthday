package com.noam.happybirthday.view_model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noam.happybirthday.data_layer.BabyImageRepository
import com.noam.happybirthday.data_layer.BirthdayRepository
import com.noam.happybirthday.remote.WebSocketListenerEvent
import com.noam.happybirthday.ui.model.AgeTextType
import com.noam.happybirthday.ui.model.BabyImageState
import com.noam.happybirthday.ui.model.BirthdayUiState
import com.noam.happybirthday.ui.model.DateOfBirthData
import com.noam.happybirthday.ui.model.HappyBirthdayThemeData
import com.noam.happybirthday.utils.ConnectionState
import com.noam.happybirthday.ui.view.Navigator
import com.noam.happybirthday.ui.view.Screens
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BirthdayViewModel(private val repository: BirthdayRepository, private val imageRepository: BabyImageRepository, private val navigator: Navigator) : ViewModel() {

    private val _connectionState: MutableStateFlow<ConnectionState> = MutableStateFlow(ConnectionState.DISCONNECTED)
    val connectionState: StateFlow<ConnectionState>
        get() = _connectionState

    private val _uiState = MutableStateFlow(BirthdayUiState())
    val uiState : StateFlow<BirthdayUiState>
        get() =  _uiState.asStateFlow()

    private val _babyImageState = MutableStateFlow(BabyImageState(null))
    val babyImageState : StateFlow<BabyImageState>
        get() =  _babyImageState.asStateFlow()

    private val _showDialogErrorState = MutableStateFlow( false)
    val showDialogErrorState: StateFlow<Boolean>
        get() = _showDialogErrorState.asStateFlow()

    init {
        viewModelScope.launch {
            imageRepository.latestBabyImageFlow.collect { babyImage ->
                Log.d("TAG", "connectToServer: just collected the next baby image = $babyImage")
                val babyImageState = _babyImageState.value.copy(image = babyImage)
                _babyImageState.emit(babyImageState)
            }
        }
    }

    fun connectToServer(ipAddress: String) {
        viewModelScope.launch {
            repository.webSocketFlow(ipAddress).distinctUntilChanged().collect { event ->
                when(event) {
                    is WebSocketListenerEvent.Connecting -> { _connectionState.value = ConnectionState.CONNECTING }
                    is WebSocketListenerEvent.Connected -> { _connectionState.value = ConnectionState.CONNECTED }
                    is WebSocketListenerEvent.Disconnected -> { _connectionState.value = ConnectionState.DISCONNECTED }
                    is WebSocketListenerEvent.Error -> {
                        _connectionState.value = ConnectionState.ERROR
                        onShowDialog()
                    }
                    is WebSocketListenerEvent.MessageReceived -> {
                        val birthdayWish = event.birthdayWish
                        Log.d("TAG", "connectToServer: just collected the next birthday wish = $birthdayWish")
//                        while (birthdayWish.dob.years > 12) {
//                            Log.d("Ages", "the birthday wish is too old (${birthdayWish.dob.years} > 12), so we are going to subtract 1 year from it")
//                            birthdayWish.dob.minusYears(1)
//                        }
                        val dateOfBirthData = if (birthdayWish.dob.years > 0) {
                            DateOfBirthData(
                                ageTextType = if (birthdayWish.dob.years == 1) AgeTextType.YEAR else AgeTextType.YEARS,
                                numberOfAgeDrawable = DateOfBirthData.getAgeDrawable(birthdayWish.dob.years)
                            )
                        } else {
                            DateOfBirthData(
                                ageTextType = if (birthdayWish.dob.months == 1) AgeTextType.MONTH else AgeTextType.MONTHS,
                                numberOfAgeDrawable = DateOfBirthData.getAgeDrawable(birthdayWish.dob.months)
                            )
                        }
                        val uiStateCopy = _uiState.value.copy(themeData = HappyBirthdayThemeData.getThemeObject(birthdayWish.theme), name = birthdayWish.name, dateOfBirthData = dateOfBirthData)
                        _uiState.emit(
                            uiStateCopy
                        )
                        navigator.navigate(Screens.HappyBirthday)
                    }
                }

            }
        }
    }

    // call this when you want to show the dialog
    private fun onShowDialog() {
        _showDialogErrorState.update { true }
    }

    fun onDismiss() {
        _showDialogErrorState.update { false }
        _connectionState.value = ConnectionState.DISCONNECTED
    }

    override fun onCleared() {
        Log.d("TAG", "onCleared: ViewModel is being cleared")
        super.onCleared()
        repository.disconnectFromServer()
    }
}