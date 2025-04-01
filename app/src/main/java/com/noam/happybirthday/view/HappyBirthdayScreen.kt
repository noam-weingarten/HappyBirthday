package com.noam.happybirthday.view

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.noam.happybirthday.R
import com.noam.happybirthday.ui.model.AgeTextType
import com.noam.happybirthday.ui.theme.HappyBirthdayTheme
import com.noam.happybirthday.utils.circularMeasurePolicy
import com.noam.happybirthday.view_model.BirthdayViewModel

@Composable
fun HappyBirthday(navController: NavController, viewModel: BirthdayViewModel) {
    val birthdayUiState by viewModel.uiState.collectAsState()
    val bitmap = remember {
        mutableStateOf<ImageBitmap?>(null)
    }
    if (birthdayUiState.babyImage.height > 1 && birthdayUiState.babyImage.width > 1) {
        bitmap.value = birthdayUiState.babyImage
    }

    Scaffold(modifier = Modifier
        .fillMaxSize()) { innerPadding ->
        ConstraintLayout(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .background(color = colorResource(birthdayUiState.themeData.backgroundColor))) {
            val (backgroundImage, titleAndAge, babyImageAndLogo, logo) = createRefs()
            TitleAndAgeText(
                modifier = Modifier.constrainAs(titleAndAge) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top, margin = 20.dp)
                    bottom.linkTo(babyImageAndLogo.top, margin = 15.dp)
                },
                name = birthdayUiState.name,
                ageText = birthdayUiState.dateOfBirthData.ageTextType.value,
                ageDrawable = birthdayUiState.dateOfBirthData.numberOfAgeDrawable
            )
            BabyImageWithCameraAt45Angle(
                modifier = Modifier.constrainAs(babyImageAndLogo) {
                    start.linkTo(parent.start, margin = 50.dp)
                    end.linkTo(parent.end, margin = 50.dp)
                    bottom.linkTo(logo.top, margin = 15.dp)
                },
                babyImage = bitmap.value,
                borderDrawableRes = birthdayUiState.themeData.babyCircleBorderDrawable,
                centerDrawableRes = birthdayUiState.themeData.babyCircleDrawable,
                cameraDrawableRes = birthdayUiState.themeData.cameraDrawable,
                cameraDrawableOnClick = {
                    Log.d("Camera", "Clicked on the camera button")
                    navController.navigate(Screens.LoadImage.route)
                }

            )
            Image(
                painterResource(id = birthdayUiState.themeData.backgroundDrawable),
                contentDescription = "",
                contentScale = ContentScale.FillBounds, // or some other scale
                modifier = Modifier
                    .constrainAs(backgroundImage) {
                        width = Dimension.matchParent
                        height = Dimension.matchParent
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .layoutId("backgroundImage")
            )
            Image(
                painterResource(id = R.drawable.nanit),
                contentDescription = "",
                modifier = Modifier
                    .constrainAs(logo) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom, margin = 140.dp)
                    }
                    .layoutId("logo"))
        }
    }
}

@Composable
fun HappyBirthdayConstraintLayoutFinal(navController: NavController) {
    Scaffold(contentWindowInsets = WindowInsets.systemBars ,modifier = Modifier
        .fillMaxSize()
        .windowInsetsPadding(WindowInsets.systemBars)) { innerPadding ->
        ConstraintLayout(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .background(color = colorResource(R.color.pelican_blue))) {
            val (backgroundImage, titleAndAge, babyImageAndLogo, logo) = createRefs()
            TitleAndAgeText(
                modifier = Modifier.constrainAs(titleAndAge) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top, margin = 20.dp)
                    bottom.linkTo(babyImageAndLogo.top, margin = 15.dp)
                }
            )
            BabyImageWithCameraAt45Angle(
                modifier = Modifier.constrainAs(babyImageAndLogo) {
                    start.linkTo(parent.start, margin = 50.dp)
                    end.linkTo(parent.end, margin = 50.dp)
                    bottom.linkTo(logo.top, margin = 15.dp)
                }
            )
            Image(
                painterResource(id = R.drawable.bg_pelican),
                contentDescription = "",
                contentScale = ContentScale.FillBounds, // or some other scale
                modifier = Modifier
                    .constrainAs(backgroundImage) {
                        width = Dimension.matchParent
                        height = Dimension.matchParent
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .layoutId("backgroundImage")
            )
            Image(
                painterResource(id = R.drawable.nanit),
                contentDescription = "",
                modifier = Modifier
                    .constrainAs(logo) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom, margin = 140.dp)
//                    linkTo(top = babyImageAndLogo.bottom, bottom = parent.bottom, bottomMargin = 140.dp, bias = 1f)
                    }
                    .layoutId("logo"))
        }
    }
}

