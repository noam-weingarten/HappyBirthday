package com.noam.happybirthday.view

import kotlinx.coroutines.flow.MutableStateFlow

class Navigator {
    var destination: MutableStateFlow<NavigationDestination> = MutableStateFlow(Screens.HomeScreen)

    fun navigate(destination: NavigationDestination) {
        this.destination.value = destination
    }
}