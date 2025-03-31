package com.noam.happybirthday.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.noam.happybirthday.MainActivity.Companion.DEFAULT_IP_ADDRESS
import com.noam.happybirthday.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, setUpConnection: (String) -> Unit) {
    Scaffold(topBar = {
        TopAppBar(
            modifier = Modifier,
            title = { Text(text = stringResource(R.string.happybirthday)) },
            actions = {
                IconButton(onClick = { /* Handle settings */ }) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings")
                }
            }
        )
    },modifier = Modifier.fillMaxSize()) { innerPadding ->
        IpInput(
            hint = "Android",
            modifier = Modifier.padding(innerPadding),
            setUpConnection = setUpConnection
        )
    }
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
            label = { Text (stringResource(R.string.enter_ip_address_and_port)) },
            minLines = 3,
            maxLines = 3,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(16.dp)
        )
        Button(
            onClick = { setUpConnection(ipUrl.ifEmpty { DEFAULT_IP_ADDRESS }) },
            modifier = Modifier.padding(8.dp)
        )
        {
            Text(stringResource(R.string.connect))
        }
    }
}