@Composable
fun TextTitle(modifier: Modifier, fontSize: TextUnit = 21.sp, text: String = "") {
    Row(modifier = modifier
        .width(IntrinsicSize.Max)
        .height(IntrinsicSize.Max),
        verticalAlignment = Alignment.CenterVertically) {
        Text(modifier = Modifier.weight(0.5f),
            fontSize = fontSize,
            fontFamily = FontFamily.SansSerif,
            fontStyle = FontStyle.Normal,
            fontWeight = FontWeight.Bold,
            color = colorResource(R.color.text_color),
            textAlign = TextAlign.Center,
            text = text.uppercase(),
            maxLines = 2)
    }
}

@Composable
fun AgeSquare(modifier: Modifier, @DrawableRes ageDrawable: Int) {
    Row(modifier = modifier
        .width(IntrinsicSize.Max)
        .height(IntrinsicSize.Max),
        verticalAlignment = Alignment.CenterVertically) {
        Image(
            painterResource(R.drawable.left_swirls),
            contentDescription = "",
            contentScale = ContentScale.FillBounds, // or some other scale
            modifier = Modifier.layoutId("left_swirls")
        )
        Image(
            painterResource(ageDrawable),
            contentDescription = "",
            contentScale = ContentScale.FillBounds, // or some other scale
            modifier = Modifier
                .weight(1f)
                .padding(start = 22.dp, end = 22.dp)
        )
        Image(
            painterResource(R.drawable.right_swirls),
            contentDescription = "",
            contentScale = ContentScale.FillBounds, // or some other scale
            modifier = Modifier.layoutId("right_swirls"),
        )
    }
}

@Composable
fun AgeSquare(modifier: Modifier) {
    Row(modifier = modifier
        .width(IntrinsicSize.Max)
        .height(IntrinsicSize.Max),
        verticalAlignment = Alignment.CenterVertically) {
        Image(
            painterResource(R.drawable.left_swirls),
            contentDescription = "",
            contentScale = ContentScale.FillBounds, // or some other scale
            modifier = Modifier.layoutId("left_swirls")
        )
        Image(
            painterResource(R.drawable.age_1),
            contentDescription = "",
            contentScale = ContentScale.FillBounds, // or some other scale
            modifier = Modifier
                .weight(1f)
                .padding(start = 22.dp, end = 22.dp)
        )
        Image(
            painterResource(R.drawable.right_swirls),
            contentDescription = "",
            contentScale = ContentScale.FillBounds, // or some other scale
            modifier = Modifier.layoutId("right_swirls"),
        )
    }
}

