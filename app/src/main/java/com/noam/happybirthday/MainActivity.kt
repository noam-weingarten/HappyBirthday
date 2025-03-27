package com.noam.happybirthday

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.noam.happybirthday.MainActivity.Companion.DEFAULT_IP_ADDRESS
import com.noam.happybirthday.ui.theme.HappyBirthdayTheme
import com.noam.happybirthday.view_model.BirthdayViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val viewModel: BirthdayViewModel by viewModel()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HappyBirthdayTheme {
                Scaffold(contentWindowInsets = WindowInsets.systemBars, topBar = {
                    TopAppBar(
                        modifier = Modifier,
                        title = { Text(text = "HappyBirthday") },
                        actions = {
                            IconButton(onClick = { /* Handle settings */ }) {
                                Icon(Icons.Default.Settings, contentDescription = "Settings")
                            }
                        }
                    )
                },modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.systemBars)) { innerPadding ->
                    IpInput(
                        hint = "Android",
                        modifier = Modifier.padding(innerPadding),
                        setUpConnection = ::setUpConnection
                    )
                }
            }
        }
    }
    fun setUpConnection(url: String) {
        viewModel.connectToServer(url)
    }

    companion object {
        const val DEFAULT_IP_ADDRESS = "10.0.0.1:8080"
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Composable
fun IpInput(hint: String, modifier: Modifier = Modifier, setUpConnection: (String) -> Unit) {
    var ipUrl by remember { mutableStateOf("") }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = ipUrl.ifEmpty { DEFAULT_IP_ADDRESS },
            onValueChange = { ipUrl = it },
            label = { Text ("Enter IP") },
            minLines = 3,
            maxLines = 3,
            modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(16.dp)
        )
        Button(
            onClick = { setUpConnection(ipUrl.ifEmpty { DEFAULT_IP_ADDRESS }) },
            modifier = Modifier.padding(8.dp)
        )
        {
            Text("connect")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    HappyBirthdayTheme {
        Greeting("Android")
        IpInput("", setUpConnection = {})
    }
}