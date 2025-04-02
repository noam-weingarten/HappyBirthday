package com.noam.happybirthday.ui.view

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.noam.happybirthday.R
import com.noam.happybirthday.data_layer.BabyImageRepositoryImpl
import com.noam.happybirthday.ui.model.ImageIntent
import com.noam.happybirthday.ui.model.ImageViewState
import com.noam.happybirthday.ui.theme.HappyBirthdayTheme
import com.noam.happybirthday.view_model.ImageViewModel
import kotlinx.coroutines.Dispatchers


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageSelectionScreen(navController: NavController, modifier: Modifier = Modifier, viewModel: ImageViewModel) {
    val viewState: ImageViewState by viewModel.viewStateFlow.collectAsState()
    val currentContext = LocalContext.current

    val pickImageFromAlbumLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { url ->
            viewModel.onReceive(ImageIntent.OnFinishPickingImageWith(currentContext, url))
        }

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { isImageSaved ->
            if (isImageSaved) {
                viewModel.onReceive(ImageIntent.OnImageSavedWith(currentContext))
            } else {
                viewModel.onReceive(ImageIntent.OnImageSavingCanceled)
            }
        }

    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { permissionGranted ->
            if (permissionGranted) {
                viewModel.onReceive(ImageIntent.OnPermissionGrantedWith(currentContext))
            } else {
                viewModel.onReceive(ImageIntent.OnPermissionDenied)
            }
        }

    LaunchedEffect(key1 = viewState.tempFileUrl) {
        viewState.tempFileUrl?.let {
            cameraLauncher.launch(it)
        }
    }

    BasicAlertDialog(
        onDismissRequest = { navController.navigate(Screens.HappyBirthday.route) },
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .width(IntrinsicSize.Max)
            .height(IntrinsicSize.Min)
            .padding(16.dp)
            .background(color = colorResource(R.color.white)),
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true),
    ) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .then(modifier),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Row {
                Button(onClick = { permissionLauncher.launch(Manifest.permission.CAMERA)  }) {
                    Text(text = stringResource(R.string.use_camera))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(onClick = {
                    val mediaRequest = PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    pickImageFromAlbumLauncher.launch(mediaRequest)  }) {
                    Text(text = stringResource(R.string.use_gallery))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = stringResource(R.string.selected_picture))
            Box(
                modifier = modifier
                    .clip(CircleShape)
                    .size(210.dp)
                    .background(Color.Gray)
                    .padding(10.dp)
            ) {
                Image(
                    modifier = Modifier
                        .size(200.dp)
                        .clip(CircleShape)
                        .align(Alignment.Center),
                    bitmap = viewState.selectedPicture,
                    contentScale = ContentScale.Crop,
                    contentDescription = null
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                navController.navigate(Screens.HappyBirthday.route)
            }) {
                Text(text = stringResource(R.string.looks_good))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ImageScreenPreview() {
    val navController = rememberNavController()
    val viewModel = ImageViewModel(coroutineContext = Dispatchers.Default, imageRepository = BabyImageRepositoryImpl(LocalContext.current))
    HappyBirthdayTheme {
        ImageSelectionScreen(navController = navController, viewModel = viewModel)
    }
}