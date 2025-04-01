package com.noam.happybirthday.view

interface NavigationDestination {
    val route: String
}

sealed class Screens(override val route: String): NavigationDestination{
    object HomeScreen : Screens("HomeScreen")
    object HappyBirthday : Screens("HappyBirthdayScreen")
    object LoadImage : Screens("LoadImageScreen")
}