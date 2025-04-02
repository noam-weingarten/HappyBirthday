package com.noam.happybirthday.ui.view

import kotlinx.coroutines.flow.MutableStateFlow

class Navigator {
    var destination: MutableStateFlow<NavigationDestination> = MutableStateFlow(Screens.HomeScreen)

    fun navigate(destination: NavigationDestination) {
        this.destination.value = destination
    }
}