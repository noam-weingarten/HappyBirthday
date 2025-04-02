package com.noam.happybirthday.view_model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noam.happybirthday.data_layer.BabyImageRepository
import com.noam.happybirthday.data_layer.BirthdayRepository
import com.noam.happybirthday.ui.model.AgeTextType
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
import kotlinx.coroutines.launch

class BirthdayViewModel(private val repository: BirthdayRepository, private val imageRepository: BabyImageRepository, private val navigator: Navigator) : ViewModel() {

    var connectionState : StateFlow<ConnectionState> = repository.connectionState

    private val _uiState = MutableStateFlow(BirthdayUiState())
    val uiState : StateFlow<BirthdayUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            imageRepository.latestBabyImageFlow.collect { babyImage ->
                Log.d("TAG", "connectToServer: just collected the next baby image = $babyImage")
                val uiState = _uiState.value.copy(babyImage = babyImage)
                _uiState.emit(uiState)
            }
        }
    }

    fun connectToServer(ipAddress: String) {
        viewModelScope.launch {
            repository.connectToServer(ipAddress)

            repository.latestBirthdayWish.distinctUntilChanged().collect { it ->
                Log.d("TAG", "connectToServer: just collected the next birthday wish = $it")
//                while (it.dob.years > 12) {
//                    Log.d("Ages", "the birthday wish is too old (${it.dob.years} > 12), so we are going to subtract 1 year from it")
//                    it.dob.minusYears(1)
//                }
                val dateOfBirthData = if (it.dob.years > 0) {
                    DateOfBirthData(
                        ageTextType = if (it.dob.years == 1) AgeTextType.YEAR else AgeTextType.YEARS,
                        numberOfAgeDrawable = DateOfBirthData.getAgeDrawable(it.dob.years)
                    )
                } else {
                    DateOfBirthData(
                        ageTextType = if (it.dob.months == 1) AgeTextType.MONTH else AgeTextType.MONTHS,
                        numberOfAgeDrawable = DateOfBirthData.getAgeDrawable(it.dob.months)
                    )
                }
                val uiStateCopy = _uiState.value.copy(themeData = HappyBirthdayThemeData.getThemeObject(it.theme), name = it.name, dateOfBirthData = dateOfBirthData)
                _uiState.emit(
                    uiStateCopy
                )
                navigator.navigate(Screens.HappyBirthday)
            }
        }
    }

    override fun onCleared() {
        Log.d("TAG", "onCleared: ViewModel is being cleared")
        super.onCleared()
        repository.disconnectFromServer()
    }
}