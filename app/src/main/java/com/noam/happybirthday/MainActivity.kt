package com.noam.happybirthday

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.noam.happybirthday.ui.theme.HappyBirthdayTheme
import com.noam.happybirthday.view.HappyBirthday
import com.noam.happybirthday.view.HomeScreen
import com.noam.happybirthday.view.ImageSelectionScreen
import com.noam.happybirthday.view.Navigator
import com.noam.happybirthday.view.Screens
import com.noam.happybirthday.view_model.BirthdayViewModel
import com.noam.happybirthday.view_model.ImageViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : ComponentActivity() {

    private val birthdayViewModel: BirthdayViewModel by viewModel()
    private val imageViewModel: ImageViewModel by viewModel()
    private val navigator: Navigator by inject<Navigator>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HappyBirthdayTheme {
                val navController = rememberNavController()
                val destination by navigator.destination.collectAsState()
                LaunchedEffect(destination) {
                    if (navController.currentDestination?.route != destination.route) {
                        navController.navigate(destination.route)
                    }
                }
                NavHost(navController = navController, startDestination = navigator.destination.collectAsState().value.route) {
                    composable(route = Screens.HomeScreen.route){ HomeScreen(::setUpConnection, birthdayViewModel) }
                    composable(route = Screens.HappyBirthday.route){ HappyBirthday(navController, birthdayViewModel) }
                    composable(route = Screens.LoadImage.route){ ImageSelectionScreen(navController, viewModel = imageViewModel) }
                }
            }
        }
    }

    fun setUpConnection(url: String) {
        hideKeyboard()
        birthdayViewModel.connectToServer(url)
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    companion object {
        const val DEFAULT_IP_ADDRESS = "10.0.0.1:8080"
    }
}