@Composable
fun TitleAndAgeText(modifier: Modifier, name: String, ageText: String, @DrawableRes ageDrawable: Int) {
    ConstraintLayout(modifier = modifier.fillMaxSize()) {
        val (title, ageSquare, timeTitle) = createRefs()
        TextTitle(
            text = stringResource(R.string.name_today_is, name),
            fontSize = 21.sp,
            modifier = Modifier
                .constrainAs(title) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(ageSquare.top)
                }
                .padding(bottom = 13.dp, start = 50.dp, end = 50.dp)
        )
        AgeSquare(modifier = Modifier
            .constrainAs(ageSquare) {
                top.linkTo(title.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(timeTitle.top)
            }.padding(bottom = 14.dp),
            ageDrawable = ageDrawable
        )
        TextTitle(
            text = stringResource(R.string.age_old, ageText),
            fontSize = 18.sp,
            modifier = Modifier.constrainAs(timeTitle) {
                top.linkTo(ageSquare.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
            })
        createVerticalChain(title, ageSquare, timeTitle, chainStyle = ChainStyle.Packed)
    }
}

@Composable
fun TitleAndAgeText(modifier: Modifier) {
    ConstraintLayout(modifier = modifier.fillMaxSize()) {
        val (title, ageSquare, timeTitle) = createRefs()
        TextTitle(
            text = "Today Cristiano Ronaldo is",
            fontSize = 21.sp,
            modifier = Modifier
                .constrainAs(title) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(ageSquare.top)
                }
                .padding(bottom = 13.dp, start = 50.dp, end = 50.dp)
        )
        AgeSquare(modifier = Modifier
            .constrainAs(ageSquare) {
                top.linkTo(title.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(timeTitle.top)
            }
            .padding(bottom = 14.dp)
        )
        TextTitle(
            text = "Month Old!",
            fontSize = 18.sp,
            modifier = Modifier.constrainAs(timeTitle) {
                top.linkTo(ageSquare.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
            })
            createVerticalChain(title, ageSquare, timeTitle, chainStyle = ChainStyle.Packed)
    }
}

@Composable
fun BabyImageWithCameraAt45Angle(modifier: Modifier, babyImage: ImageBitmap?, @DrawableRes borderDrawableRes: Int, @DrawableRes centerDrawableRes: Int, @DrawableRes cameraDrawableRes: Int, cameraDrawableOnClick: () -> Unit) {
    Circular(
        modifier = modifier,
        overrideRadius = null,
        startAngle = { 45f },
        center = {
            if (babyImage != null) {
                Box(
                    modifier = modifier
                        .clip(CircleShape)
                        .paint(painterResource(borderDrawableRes))
                ) {
                    Image(
                        modifier = Modifier.size(200.dp)
                            .clip(CircleShape).align(Alignment.Center),
                        bitmap = babyImage,
                        contentScale = ContentScale.Crop,
                        contentDescription = null
                    )
                }
            } else {
                Image(
                    painterResource(id = centerDrawableRes),
                    contentDescription = "",
                    contentScale = ContentScale.FillBounds, // or some other scale
                    modifier = Modifier
                        .height(IntrinsicSize.Max)
                        .width(IntrinsicSize.Max)
                )
            }
        }
    ) {
        Image(
            painterResource(id = cameraDrawableRes),
            contentDescription = "",
            contentScale = ContentScale.FillBounds, // or some other scale
            modifier = Modifier.clickable {
                Log.d("Camera", "Clicked on the camera button")
                cameraDrawableOnClick()
            }
        )
    }
}

@Composable
fun BabyImageWithCameraAt45Angle(modifier: Modifier) {
    Circular(
        modifier = modifier,
        overrideRadius = null,
        startAngle = { 45f },
        center = {
            Image(
                painterResource(id = R.drawable.blue_baby_circle),
                contentDescription = "",
                contentScale = ContentScale.FillBounds, // or some other scale
                modifier = Modifier
                    .height(IntrinsicSize.Max)
                    .width(IntrinsicSize.Max)
            )
        }
    ) {
        Image(
            painterResource(id = R.drawable.blue_camera),
            contentDescription = "",
            contentScale = ContentScale.FillBounds, // or some other scale
            modifier = Modifier.clickable { Log.d("Camera", "Clicked on the camera button") }
        )
    }
}

@Composable
fun Circular(
    modifier: Modifier = Modifier,
    overrideRadius: (() -> Dp)? = null,
    startAngle: () -> Float = { 45f },
    center: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    Layout(
        measurePolicy = circularMeasurePolicy(
            overrideRadius,
            startAngle
        ),
        contents = listOf(center, content),
        modifier = modifier,
    )
}

@Preview(showBackground = true)
@Composable
fun HappyBirthdayPreview() {
    val navController = rememberNavController()
    HappyBirthdayTheme {
        HappyBirthdayConstraintLayoutFinal(navController)
    }
}