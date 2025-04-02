package com.noam.happybirthday.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.noam.happybirthday.MainActivity.Companion.DEFAULT_IP_ADDRESS
import com.noam.happybirthday.R
import com.noam.happybirthday.utils.ConnectionState
import com.noam.happybirthday.view_model.BirthdayViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(setUpConnection: (String) -> Unit, viewModel: BirthdayViewModel) {
    val connectionState by viewModel.connectionState.collectAsState()
    val showDialog = remember { mutableStateOf(false) }
    if (connectionState == ConnectionState.ERROR) {
        showDialog.value = true
    }
    Scaffold(topBar = {
        TopAppBar(
            modifier = Modifier,
            title = { Text(text = stringResource(R.string.happy_birthday)) },
            actions = {
                IconButton(onClick = { /* Handle settings */ }) {
                    Icon(
                        when (connectionState) {
                            ConnectionState.CONNECTED -> Icons.Filled.CheckCircle
                            ConnectionState.DISCONNECTED -> Icons.Filled.Clear
                            ConnectionState.CONNECTING -> Icons.Filled.Refresh
                            ConnectionState.ERROR -> Icons.Filled.Warning
                        }, contentDescription = "ConnectionState")
                }
            }
        )
    },modifier = Modifier.fillMaxSize()) { innerPadding ->
        IpInput(
            modifier = Modifier.padding(innerPadding),
            setUpConnection = setUpConnection,
            viewModel
        )
        if (showDialog.value) {
            AlertDialog(
                onDismissRequest = { showDialog.value = false },
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .width(IntrinsicSize.Max)
                    .height(
                        IntrinsicSize.Min
                    )
                    .padding(16.dp)
                    .background(color = colorResource(R.color.white)),
                properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = false),
                confirmButton = { Button(onClick = { showDialog.value = false }) { Text("OK") }},
                dismissButton = {},
                icon = {
                    Icon(imageVector = Icons.Default.Warning, contentDescription = "Warning Icon" )
                },
                title = {
                    Text(text = stringResource(R.string.error_connecting), color = Color.Black)
                },
                text = {
                    Text(text = stringResource(R.string.there_was_an_error_attempting_to_connect_to_websocket_check_the_ip_entered_is_correct_and_try_again), color = Color.DarkGray)
                },
                containerColor = Color.White,
                iconContentColor = Color.Red,
                tonalElevation = 8.dp,
            )
        }
    }
}

@Composable
fun IpInput(
    modifier: Modifier = Modifier,
    setUpConnection: (String) -> Unit,
    viewModel: BirthdayViewModel
) {
    val connectionState by viewModel.connectionState.collectAsState()
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
            modifier = Modifier.padding(8.dp),
            enabled = !(connectionState == ConnectionState.CONNECTED || connectionState == ConnectionState.CONNECTING),
        )
        {
            Text(stringResource(R.string.connect))
        }
//        if (connectionState == ConnectionState.CONNECTING) {
//            Text(
//                text = stringResource(R.string.connecting),
//                modifier = Modifier
//                    .fillMaxWidth().align(Alignment.CenterHorizontally)
//                    .wrapContentHeight()
//                    .padding(16.dp),
//                color = Color.Gray
//            )
//        }
    }
}