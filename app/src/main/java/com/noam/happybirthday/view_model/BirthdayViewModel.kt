package com.noam.happybirthday.view_model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noam.happybirthday.data_layer.BirthdayRepository
import com.noam.happybirthday.ui.model.AgeTextType
import com.noam.happybirthday.ui.model.BirthdayUiState
import com.noam.happybirthday.ui.model.DateOfBirthData
import com.noam.happybirthday.ui.model.HappyBirthdayThemeData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class BirthdayViewModel(private val repository: BirthdayRepository) : ViewModel() {

    var birthdayWishObservable : MutableLiveData<BirthdayUiState> = MutableLiveData<BirthdayUiState>()

    private val _uiState = MutableStateFlow(BirthdayUiState())
    val uiState : StateFlow<BirthdayUiState> = _uiState.asStateFlow()

    fun connectToServer(ipAddress: String) {
        viewModelScope.launch {
            repository.connectToServer(ipAddress)

            repository.latestBirthdayWish.distinctUntilChanged().collect { it ->
                Log.d("TAG", "connectToServer: just collected the next birthday wish = $it")
                val dateOfBirthData = if (it.dob.years > 0) {
                    DateOfBirthData(
                        ageTextType = AgeTextType.YEARS,
                        numberOfAgeDrawable = DateOfBirthData.getAgeDrawable(it.dob.years)
                    )
                } else {
                    DateOfBirthData(
                        ageTextType = AgeTextType.MONTHS,
                        numberOfAgeDrawable = DateOfBirthData.getAgeDrawable(it.dob.months)
                    )
                }
                _uiState.emit(
                    BirthdayUiState(
                        themeData = HappyBirthdayThemeData.getThemeObject(it.theme),
                        name = it.name,
                        dateOfBirthData = dateOfBirthData,
                        babyImage = 0
                    )
                )
                birthdayWishObservable.postValue(
                    BirthdayUiState(
                        themeData = HappyBirthdayThemeData.getThemeObject(it.theme),
                        name = it.name,
                        dateOfBirthData = dateOfBirthData,
                        babyImage = 0
                    )
                )
            }
        }
    }

    override fun onCleared() {
        Log.d("TAG", "onCleared: ViewModel is being cleared")
        super.onCleared()
        repository.disconnectFromServer()
    }
}